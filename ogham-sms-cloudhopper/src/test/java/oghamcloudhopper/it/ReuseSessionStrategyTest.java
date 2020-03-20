package oghamcloudhopper.it;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasAnyCause;
import static fr.sii.ogham.testing.sms.simulator.bean.NumberingPlanIndicator.ISDN;
import static fr.sii.ogham.testing.sms.simulator.bean.TypeOfNumber.UNKNOWN;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;
import static testutils.SessionStrategyTestHelper.by;
import static testutils.SessionStrategyTestHelper.cleaned;
import static testutils.SessionStrategyTestHelper.closed;
import static testutils.SessionStrategyTestHelper.expirationOfLastRequest;
import static testutils.SessionStrategyTestHelper.isNull;
import static testutils.SessionStrategyTestHelper.not;
import static testutils.SessionStrategyTestHelper.opened;
import static testutils.SessionStrategyTestHelper.requests;
import static testutils.SessionStrategyTestHelper.sent;
import static testutils.SessionStrategyTestHelper.session;
import static testutils.SessionStrategyTestHelper.verifyThat;
import static testutils.SessionStrategyTestHelper.waitUntil;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.jsmpp.bean.SubmitSm;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudhopper.smpp.SmppClient;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.impl.DefaultSmppClient;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.sms.builder.cloudhopper.CloudhopperBuilder;
import fr.sii.ogham.sms.builder.cloudhopper.SmppClientSupplier;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.sender.impl.CloudhopperSMPPSender;
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.SmppException;
import fr.sii.ogham.testing.assertion.sms.AssertSms;
import fr.sii.ogham.testing.assertion.sms.ExpectedAddressedPhoneNumber;
import fr.sii.ogham.testing.assertion.sms.ExpectedSms;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import fr.sii.ogham.testing.extension.junit.sms.config.ServerConfig;
import fr.sii.ogham.testing.sms.simulator.SmppServerException;
import fr.sii.ogham.testing.sms.simulator.jsmpp.JSMPPServer;
import fr.sii.ogham.testing.sms.simulator.jsmpp.SubmitSmAdapter;
import testutils.SessionStrategyTestHelper.SessionAware;
import testutils.SessionStrategyTestHelper.TestContext;
import testutils.TrackClientAndSessionsDecorator;

public class ReuseSessionStrategyTest implements Supplier<TestContext> {
	private static final Logger LOG = LoggerFactory.getLogger(ReuseSessionStrategyTest.class);
	
	private static final String RECIPIENT = "0203040506";
	private static final String SENDER = "+33203040506";

	@Rule public final LoggingTestRule loggingRule = new LoggingTestRule();
	@Rule public final MockitoRule mockito = MockitoJUnit.rule();

	private CloudhopperSMPPSender sender;
	private CloudhopperBuilder builder;
	private JSMPPServer manualServer;
	
	@Spy SmppClient client1 = new DefaultSmppClient();
	@Spy SmppClient client2 = new DefaultSmppClient();
	List<SmppClient> clients;
	List<SmppSession> allSessions = new ArrayList<>();
	@Spy SmppClientSupplier supplier = new TrackClientAndSessionsDecorator(() -> clients, () -> allSessions);

