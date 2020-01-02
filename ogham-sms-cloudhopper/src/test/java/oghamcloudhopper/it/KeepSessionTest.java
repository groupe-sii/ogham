package oghamcloudhopper.it;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsmpp.bean.SubmitSm;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.cloudhopper.smpp.SmppClient;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.SmppSessionHandler;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.type.SmppBindException;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.sms.builder.cloudhopper.CloudhopperBuilder;
import fr.sii.ogham.sms.builder.cloudhopper.SmppClientSupplier;
import fr.sii.ogham.sms.message.Sender;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.message.addressing.NumberingPlanIndicator;
import fr.sii.ogham.sms.message.addressing.TypeOfNumber;
import fr.sii.ogham.sms.sender.impl.CloudhopperSMPPSender;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import fr.sii.ogham.testing.helper.sms.AssertSms;
import fr.sii.ogham.testing.helper.sms.ExpectedAddressedPhoneNumber;
import fr.sii.ogham.testing.helper.sms.ExpectedSms;
import fr.sii.ogham.testing.helper.sms.rule.JsmppServerRule;
import fr.sii.ogham.testing.helper.sms.rule.SmppServerRule;

public class KeepSessionTest {
	private static final String NATIONAL_PHONE_NUMBER = "0203040506";
	private static final String INTERNATIONAL_PHONE_NUMBER = "+33203040506";

	@Rule public final LoggingTestRule loggingRule = new LoggingTestRule();
	@Rule public final SmppServerRule<SubmitSm> smppServer = new JsmppServerRule();
	@Rule public final MockitoRule mockito = MockitoJUnit.rule();

	private CloudhopperSMPPSender sender;
	private CloudhopperBuilder builder;
	
	@Spy SmppClient client1 = new DefaultSmppClient();
	@Spy SmppClient client2 = new DefaultSmppClient();
	@Spy List<SmppSession> allSessions = new ArrayList<>();
	@Spy SmppClientSupplier supplier = new SmppClientSupplier() {
		int idx = 0;
		@Override
		public SmppClient get() {
			return asList(new SmppClientSessionTrackerDecorator(client1, allSessions), new SmppClientSessionTrackerDecorator(client2, allSessions)).get(idx++);
		}
	};


	@Before
	public void setUp() throws IOException {
		SmppSessionConfiguration configuration = new SmppSessionConfiguration();
		configuration.setHost("127.0.0.1");
		configuration.setPort(smppServer.getPort());
		builder = new CloudhopperBuilder()
					.clientSupplier(supplier)
					.smppSessionHandlerSupplier(() -> null)
					.session(configuration)
					.session()
						.connectRetry()
							.fixedDelay()
								.maxRetries(10)
								.delay(500L)
								.and()
							.and()
						.and();
		// @formatter:on
	}

	@Test
	public void keepSession() throws MessagingException, IOException, SmppBindException, SmppTimeoutException, SmppChannelException, UnrecoverablePduException, InterruptedException {
		// @formatter:off
		sender = builder
					.session()
						.keepSession(true)
						.and()
					.build();
		// @formatter:on
		// send one SMS
		sender.send(new Sms().content("sms content").from(new Sender(INTERNATIONAL_PHONE_NUMBER)).to(NATIONAL_PHONE_NUMBER));
		// send another SMS with same session
		sender.send(new Sms().content("sms content 2").from(new Sender(INTERNATIONAL_PHONE_NUMBER)).to(NATIONAL_PHONE_NUMBER));
		AssertSms.assertEquals(
				Arrays.asList(
						new ExpectedSms("sms content",
								new ExpectedAddressedPhoneNumber(INTERNATIONAL_PHONE_NUMBER, TypeOfNumber.UNKNOWN.value(), NumberingPlanIndicator.ISDN_TELEPHONE.value()),
								new ExpectedAddressedPhoneNumber(NATIONAL_PHONE_NUMBER, TypeOfNumber.UNKNOWN.value(), NumberingPlanIndicator.ISDN_TELEPHONE.value())),
						new ExpectedSms("sms content 2", 
								new ExpectedAddressedPhoneNumber(INTERNATIONAL_PHONE_NUMBER, TypeOfNumber.UNKNOWN.value(), NumberingPlanIndicator.ISDN_TELEPHONE.value()),
								new ExpectedAddressedPhoneNumber(NATIONAL_PHONE_NUMBER, TypeOfNumber.UNKNOWN.value(), NumberingPlanIndicator.ISDN_TELEPHONE.value()))),
				smppServer.getReceivedMessages());
		// ensure that connection has been done only one time
		verify(client1, times(1)).bind(any(SmppSessionConfiguration.class), isNull());
		verify(client2, never()).bind(any(SmppSessionConfiguration.class), isNull());
		verify(supplier, times(1)).get();
		assertThat("one session should be created", allSessions, hasSize(1));
		for (SmppSession session : allSessions) {
			verify(session, never()).unbind(anyLong());
			verify(session, never()).close();
			verify(session, never()).destroy();
		}
		verify(client1, never()).destroy();
		verify(client2, never()).destroy();
		// manual cleanup
		sender.clean();
		for (SmppSession session : allSessions) {
			verify(session, times(1)).unbind(anyLong());
			verify(session, times(1)).close();
			verify(session, times(1)).destroy();
		}
		verify(client1, times(1)).destroy();
		verify(client2, never()).destroy();
	}

