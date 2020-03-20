package oghamcloudhopper.it;

import static fr.sii.ogham.testing.sms.simulator.bean.NumberingPlanIndicator.ISDN;
import static fr.sii.ogham.testing.sms.simulator.bean.TypeOfNumber.UNKNOWN;
import static java.util.Arrays.asList;
import static testutils.SessionStrategyTestHelper.cleaned;
import static testutils.SessionStrategyTestHelper.closed;
import static testutils.SessionStrategyTestHelper.opened;
import static testutils.SessionStrategyTestHelper.verifyThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.jsmpp.bean.SubmitSm;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.cloudhopper.smpp.SmppClient;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.impl.DefaultSmppClient;

import fr.sii.ogham.sms.builder.cloudhopper.CloudhopperBuilder;
import fr.sii.ogham.sms.builder.cloudhopper.SmppClientSupplier;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.sender.impl.CloudhopperSMPPSender;
import fr.sii.ogham.sms.sender.impl.cloudhopper.ExtendedSmppSessionConfiguration;
import fr.sii.ogham.testing.assertion.sms.AssertSms;
import fr.sii.ogham.testing.assertion.sms.ExpectedAddressedPhoneNumber;
import fr.sii.ogham.testing.assertion.sms.ExpectedSms;
import fr.sii.ogham.testing.extension.junit.JsmppServerRule;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import fr.sii.ogham.testing.extension.junit.SmppServerRule;
import testutils.SessionStrategyTestHelper.SessionAware;
import testutils.SessionStrategyTestHelper.TestContext;
import testutils.TrackClientAndSessionsDecorator;

public class AlwaysNewSessionStrategyTest implements Supplier<TestContext> {
	private static final String RECIPIENT = "0203040506";
	private static final String SENDER = "+33203040506";

	@Rule public final LoggingTestRule loggingRule = new LoggingTestRule();
	@Rule public final SmppServerRule<SubmitSm> smppServer = new JsmppServerRule();
	@Rule public final MockitoRule mockito = MockitoJUnit.rule();

	private CloudhopperSMPPSender sender;
	private CloudhopperBuilder builder;
	
	@Spy SmppClient client1 = new DefaultSmppClient();
	@Spy SmppClient client2 = new DefaultSmppClient();
	List<SmppClient> clients;
	List<SmppSession> allSessions = new ArrayList<>();
	@Spy SmppClientSupplier supplier = new TrackClientAndSessionsDecorator(() -> clients, () -> allSessions);

	@Before
	public void setup() throws IOException {
		clients = asList(client1, client2);
		ExtendedSmppSessionConfiguration configuration = new ExtendedSmppSessionConfiguration();
		configuration.setHost("127.0.0.1");
		configuration.setPort(smppServer.getPort());
		builder = new CloudhopperBuilder()
					.clientSupplier(supplier)
					.session(configuration)
					.session()
						.reuseSession()
							.enable(false)
							.and()
						.keepAlive()
							.enable(false)
							.and()
						.connectRetry()
							.fixedDelay()
								.maxRetries(10)
								.delay(500L)
								.and()
							.and()
						.and();
		sender = builder.build();
		// @formatter:on
	}

	
	@Test
	public void alwaysNewSession() throws Exception {
		// send one SMS
		sender.send(new Sms().content("sms content").from(SENDER).to(RECIPIENT));
		// send another SMS with same session
		sender.send(new Sms().content("sms content 2").from(SENDER).to(RECIPIENT));
		AssertSms.assertEquals(
				Arrays.asList(
						new ExpectedSms("sms content",
								new ExpectedAddressedPhoneNumber(SENDER, UNKNOWN, ISDN),
								new ExpectedAddressedPhoneNumber(RECIPIENT, UNKNOWN, ISDN)),
						new ExpectedSms("sms content 2", 
								new ExpectedAddressedPhoneNumber(SENDER, UNKNOWN, ISDN),
								new ExpectedAddressedPhoneNumber(RECIPIENT, UNKNOWN, ISDN))),
				smppServer.getReceivedMessages());
		
		// ensure that two sessions are opened
		verifyThat(this)
			.sessions(opened(2));
		// ensure that both sessions are closed
		verifyThat(this)
			.session(0, closed())
			.client(client1, cleaned())
			.session(1, closed())
			.client(client2, cleaned());
		
		// manual cleanup
		sender.clean();
		
		// ensure that manual cleanup should not clean again
		verifyThat(this)
			.session(0, closed())
			.client(client1, cleaned())
			.session(1, closed())
			.client(client2, cleaned());
	}
	
	
	@Override
	public TestContext get() {
		return new AlwaysNewSessionTestContext();
	}
	
	class AlwaysNewSessionTestContext implements TestContext, SessionAware {
		public List<SmppClient> getClients() {
			return clients;
		}
		public List<SmppSession> getSessions() {
			return allSessions;
		}
		public SmppClientSupplier getSupplier() {
			return supplier;
		}
	}
}