	@Before
	public void setup() throws IOException {
		clients = asList(client1, client2);
		builder = new CloudhopperBuilder()
					.host("127.0.0.1")
					.clientSupplier(supplier)
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

	@After
	public void cleanup() throws SmppServerException {
		if (manualServer != null) {
			manualServer.stop();
		}
	}
	
	
	// @formatter:off
	/**
	 * 
	 * <pre>{@code
	 *  ┌─────────┐                                    ┌─────────┐
	 *  │ Client  │                                    │ Server  │
	 *  └────┬────┘                                    └────┬────┘
	 *       │                                              ├─┐ start() 
	 *       │                                              │<┘ 
	 *       │                                              │ 
	 *       │                                              │ 
	 *       ├─┐ send(Sms(sms content 1 / session 1))       │
	 *       │<┘                                            │
	 *       │ connect [session 1]                          │
	 *       ├─────────────────────────────────────────────>│
	 *       │                                    connected │ 
	 *       │<─────────────────────────────────────────────┤
	 *       │                                              │ 
	 *       │ send SubmitSm                                │ 
	 *       ├─────────────────────────────────────────────>│
	 *       │                                    response  │ 
	 *       │<─────────────────────────────────────────────┤
	 *       │                                              │ 
	 *       :                                              :
	 *       : wait for expiration of last request (200ms)  :                     (?) expiration is configured to 100ms (=> need to send EnquireLink)
	 *       :                                              :
	 *       │                                              │ 
	 *       │                                              │ 
	 *       ├─┐ send(Sms(sms content 2 / session 1))       │
	 *       │<┘                                            │
	 *       │ send EnquireLink                             │
	 *       ├─────────────────────────────────────────────>│
	 *       │                                    response  │ 
	 *       │<─────────────────────────────────────────────┤
	 *       │                                              │ 
	 *       │ send SubmitSm                                │ 
	 *       ├─────────────────────────────────────────────>│
	 *       │                                    response  │ 
	 *       │<─────────────────────────────────────────────┤
	 *       │                                              │ 
	 *       :                                              :
	 *       : wait for expiration of last request (200ms)  :                     (?) expiration is configured to 100ms (=> need to send EnquireLink)
	 *       :                                              :
	 *       │                                              │ 
	 *       │                                              │ 
	 *       ├─┐ send(Sms(sms content 3 / session 1))       │
	 *       │<┘                                            │
	 *       │ send EnquireLink                             │
	 *       ├─────────────────────────────────────────────>│
	 *       │                                    response  │ 
	 *       │<─────────────────────────────────────────────┤
	 *       │                                              │ 
	 *       │ send SubmitSm                                │ 
	 *       ├─────────────────────────────────────────────>│
	 *       │                                    response  │ 
	 *       │<─────────────────────────────────────────────┤
	 *       
	 * }</pre>
	 *   
	 */
	@Test
	@SuppressWarnings("javadoc")
	public void reuseSession() throws Exception {
		manualServer = new JSMPPServer(8057, new ServerConfig().build());
		manualServer.start();
		// @formatter:off
		sender = builder
					.port(8057)
					.session()
						.responseTimeout(200L)
						.reuseSession()
							.enable(true)
							.responseTimeout(100L)
							.lastInteractionExpiration(100L)
							.and()
						.and()
					.build();
		// @formatter:on
		// send one SMS
		sender.send(new Sms().content("sms content 1 / session 1").from(SENDER).to(RECIPIENT));
		waitUntil(expirationOfLastRequest(200));
		// send another SMS with same session
		sender.send(new Sms().content("sms content 2 / session 1").from(SENDER).to(RECIPIENT));
		waitUntil(expirationOfLastRequest(200));
		// send another SMS with same session
		sender.send(new Sms().content("sms content 3 / session 1").from(SENDER).to(RECIPIENT));
		AssertSms.assertEquals(
				Arrays.asList(
						new ExpectedSms("sms content 1 / session 1",
								new ExpectedAddressedPhoneNumber(SENDER, UNKNOWN, ISDN),
								new ExpectedAddressedPhoneNumber(RECIPIENT, UNKNOWN, ISDN)),
						new ExpectedSms("sms content 2 / session 1", 
								new ExpectedAddressedPhoneNumber(SENDER, UNKNOWN, ISDN),
								new ExpectedAddressedPhoneNumber(RECIPIENT, UNKNOWN, ISDN)),
						new ExpectedSms("sms content 3 / session 1", 
								new ExpectedAddressedPhoneNumber(SENDER, UNKNOWN, ISDN),
								new ExpectedAddressedPhoneNumber(RECIPIENT, UNKNOWN, ISDN))),
				convert(manualServer.getReceivedMessages()));

		// ensure that connection has been done only one time
		// and 2 EnquireLink requests have been sent
		verifyThat(this)
			.sessions(opened(1))
			.enquireLinks(sent(requests(2), by(session(0))));
		// ensure that session is still running
		verifyThat(this)
			.session(0, opened())
			.client(client1, not(cleaned()))
			.session(1, isNull())
			.client(client2, not(cleaned()));
		
		// manual cleanup
		sender.clean();
		
		// ensure that session is correctly closed
		verifyThat(this)
			.session(0, closed())
			.client(client1, cleaned())
			.session(1, isNull())
			.client(client2, not(cleaned()));
	}

	// @formatter:off
	/**
	 * 
	 * <pre>{@code
	 *  ┌─────────┐                                    ┌─────────┐
	 *  │ Client  │                                    │ Server  │
	 *  └────┬────┘                                    └────┬────┘
	 *       │                                              ├─┐ start() 
	 *       │                                              │<┘ 
	 *       │                                              │ 
	 *       │                                              │ 
	 *       ├─┐ send(Sms(sms content 1 / session 1))       │
	 *       │<┘                                            │
	 *       │ connect [session 1]                          │
	 *       ├─────────────────────────────────────────────>│
	 *       │                                    connected │ 
	 *       │<─────────────────────────────────────────────┤
	 *       │                                              │ 
	 *       │                                              │ 
	 *       │ send SubmitSm                                │ 
	 *       ├─────────────────────────────────────────────>│
	 *       │                                    response  │ 
	 *       │<─────────────────────────────────────────────┤
	 *       │                                              │
	 *       :                                              :
	 *       : wait for expiration of last request (200ms)  :                     (?) expiration is configured to 100ms (=> need to send EnquireLink)
	 *       :                                              :
	 *       │                                              │ 
	 *       │                                              │ 
	 *       ├─┐ send(Sms(sms content 2 / session 1))       │
	 *       │<┘                                            │
	 *       │ send EnquireLink                             │
	 *       ├─────────────────────────────────────────────>│
	 *       │                                    response  │ 
	 *       │<─────────────────────────────────────────────┤
	 *       │                                              │ 
	 *       │ send SubmitSm                                │ 
	 *       ├─────────────────────────────────────────────>│
	 *       │                                    response  │ 
	 *       │<─────────────────────────────────────────────┤
	 *       │                                              ├─┐ wait(200ms)
	 *       │                                              │<┘ 
	 *       :                                              :
	 *       : wait for expiration of last request (200ms)  :                     (?) expiration is configured to 100ms (=> need to send EnquireLink)
	 *       :                                              :
	 *       │                                              │ 
	 *       │                                              │ 
	 *       :                                              :
	 *       :       wait for reception of messages         :
	 *       :                                              :
	 *       │                                              │ 
	 *       │                                              ├─┐ stop()            (?) session is closed by the server => need a new session
	 *       │                                              │<┘ 
	 *       │                                              ├─┐ start() 
	 *       │                                              │<┘ 
	 *       │                                              │ 
	 *       ├─┐ send(Sms(sms content 3 / session 2))       │ 
	 *       │<┘                                            │
	 *       │ send EnquireLink                             │
	 *       ├─────────────────────────────────────────────>│
	 *       │                                 /!\ failure  │                     (?) ClosedChannelException 
	 *       │<─────────────────────────────────────────────┤
	 *       ├─┐ closing [session 1]                        │ 
	 *       │<┘                                            │
	 *       │                                              │ 
	 *       │ connect [session 2]                          │
	 *       ├─────────────────────────────────────────────>│
	 *       │                                    connected │ 
	 *       │<─────────────────────────────────────────────┤
	 *       │                                              │
	 *       │                                              │ 
	 *       │ send SubmitSm                                │ 
	 *       ├─────────────────────────────────────────────>│
	 *       │                                    response  │ 
	 *       │<─────────────────────────────────────────────┤
	 *       │                                              │
	 *       │                                              │ 
	 *       │                                              │ 
	 *       ├─┐ send(Sms(sms content 4 / session 2))       │
	 *       │<┘                                            │
	 *       │                                              │ 
	 *       │ send SubmitSm                                │ 
	 *       ├─────────────────────────────────────────────>│
	 *       │                                    response  │ 
	 *       │<─────────────────────────────────────────────┤
	 *       │                                              │ 
	 *       
	 * }</pre>
	 *   
	 */
	// @formatter:on
	@Test
	@SuppressWarnings("javadoc")
	public void reuseSessionButSessionClosedByServer() throws Exception {
		/**
		 * GIVEN
		 */
		// @formatter:off
		manualServer = new JSMPPServer(8057, new ServerConfig().build());
		// @formatter:on
		manualServer.start();
		// @formatter:off
		sender = builder
					.port(8057)
					.session()
						.responseTimeout(500L)
						.reuseSession()
							.enable(true)
							.responseTimeout(200L)
							.lastInteractionExpiration(100L)
							.and()
						.and()
					.build();
		// @formatter:on
		/**
		 * WHEN
		 */
		// send one SMS
		sender.send(new Sms().content("sms content 1 / session 1").from(SENDER).to(RECIPIENT));
		waitUntil(expirationOfLastRequest(200));
		// send another SMS with same session
		sender.send(new Sms().content("sms content 2 / session 1").from(SENDER).to(RECIPIENT));
		waitUntil(expirationOfLastRequest(200));
		// stop the server
		manualServer.stop();
		List<SubmitSm> session1Messages = manualServer.getReceivedMessages();
		// restart the server
		manualServer.start();
		// send another SMS with new session
		sender.send(new Sms().content("sms content 3 / session 2").from(SENDER).to(RECIPIENT));
		// send another SMS with same session
		sender.send(new Sms().content("sms content 4 / session 2").from(SENDER).to(RECIPIENT));
		List<SubmitSm> session2Messages = manualServer.getReceivedMessages();
		/**
		 * THEN
		 */
		assertThat(convert(session1Messages))
			.count(is(2))
			.message(0).content(is("sms content 1 / session 1")).and()
			.message(1).content(is("sms content 2 / session 1"));
		assertThat(convert(session2Messages))
			.count(is(2))
			.message(0).content(is("sms content 3 / session 2")).and()
			.message(1).content(is("sms content 4 / session 2"));
		
		// ensure that connection has been done two times
		// and 2 EnquireLink requests have been sent by first session
		// and no EnquireLink requests has been sent by second session
		verifyThat(this)
			.sessions(opened(2))
			.enquireLinks(sent(requests(2), by(session(0))))
			.enquireLinks(sent(requests(0), by(session(1))));
		// ensure that first session has been automatically closed
		// and second session is still running
		verifyThat(this)
			.session(0, closed())
			.client(client1, cleaned())
			.session(1, opened())
			.client(client2, not(cleaned()));
		
		// manual cleanup
		sender.clean();
		
		// ensure that all sessions are correctly closed
		verifyThat(this)
			.session(0, closed())
			.client(client1, cleaned())
			.session(1, closed())
			.client(client2, cleaned());
	}

	// @formatter:off
	/**
	 * 
	 * <pre>{@code
	 *  ┌─────────┐                                    ┌─────────┐
	 *  │ Client  │                                    │ Server  │
	 *  └────┬────┘                                    └────┬────┘
	 *       │                                              ├─┐ start() 
	 *       │                                              │<┘ 
	 *       │                                              │ 
	 *       │                                              │ 
	 *       ├─┐ send(Sms(sms content 1 / session 1))       │
	 *       │<┘                                            │
	 *       │ connect [session 1]                          │
	 *       ├─────────────────────────────────────────────>│
	 *       │                                    connected │ 
	 *       │<─────────────────────────────────────────────┤
	 *       │                                              │ 
	 *       │                                              │ 
	 *       │ send SubmitSm                                │ 
	 *       ├─────────────────────────────────────────────>│
	 *       │                                    response  │ 
	 *       │<─────────────────────────────────────────────┤
	 *       │                                              │ 
	 *       :                                              :
	 *       :  wait for expiration of last request (50ms)  :           (?) expiration is configured to 20ms
	 *       :                                              :
	 *       │                                              │ 
	 *       │                                              │ 
	 *       ├─┐ send(Sms(sms content 2 / session 2))       │
	 *       │<┘                                            │
	 *       │ send EnquireLink                             │
	 *       ├─────────────────────────────────────────────>│
	 *       │                                              ├─┐ wait(300ms) 
	 *       ├─┐ timeout (100ms)                            │ │ 
	 *       │<┘                                            │ │ 
	 *       │                                              │ :
	 *       │                                              │ 
	 *       │ unbind [session 1]                           │
	 *       ├─────────────────────────────────────────────>│
	 *       │                                 disconnected │ 
	 *       │<─────────────────────────────────────────────┤
	 *       │                                              │ 
	 *       │ connect [session 2]                          │
	 *       ├─────────────────────────────────────────────>│
	 *       │                                    connected │ 
	 *       │<─────────────────────────────────────────────┤
	 *       │                                              │ 
	 *       │                                              │ 
	 *       │ send SubmitSm                                │ 
	 *       ├─────────────────────────────────────────────>│
	 *       │                                    response  │ 
	 *       │<─────────────────────────────────────────────┤
	 *       
	 * }</pre>
	 *   
	 */
	// @formatter:on
	@Test
	@SuppressWarnings("javadoc")
	public void reuseSessionButEnquireLinkTimeout() throws Exception {
		/**
		 * GIVEN
		 */
		// @formatter:off
		manualServer = new JSMPPServer(8057, new ServerConfig()
				.slow()
					.sendEnquireLinkRespDelay(300L)
					.and()
				.build());
		// @formatter:on
		manualServer.start();
		// @formatter:off
		sender = builder
					.port(8057)
					.session()
						.reuseSession()
							.enable(true)
							.responseTimeout(100L)
							.lastInteractionExpiration(20L)		// do not send EnquireLink the first time but send the second time
							.and()
						.and()
					.build();
		// @formatter:on
		/**
		 * WHEN
		 */
		// send one SMS with new session (no EnquireLink on first connection)
		sender.send(new Sms().content("sms content 1 / session 1").from(SENDER).to(RECIPIENT));
		waitUntil(expirationOfLastRequest(50));
		// send another SMS with new session (EnquireLink has timed-out)
		sender.send(new Sms().content("sms content 2 / session 2").from(SENDER).to(RECIPIENT));
		/**
		 * THEN
		 */
		assertThat(convert(manualServer.getReceivedMessages()))
			.count(is(2))
			.message(0).content(is("sms content 1 / session 1")).and()
			.message(1).content(is("sms content 2 / session 2"));
		
		
		// ensure that connection has been done two times
		// ensure that EnquireLink has been sent to check if session is still active on session 1 (but failed)
		// ensure that EnquireLink has not been sent to check if session is still active on session 2
		verifyThat(this)
			.sessions(opened(2))
			.enquireLinks(sent(requests(1), by(session(0))))
			.enquireLinks(sent(requests(0), by(session(1))));
		// ensure that first session has been automatically closed
		// and second session is still running
		verifyThat(this)
			.session(0, closed())
			.client(client1, cleaned())
			.session(1, opened())
			.client(client2, not(cleaned()));
		
		// manual cleanup
		sender.clean();
		
		// ensure that all sessions are correctly closed
		verifyThat(this)
			.session(0, closed())
			.client(client1, cleaned())
			.session(1, closed())
			.client(client2, cleaned());
	}

	// @formatter:off
	/**
	 * 
	 * <pre>{@code
	 *  ┌─────────┐                                    ┌─────────┐
	 *  │ Client  │                                    │ Server  │
	 *  └────┬────┘                                    └────┬────┘
	 *       │                                              ├─┐ start() 
	 *       │                                              │<┘ 
	 *       │                                              │ 
	 *       │                                              │ 
	 *       ├─┐ send(Sms(sms content 1 / session 1))       │
	 *       │<┘                                            │
	 *       │ connect [session 1]                          │
	 *       ├─────────────────────────────────────────────>│
	 *       │                                    connected │ 
	 *       │<─────────────────────────────────────────────┤
	 *       │                                              │ 
	 *       │                                              │ 
	 *       │ send SubmitSm                                │ 
	 *       ├─────────────────────────────────────────────>│
	 *       │                                    response  │ 
	 *       │<─────────────────────────────────────────────┤
	 *       │                                              │
	 *       │                                              ├─┐ stop()            (?) session is closed by the server => need a new session
	 *       │                                              │<┘ 
	 *       │                                              │ 
	 *       ├─┐ send(Sms(can't be sent))                   │
	 *       │<┘                                            │
	 *       │ send SubmitSm                                │ 
	 *       ├─────────────────────────────────────────────>│
	 *       │                                 /!\ failure  │ 
	 *       │<─────────────────────────────────────────────┤
	 *       │                                              │ 
	 *       ├─┐ closing [session 1]                        │ 
	 *       │<┘                                            │
	 *       │ unbind [session 1]                           │
	 *       ├─────────────────────────────────────────────>│
	 *       │                                 disconnected │ 
	 *       │<─────────────────────────────────────────────┤
	 *       │                                              │ 
	 *       :                                              ├─┐ start() 
	 *       :                                              │<┘ 
	 *       :   wait for server ready                      │ 
	 *       :                                              │ 
	 *       :                                              │ 
	 *       ├─┐ send(Sms(sms content 2 / session 2))       │ 
	 *       │<┘                                            │
	 *       │ connect [session 2]                          │
	 *       ├─────────────────────────────────────────────>│
	 *       │                                    connected │ 
	 *       │<─────────────────────────────────────────────┤
	 *       │                                              │
	 *       │                                              │ 
	 *       │ send SubmitSm                                │ 
	 *       ├─────────────────────────────────────────────>│
	 *       │                                    response  │ 
	 *       │<─────────────────────────────────────────────┤
	 *       │                                              │ 
	 *       │                                              │ 
	 *       │                                              ├─┐ wait(200ms)
	 *       │                                              │<┘ 
	 *       :                                              :
	 *       : wait for expiration of last request (200ms)  :                     (?) expiration is configured to 100ms (=> need to send EnquireLink)
	 *       :                                              :
	 *       │                                              │ 
	 *       │                                              │ 
	 *       │                                              │ 
	 *       ├─┐ send(Sms(sms content 3 / session 2))       │
	 *       │<┘                                            │
	 *       │ send EnquireLink                             │
	 *       ├─────────────────────────────────────────────>│
	 *       │                                    response  │ 
	 *       │<─────────────────────────────────────────────┤
	 *       │                                              │ 
	 *       │ send SubmitSm                                │ 
	 *       ├─────────────────────────────────────────────>│
	 *       │                                    response  │ 
	 *       │<─────────────────────────────────────────────┤
	 *       
	 * }</pre>
	 *   
	 */
	// @formatter:on
	@Test
	@SuppressWarnings("javadoc")
	public void reuseSessionButSendFailsDueToSessionClosedByServer() throws Exception {
		/**
		 * GIVEN
		 */
		manualServer = new JSMPPServer(8057, new ServerConfig().build());
		manualServer.start();
		// @formatter:off
		sender = builder
					.port(8057)
					.session()
						.responseTimeout(500L)
						.reuseSession()
							.enable(true)
							.responseTimeout(200L)
							.lastInteractionExpiration(100L)
							.and()
						.and()
					.build();
		// @formatter:on
		
		/**
		 * WHEN
		 */
		// send one SMS
		sender.send(new Sms().content("sms content 1 / session 1").from(SENDER).to(RECIPIENT));
		// stop the server
		LOG.debug("Stopping server...");
		manualServer.stop();
		LOG.debug("Server stopped");
		List<SubmitSm> session1Messages = manualServer.getReceivedMessages();
		MessagingException sendFailure = assertThrows("should indicate that message couldn't be sent", MessagingException.class, () -> {
			sender.send(new Sms().content("can't be sent").from(SENDER).to(RECIPIENT));
		});
		// restart the server
		LOG.debug("Restarting server...");
		manualServer.start();
		LOG.debug("Server restarted...");
		// send another SMS with new session
		sender.send(new Sms().content("sms content 2 / session 2").from(SENDER).to(RECIPIENT));
		waitUntil(expirationOfLastRequest(200));
		// send another SMS with same session
		sender.send(new Sms().content("sms content 3 / session 2").from(SENDER).to(RECIPIENT));
		List<SubmitSm> session2Messages = manualServer.getReceivedMessages();
		
		/**
		 * THEN
		 */
		assertThat("should indicate why message couldn't be sent", sendFailure, hasAnyCause(SmppException.class));
		assertThat("should indicate source of send failure", sendFailure, hasAnyCause(ClosedChannelException.class));

		assertThat(convert(session1Messages))
			.count(is(1))
			.message(0).content(is("sms content 1 / session 1"));
		assertThat(convert(session2Messages))
			.count(is(2))
			.message(0).content(is("sms content 2 / session 2")).and()
			.message(1).content(is("sms content 3 / session 2"));
		
		// ensure that connection has been done two times
		// and 2 EnquireLink requests have been sent by first session
		// and no EnquireLink requests has been sent by second session
		verifyThat(this)
			.sessions(opened(2))
			.enquireLinks(sent(requests(0), by(session(0))))
			.enquireLinks(sent(requests(1), by(session(1))));
		// ensure that first session has been automatically closed
		// and second session is still running
		verifyThat(this)
			.session(0, closed())
			.client(client1, cleaned())
			.session(1, opened())
			.client(client2, not(cleaned()));
		
		// manual cleanup
		sender.clean();
		
		// ensure that all sessions are correctly closed
		verifyThat(this)
			.session(0, closed())
			.client(client1, cleaned())
			.session(1, closed())
			.client(client2, cleaned());
	}

	
	@Override
	public TestContext get() {
		return new ReuseSessionTestContext();
	}
	
	class ReuseSessionTestContext implements TestContext, SessionAware {
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

	private List<fr.sii.ogham.testing.sms.simulator.bean.SubmitSm> convert(List<SubmitSm> rawMessages) {
		return rawMessages.stream().map(m -> new SubmitSmAdapter(m)).collect(toList());
	}
}
