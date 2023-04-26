package oghamcloudhopper.it;

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
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import fr.sii.ogham.testing.extension.junit.sms.JsmppServerExtension;
import fr.sii.ogham.testing.extension.junit.sms.SmppServerExtension;
import ogham.testing.org.jsmpp.bean.SubmitSm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoSettings;
import testutils.TrackClientAndSessionsDecorator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static fr.sii.ogham.testing.sms.simulator.bean.NumberingPlanIndicator.ISDN;
import static fr.sii.ogham.testing.sms.simulator.bean.TypeOfNumber.UNKNOWN;
import static java.util.Arrays.asList;
import static testutils.SessionStrategyTestHelper.*;

@LogTestInformation
@MockitoSettings
public class AlwaysNewSessionStrategyTest implements Supplier<TestContext> {
	private static final String RECIPIENT = "0203040506";
	private static final String SENDER = "+33203040506";

	@RegisterExtension public final SmppServerExtension<SubmitSm> smppServer = new JsmppServerExtension();

	private CloudhopperSMPPSender sender;
	private CloudhopperBuilder builder;
	
	@Spy SmppClient client1 = new DefaultSmppClient();
	@Spy SmppClient client2 = new DefaultSmppClient();
	List<SmppClient> clients;
	List<SmppSession> allSessions = new ArrayList<>();
	@Spy SmppClientSupplier supplier = new TrackClientAndSessionsDecorator(() -> clients, () -> allSessions);

	@BeforeEach
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