	@Test
	public void alwaysNewSession() throws MessagingException, IOException, SmppBindException, SmppTimeoutException, SmppChannelException, UnrecoverablePduException, InterruptedException {
		// @formatter:off
		sender = builder
					.session()
						.keepSession(false)
						.and()
					.build();
		// @formatter:on
		// send one SMS
		sender.send(new Sms().content("sms content").from(new Sender(INTERNATIONAL_PHONE_NUMBER)).to(NATIONAL_PHONE_NUMBER));
		// send another SMS with same session
		sender.send(new Sms().content("sms content 2").from(new Sender(INTERNATIONAL_PHONE_NUMBER)).to(NATIONAL_PHONE_NUMBER));
		AssertSms.assertEquals(
				Arrays.asList(
						new ExpectedSms("sms content",
								new ExpectedAddressedPhoneNumber(INTERNATIONAL_PHONE_NUMBER, TypeOfNumber.UNKNOWN.value(), NumberingPlanIndicator.ISDN_TELEPHONE.value()),
								new ExpectedAddressedPhoneNumber(NATIONAL_PHONE_NUMBER, TypeOfNumber.UNKNOWN.value(), NumberingPlanIndicator.ISDN_TELEPHONE.value())),
						new ExpectedSms("sms content 2", 
								new ExpectedAddressedPhoneNumber(INTERNATIONAL_PHONE_NUMBER, TypeOfNumber.UNKNOWN.value(), NumberingPlanIndicator.ISDN_TELEPHONE.value()),
								new ExpectedAddressedPhoneNumber(NATIONAL_PHONE_NUMBER, TypeOfNumber.UNKNOWN.value(), NumberingPlanIndicator.ISDN_TELEPHONE.value()))),
				smppServer.getReceivedMessages());
		// ensure that connection has been done two times
		verify(client1, times(1)).bind(any(SmppSessionConfiguration.class), isNull());
		verify(client2, times(1)).bind(any(SmppSessionConfiguration.class), isNull());
		verify(supplier, times(2)).get();
		assertThat("two sessions should be created", allSessions, hasSize(2));
		for (SmppSession session : allSessions) {
			verify(session, times(1)).unbind(anyLong());
			verify(session, times(1)).close();
			verify(session, times(1)).destroy();
		}
		verify(client1, times(1)).destroy();
		verify(client2, times(1)).destroy();
		// manual cleanup should not clean again
		sender.clean();
		for (SmppSession session : allSessions) {
			verify(session, times(1)).unbind(anyLong());
			verify(session, times(1)).close();
			verify(session, times(1)).destroy();
		}
		verify(client1, times(1)).destroy();
		verify(client2, times(1)).destroy();
	}
	
	private static class SmppClientSessionTrackerDecorator implements SmppClient {
		private final SmppClient delegate;
		private final List<SmppSession> sessions;
		
		public SmppClientSessionTrackerDecorator(SmppClient delegate, List<SmppSession> sessions) {
			super();
			this.delegate = delegate;
			this.sessions = sessions;
		}

		@Override
		public SmppSession bind(SmppSessionConfiguration config, SmppSessionHandler sessionHandler) throws SmppTimeoutException, SmppChannelException, SmppBindException, UnrecoverablePduException, InterruptedException {
			SmppSession session = spy(delegate.bind(config, sessionHandler));
			sessions.add(session);
			return session;
		}

		@Override
		public void destroy() {
			delegate.destroy();
		}
		
	}
}
