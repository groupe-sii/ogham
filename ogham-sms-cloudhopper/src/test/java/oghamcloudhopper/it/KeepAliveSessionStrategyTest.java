package oghamcloudhopper.it;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasAnyCause;
import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasMessage;
import static fr.sii.ogham.testing.sms.simulator.bean.NumberingPlanIndicator.ISDN;
import static fr.sii.ogham.testing.sms.simulator.bean.TypeOfNumber.UNKNOWN;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.spy;
import static testutils.SessionStrategyTestHelper.active;
import static testutils.SessionStrategyTestHelper.by;
import static testutils.SessionStrategyTestHelper.cleaned;
import static testutils.SessionStrategyTestHelper.client;
import static testutils.SessionStrategyTestHelper.clientConnected;
import static testutils.SessionStrategyTestHelper.closed;
import static testutils.SessionStrategyTestHelper.connectionAttempts;
import static testutils.SessionStrategyTestHelper.createdTasks;
import static testutils.SessionStrategyTestHelper.createdTimers;
import static testutils.SessionStrategyTestHelper.enquireLinkFailureReceived;
import static testutils.SessionStrategyTestHelper.enquireLinkReceived;
import static testutils.SessionStrategyTestHelper.enquireLinkTimeout;
import static testutils.SessionStrategyTestHelper.isNull;
import static testutils.SessionStrategyTestHelper.not;
import static testutils.SessionStrategyTestHelper.opened;
import static testutils.SessionStrategyTestHelper.reconnectionFailed;
import static testutils.SessionStrategyTestHelper.requests;
import static testutils.SessionStrategyTestHelper.sent;
import static testutils.SessionStrategyTestHelper.session;
import static testutils.SessionStrategyTestHelper.stopped;
import static testutils.SessionStrategyTestHelper.track;
import static testutils.SessionStrategyTestHelper.verifyThat;
import static testutils.SessionStrategyTestHelper.waitUntil;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
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

import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.sms.builder.cloudhopper.CloudhopperBuilder;
import fr.sii.ogham.sms.builder.cloudhopper.SmppClientSupplier;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.sender.impl.CloudhopperSMPPSender;
import fr.sii.ogham.testing.assertion.sms.AssertSms;
import fr.sii.ogham.testing.assertion.sms.ExpectedAddressedPhoneNumber;
import fr.sii.ogham.testing.assertion.sms.ExpectedSms;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import fr.sii.ogham.testing.extension.junit.sms.config.ServerConfig;
import fr.sii.ogham.testing.sms.simulator.SmppServerException;
import fr.sii.ogham.testing.sms.simulator.jsmpp.JSMPPServer;
import fr.sii.ogham.testing.sms.simulator.jsmpp.SubmitSmAdapter;
import testutils.SessionStrategyTestHelper.EnquireLinkTaskAware;
import testutils.SessionStrategyTestHelper.SessionAware;
import testutils.SessionStrategyTestHelper.TestContext;
import testutils.TrackClientAndSessionsDecorator;

public class KeepAliveSessionStrategyTest implements Supplier<TestContext> {
	private static final Logger LOG = LoggerFactory.getLogger(KeepAliveSessionStrategyTest.class);
	
	private static final String RECIPIENT = "0203040506";
	private static final String SENDER = "+33203040506";

	@Rule public final LoggingTestRule loggingRule = new LoggingTestRule();
	@Rule public final MockitoRule mockito = MockitoJUnit.rule();

	private CloudhopperSMPPSender sender;
	private CloudhopperBuilder builder;
	private JSMPPServer manualServer;
	
	@Spy SmppClient client1 = new DefaultSmppClient();
	@Spy SmppClient client2 = new DefaultSmppClient();
	@Spy SmppClient client3 = new DefaultSmppClient();
	List<SmppClient> clients;
	List<SmppSession> allSessions = new ArrayList<>();
	@Spy SmppClientSupplier supplier = new TrackClientAndSessionsDecorator(() -> clients, () -> allSessions);
	List<ScheduledExecutorService> timers = new ArrayList<>();
	List<ScheduledFuture<?>> enquireLinkTasks = new ArrayList<>();
	@Spy Supplier<ScheduledExecutorService> timerFactory = new Supplier<ScheduledExecutorService>() {
		@Override
		public ScheduledExecutorService get() {
			ScheduledExecutorService timer = spy(track(Executors.newSingleThreadScheduledExecutor(), enquireLinkTasks, timers.size()));
			timers.add(timer);
			return timer;
		}
	};

	@Before
	public void setup() throws IOException {
		clients = asList(client1, client2, client3);
		builder = new CloudhopperBuilder()
					.host("127.0.0.1")
					.clientSupplier(supplier)
					.session()
						.connectRetry()
							.fixedDelay()
								.maxRetries(5)
								.delay(200L)
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
	
	// TODO: bug when Reconnecting due to EnquireLink failure and Reconnecting due to message sending in the same time ?
	
	// @formatter:off
	/**
	 * 
	 * <pre>{@code
	 *  ┌─────────┐                                ┌─────────────────┐                                ┌─────────┐
	 *  │ Client  │                                │ EnquireLinkTask │                                │ Server  │
	 *  └────┬────┘                                └────────┬────────┘                                └────┬────┘
	 *       │                                              │                                              ├─┐ start() 
	 *       │                                              │                                              │<┘ 
	 *       │                                              │                                              │ 
	 *       │                                              │                                              │ 
	 *       ├─┐ send(Sms(sms content 1 / session 1))       │                                              │
	 *       │<┘                                            │                                              │
	 *       │ connect [session 1]                          │                                              │
	 *       ├────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       │                                              │                                    connected │
	 *       │<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       │                                              │                                              │
	 *       │ send SubmitSm                                │                                              │
	 *       ├────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       │                                              │                                    response  │ 
	 *       │<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       │                                              │                                              │
	 *       │                                              │                                              │ 
	 *       :                                              ├─┐ wait(300ms)                                │
	 *       :                                              │<┘                                            │
	 *       :                                              │ send EnquireLink                             │
	 *       :  wait for EnquireLink sent (350ms)           ├─────────────────────────────────────────────>│
	 *       :                                              │                                    response  │ 
	 *       :                                              │<─────────────────────────────────────────────┤
	 *       :                                              :                                              │
	 *       :                                              :                                              │
	 *       │                                              │                                              │
	 *       ├─┐ send(Sms(sms content 2 / session 1))       │                                              │
	 *       │<┘                                            │                                              │
	 *       │ send SubmitSm                                │                                              │
	 *       ├────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       │                                              │                                    response  │ 
	 *       │<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       │                                              │                                              │ 
	 *       :                                              ├─┐ wait(300ms)                                │
	 *       :                                              │<┘                                            │
	 *       :                                              │ send EnquireLink                             │
	 *       :                                              ├─────────────────────────────────────────────>│
	 *       :                                              │                                    response  │ 
	 *       :                                              │<─────────────────────────────────────────────┤
	 *       : wait for 2 EnquireLink sent (650ms)          :                                              │
	 *       :                                              :                                              │
	 *       :                                              ├─┐ wait(300ms)                                │
	 *       :                                              │<┘                                            │
	 *       :                                              │ send EnquireLink                             │
	 *       :                                              ├─────────────────────────────────────────────>│
	 *       :                                              │                                    response  │ 
	 *       :                                              │<─────────────────────────────────────────────┤
	 *       :                                              :                                              │
	 *       :                                              :                                              │
	 *       │                                              │                                              │
	 *       ├─┐ send(Sms(sms content 3 / session 1))       │                                              │
	 *       │<┘                                            │                                              │
	 *       │ send SubmitSm                                │                                              │
	 *       ├────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       │                                              │                                    response  │ 
	 *       │<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       
	 * }</pre>
	 *   
	 */
	@Test
	@SuppressWarnings("javadoc")
	public void keepAlive() throws Exception {
		manualServer = new JSMPPServer(new ServerConfig().randomPort().build());
		manualServer.start();
		/**
		 * GIVEN
		 */
		// @formatter:off
		sender = builder
					.port(manualServer.getPort())
					.session()
						.responseTimeout(200L)
						.keepAlive()
							.enable(true)
							.responseTimeout(200L)
							.interval(300L)
							.executor(timerFactory)
							.maxConsecutiveTimeouts(1)
							.and()
						.and()
					.build();
		// @formatter:on
		/**
		 * WHEN
		 */
		// send one SMS
		sender.send(new Sms().content("sms content 1 / session 1").from(SENDER).to(RECIPIENT));
		waitUntil(enquireLinkReceived(allSessions.get(0), 1));
		// send another SMS with same session
		sender.send(new Sms().content("sms content 2 / session 1").from(SENDER).to(RECIPIENT));
		waitUntil(enquireLinkReceived(allSessions.get(0), 2));
		waitUntil(enquireLinkReceived(allSessions.get(0), 3));
		// send another SMS with same session
		sender.send(new Sms().content("sms content 3 / session 1").from(SENDER).to(RECIPIENT));
		/**
		 * THEN
		 */
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
		// and 3 EnquireLink requests have been sent (using only one task)
		verifyThat(this)
			.sessions(opened(1))
			.enquireLinks(sent(requests(3), by(session(0))), createdTimers(1), createdTasks(1));
		// ensure that session is still running
		verifyThat(this)
			.session(0, opened())
			.client(client1, not(cleaned()))
			.enquireLinkTask(0, active())
			.session(1, isNull())
			.client(client2, not(cleaned()))
			.enquireLinkTask(1, isNull());
		
		// manual cleanup
		sender.clean();
		
		// ensure that session is correctly closed
		verifyThat(this)
			.session(0, closed())
			.client(client1, cleaned())
			.enquireLinkTask(0, stopped())
			.session(1, isNull())
			.client(client2, not(cleaned()))
			.enquireLinkTask(1, isNull());
	}

	// @formatter:off
	/**
	 * 
	 * <pre>{@code
	 *  ┌─────────┐                                ┌─────────────────┐                                ┌─────────┐
	 *  │ Client  │                                │ EnquireLinkTask │                                │ Server  │
	 *  └────┬────┘                                └────────┬────────┘                                └────┬────┘
	 *       │                                              │                                              ├─┐ start() 
	 *       │                                              │                                              │<┘ 
	 *       │                                              │                                              │ 
	 *       │                                              │                                              │ 
	 *       ├─┐ send(Sms(sms content 1 / session 1))       │                                              │
	 *       │<┘                                            │                                              │
	 *       │ connect [session 1]                          │                                              │
	 *       ├────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       │                                              │                                    connected │
	 *       │<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       │                                              │                                              │
	 *       │ send SubmitSm                                │                                              │
	 *       ├────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       │                                              │                                    response  │ 
	 *       │<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       │                                              │                                              │
	 *       │                                              │                                              │ 
	 *       :                                              ├─┐ wait(200ms)                                │
	 *       :                                              │<┘                                            │
	 *       :                                              │ send EnquireLink                             │
	 *       :  wait for EnquireLink failure (400ms)        ├─────────────────────────────────────────────>│
	 *       :                                              │                                    response  │ 
	 *       :                                              │                                              ├─┐ wait(300ms) 
	 *       :                                              ├─┐ timeout (100ms)                            │ │ 
	 *       :                                              │<┘                                            │ │ 
	 *       │                                              │                                              │ :
	 *       │ unbind [session 1]                           │                                              │
	 *       ├────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       │                                              │                                 disconnected │
	 *       │<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       │                                              │                                              │
	 *       │ connect [session 2]                          │                                              │
	 *       ├────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       │                                              │                                    connected │
	 *       │<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       │                                              │                                              │
	 *       :                                              :                                              │
	 *       :  still waiting                               :                                              │
	 *       :                                              :                                              │
	 *       │                                              │                                              │
	 *       │                                              │                                              │
	 *       ├─┐ send(Sms(sms content 2 / session 2))       │                                              │
	 *       │<┘                                            │                                              │
	 *       │ send SubmitSm                                │                                              │
	 *       ├────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       │                                              │                                    response  │ 
	 *       │<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       
	 * }</pre>
	 *   
	 */
	@Test
	@SuppressWarnings("javadoc")
	public void keepAliveButEnquireLinkTimeout() throws Exception {
		/**
		 * GIVEN
		 */
		// @formatter:off
		manualServer = new JSMPPServer(new ServerConfig()
				.randomPort()
				.slow()
					.sendEnquireLinkRespDelay(300L)
					.and()
				.build());
		// @formatter:on
		manualServer.start();
		// @formatter:off
		sender = builder
					.port(manualServer.getPort())
					.session()
						.responseTimeout(200L)
						.keepAlive()
							.enable(true)
							.responseTimeout(100L)
							.interval(200L)
							.executor(timerFactory)
							.maxConsecutiveTimeouts(1)
							.and()
						.and()
					.build();
		// @formatter:on
		/**
		 * WHEN
		 */
		// send one SMS
		sender.send(new Sms().content("sms content 1 / session 1").from(SENDER).to(RECIPIENT));
		waitUntil(enquireLinkTimeout(allSessions.get(0)));
		waitUntil(clientConnected(client2));
		// send another SMS with new session
		sender.send(new Sms().content("sms content 2 / session 2").from(SENDER).to(RECIPIENT));
		/**
		 * THEN
		 */
		AssertSms.assertEquals(
				Arrays.asList(
						new ExpectedSms("sms content 1 / session 1",
								new ExpectedAddressedPhoneNumber(SENDER, UNKNOWN, ISDN),
								new ExpectedAddressedPhoneNumber(RECIPIENT, UNKNOWN, ISDN)),
						new ExpectedSms("sms content 2 / session 2", 
								new ExpectedAddressedPhoneNumber(SENDER, UNKNOWN, ISDN),
								new ExpectedAddressedPhoneNumber(RECIPIENT, UNKNOWN, ISDN))),
				convert(manualServer.getReceivedMessages()));

		// ensure that connection has been done two times
		// and 1 EnquireLink request has been sent by session 1
		// and no EnquireLink request has been sent by session 2
		// and that a task is created for each session
		verifyThat(this)
			.sessions(opened(2))
			.enquireLinks(sent(requests(1), by(session(0))))
			.enquireLinks(sent(requests(0), by(session(1))))
			.enquireLinks(createdTimers(2), createdTasks(2));
		// ensure that first session has been automatically closed
		// and second session is still running
		verifyThat(this)
			.session(0, closed())
			.client(client1, cleaned())
			.enquireLinkTask(0, stopped())
			.session(1, opened())
			.client(client2, not(cleaned()))
			.enquireLinkTask(1, active());
		
		// manual cleanup
		sender.clean();
		
		// ensure that all sessions are correctly closed
		verifyThat(this)
			.session(0, closed())
			.client(client1, cleaned())
			.enquireLinkTask(0, stopped())
			.session(1, closed())
			.client(client2, cleaned())
			.enquireLinkTask(1, stopped());
	}


	// @formatter:off
	/**
	 * 
	 * <pre>{@code
	 *  ┌─────────┐                                ┌─────────────────┐                                ┌─────────┐
	 *  │ Client  │                                │ EnquireLinkTask │                                │ Server  │
	 *  └────┬────┘                                └────────┬────────┘                                └────┬────┘
	 *       │                                              │                                              ├─┐ start() 
	 *       │                                              │                                              │<┘ 
	 *       │                                              │                                              │ 
	 *       │                                              │                                              │ 
	 *       ├─┐ send(Sms(sms content 1 / session 1))       │                                              │
	 *       │<┘                                            │                                              │
	 *       │ connect [session 1]                          │                                              │
	 *       ├────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       │                                              │                                    connected │
	 *       │<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       │                                              │                                              │
	 *       │ send SubmitSm                                │                                              │
	 *       ├────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       │                                              │                                    response  │ 
	 *       │<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       │                                              │                                              │
	 *       :                                              ├─┐ wait(300ms)                                ├─┐ stop()            (?) session is closed by the server => need a new session
	 *       :                                              │ │                                            │<┘
	 *       :                                              │ │                                            ├─┐ start() 
	 *       :                                              │ │                                            │<┘
	 *       :                                              │ │                                            │
	 *       :   wait for EnquireLink failure (350ms)       │<┘                                            │ 
	 *       :                                              │ send EnquireLink                             │ 
	 *       :                                              ├─────────────────────────────────────────────>│
	 *       :                                              │                                 /!\ failure  │                     (?) ClosedChannelException 
	 *       :                                              │<─────────────────────────────────────────────┤ 
	 *       :                                              │                                              │
	 *       │                                              │                                              │ 
	 *       │                                              │                                              │
	 *       │ unbind [session 1]                           │                                              │
	 *       ├────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       │                                              │                                 disconnected │
	 *       │<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       │                                              │                                              │
	 *       │ connect [session 2]                          │                                              │
	 *       ├────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       │                                              │                                    connected │
	 *       │<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       │                                              │                                              │
	 *       :                                              :                                              │
	 *       :  still waiting                               :                                              │
	 *       :                                              :                                              │
	 *       │                                              │                                              │
	 *       │                                              │                                              │
	 *       ├─┐ send(Sms(sms content 2 / session 2))       │                                              │
	 *       │<┘                                            │                                              │
	 *       │ send SubmitSm                                │                                              │
	 *       ├────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       │                                              │                                    response  │ 
	 *       │<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       
	 * }</pre>
	 *   
	 */
	@Test
	@SuppressWarnings("javadoc")
	public void keepAliveButSessionClosedByServer() throws Exception {
		/**
		 * GIVEN
		 */
		// @formatter:off
		manualServer = new JSMPPServer(new ServerConfig().randomPort().build());
		// @formatter:on
		manualServer.start();
		// @formatter:off
		sender = builder
					.port(manualServer.getPort())
					.session()
						.responseTimeout(500L)
						.keepAlive()
							.enable(true)
							.responseTimeout(100L)
							.interval(300L)
							.executor(timerFactory)
							.maxConsecutiveTimeouts(1)
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
		// restart the server
		manualServer.start();
		// wait
		waitUntil(enquireLinkFailureReceived(allSessions.get(0)));
		waitUntil(clientConnected(client2));
		// send another SMS with new session
		sender.send(new Sms().content("sms content 2 / session 2").from(SENDER).to(RECIPIENT));
		List<SubmitSm> session2Messages = manualServer.getReceivedMessages();
		
		/**
		 * THEN
		 */
		assertThat(convert(session1Messages))
			.count(is(1))
			.message(0).content(is("sms content 1 / session 1"));
		assertThat(convert(session2Messages))
			.count(is(1))
			.message(0).content(is("sms content 2 / session 2"));

		// ensure that connection has been done two times
		// and 1 EnquireLink request has been sent by first session (but failed)
		// and that a task is created for each session
		verifyThat(this)
			.sessions(opened(2))
			.enquireLinks(sent(requests(1), by(session(0))))
			.enquireLinks(sent(requests(0), by(session(1))))
			.enquireLinks(createdTimers(2), createdTasks(2));
		// ensure that first session has been automatically closed
		// and second session is still running
		verifyThat(this)
			.session(0, closed())
			.client(client1, cleaned())
			.enquireLinkTask(0, stopped())
			.session(1, opened())
			.client(client2, not(cleaned()))
			.enquireLinkTask(1, active());
		
		// manual cleanup
		sender.clean();
		
		// ensure that all sessions are correctly closed
		verifyThat(this)
			.session(0, closed())
			.client(client1, cleaned())
			.enquireLinkTask(0, stopped())
			.session(1, closed())
			.client(client2, cleaned())
			.enquireLinkTask(1, stopped());
	}

	

	// @formatter:off
	/**
	 * 
	 * <pre>{@code
	 *  ┌─────────┐                                ┌─────────────────┐                                ┌─────────┐
	 *  │ Client  │                                │ EnquireLinkTask │                                │ Server  │
	 *  └────┬────┘                                └────────┬────────┘                                └────┬────┘
	 *       │                                              │                                              ├─┐ start() 
	 *       │                                              │                                              │<┘ 
	 *       │                                              │                                              │ 
	 *       │                                              │                                              │ 
	 *       ├─┐ send(Sms(sms content 1 / session 1))       │                                              │
	 *       │<┘                                            │                                              │
	 *       │ connect [session 1]                          │                                              │
	 *       ├────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       │                                              │                                    connected │
	 *       │<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       │                                              │                                              │
	 *       │ send SubmitSm                                │                                              │
	 *       ├────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       │                                              │                                    response  │ 
	 *       │<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       │                                              │                                              │
	 *       :                                              ├─┐ wait(200ms)                                ├─┐ stop()            (?) session is closed by the server => need a new session
	 *       :                                              │ │                                            │<┘
	 *       :                                              │ │                                            │ 
	 *       :                                              │ │                                            │
	 *       :                                              │ │                                            │
	 *       :   wait for maximum reconnection attempts     │<┘                                            │ 
	 *       :                 (1500ms)                     │ send EnquireLink                             │ 
	 *       :                                              ├─────────────────────────────────────────────>│
	 *       :                                              │                                 /!\ failure  │                     (?) ClosedChannelException 
	 *       :                                              │<─────────────────────────────────────────────┤ 
	 *       :                                              │                                              │
	 *       :                                              :                                              │ 
	 *       :                                              :                                              │
	 *       : unbind [session 1]                           :                                              │
	 *       :────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       :                                              :                                 disconnected │
	 *       :<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       :                                              :                                              │
	 *       : connect (tried 5 times)                      :                                              │
	 *       :────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       :                                              :                                 /!\ failure  │                     (?) Retried 5 times
	 *       :<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       :                                              :                                              │
	 *       :                                              :                                              │
	 *       :  still waiting for maximum attempts          :                                              │
	 *       :                                              :                                              │
	 *       :                                              :                                              │
	 *       ├─┐ clean()                                    │                                              │ 
	 *       │<┘                                            │                                              │ 
	 *       │                                              │                                              │
	 *       │                                              │                                              ├─┐ start()
	 *       │                                              │                                              │<┘
	 *       │                                              │                                              │ 
	 *       ├─┐ send(Sms(sms content 2 / session 2))       │                                              │
	 *       │<┘                                            │                                              │
	 *       │ connect [session 2]                          │                                              │
	 *       ├────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       │                                              │                                    connected │
	 *       │<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       │                                              │                                              │ 
	 *       │ send SubmitSm                                │                                              │
	 *       ├────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       │                                              │                                    response  │ 
	 *       │<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       
	 * }</pre>
	 *   
	 */
	@Test
	@SuppressWarnings("javadoc")
	public void keepAliveButSessionClosedByServerAndCantReconnect() throws Exception {
		/**
		 * GIVEN
		 */
		// @formatter:off
		manualServer = new JSMPPServer(new ServerConfig().randomPort().build());
		// @formatter:on
		manualServer.start();
		// @formatter:off
		sender = builder
					.port(manualServer.getPort())
					.session()
						.responseTimeout(200L)
						.keepAlive()
							.enable(true)
							.responseTimeout(100L)
							.interval(200L)
							.executor(timerFactory)
							.maxConsecutiveTimeouts(1)
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
		manualServer.stop();
		List<SubmitSm> session1Messages = manualServer.getReceivedMessages();
		// wait for reconnection attempts
		waitUntil(enquireLinkFailureReceived(allSessions.get(0)));
		waitUntil(reconnectionFailed(client2, 5));
		manualServer.start();
		// send another SMS with new session
		sender.send(new Sms().content("sms content 2 / session 2").from(SENDER).to(RECIPIENT));
		List<SubmitSm> session2Messages = manualServer.getReceivedMessages();
		
		/**
		 * THEN
		 */
		assertThat(convert(session1Messages))
			.count(is(1))
			.message(0).content(is("sms content 1 / session 1"));
		assertThat(convert(session2Messages))
			.count(is(1))
			.message(0).content(is("sms content 2 / session 2"));

		// ensure that connection has been tried 2 times
		// and 1 EnquireLink request has been sent by first session (but failed)
		// and that a task is created for each session
		verifyThat(this)
			.sessions(opened(2, connectionAttempts(1, by(client(0))), connectionAttempts(5, by(client(1))), connectionAttempts(1, by(client(2)))))
			.enquireLinks(sent(requests(1), by(session(0))))
			.enquireLinks(sent(requests(0), by(session(1))))
			.enquireLinks(createdTimers(2), createdTasks(2));
		// ensure that first session has been automatically closed
		// and second session is still running
		verifyThat(this)
			.session(0, closed())
			.client(client1, cleaned())
			.enquireLinkTask(0, stopped())
			.client(client2, cleaned())
			.session(1, opened())
			.client(client3, not(cleaned()))
			.enquireLinkTask(1, active());
		
		// manual cleanup
		sender.clean();
		
		// ensure that all sessions are correctly closed
		verifyThat(this)
			.session(0, closed())
			.client(client1, cleaned())
			.enquireLinkTask(0, stopped())
			.client(client2, cleaned())
			.session(1, closed())
			.client(client3, cleaned())
			.enquireLinkTask(1, stopped());
	}

	// @formatter:off
	/**
	 * 
	 * <pre>{@code
	 *  ┌─────────┐                                ┌─────────────────┐                                ┌─────────┐
	 *  │ Client  │                                │ EnquireLinkTask │                                │ Server  │
	 *  └────┬────┘                                └────────┬────────┘                                └────┬────┘
	 *       │                                              │                                              ├─┐ start() 
	 *       │                                              │                                              │<┘ 
	 *       │                                              │                                              │ 
	 *       │                                              │                                              │ 
	 *       ├─┐ init                                       │                                              │
	 *       │<┘                                            │                                              │
	 *       │ connect [session 1]                          │                                              │
	 *       ├────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       │                                              │                                    connected │
	 *       │<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       │                                              │                                              │
	 *       :                                              ├─┐ wait(300ms)                                │
	 *       :                                              │<┘                                            │
	 *       :                                              │ send EnquireLink                             │
	 *       :  wait for EnquireLink sent (350ms)           ├─────────────────────────────────────────────>│
	 *       :                                              │                                    response  │ 
	 *       :                                              │<─────────────────────────────────────────────┤
	 *       :                                              :                                              │
	 *       :                                              :                                              │
	 *       │                                              │                                              │
	 *       │                                              │                                              │
	 *       ├─┐ send(Sms(sms content 1 / session 1))       │                                              │
	 *       │<┘                                            │                                              │
	 *       │ send SubmitSm                                │                                              │
	 *       ├────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       │                                              │                                    response  │ 
	 *       │<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       │                                              │                                              │
	 *       │                                              │                                              │ 
	 *       ├─┐ send(Sms(sms content 2 / session 1))       │                                              │
	 *       │<┘                                            │                                              │
	 *       │ send SubmitSm                                │                                              │
	 *       ├────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       │                                              │                                    response  │ 
	 *       │<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       │                                              │                                              │ 
	 *       :                                              ├─┐ wait(300ms)                                │
	 *       :                                              │<┘                                            │
	 *       :                                              │ send EnquireLink                             │
	 *       :                                              ├─────────────────────────────────────────────>│
	 *       :                                              │                                    response  │ 
	 *       :                                              │<─────────────────────────────────────────────┤
	 *       : wait for 2 EnquireLink sent (650ms)          :                                              │
	 *       :                                              :                                              │
	 *       :                                              ├─┐ wait(300ms)                                │
	 *       :                                              │<┘                                            │
	 *       :                                              │ send EnquireLink                             │
	 *       :                                              ├─────────────────────────────────────────────>│
	 *       :                                              │                                    response  │ 
	 *       :                                              │<─────────────────────────────────────────────┤
	 *       :                                              :                                              │
	 *       :                                              :                                              │
	 *       │                                              │                                              │
	 *       ├─┐ send(Sms(sms content 3 / session 1))       │                                              │
	 *       │<┘                                            │                                              │
	 *       │ send SubmitSm                                │                                              │
	 *       ├────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       │                                              │                                    response  │ 
	 *       │<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       
	 * }</pre>
	 *   
	 */
	@Test
	@SuppressWarnings("javadoc")
	public void keepAliveConnectedAtStartup() throws Exception {
		/**
		 * GIVEN
		 */
		manualServer = new JSMPPServer(new ServerConfig().randomPort().build());
		manualServer.start();
		
		/**
		 * WHEN
		 */
		// @formatter:off
		sender = builder
					.port(manualServer.getPort())
					.session()
						.responseTimeout(200L)
						.keepAlive()
							.enable(true)
							.responseTimeout(200L)
							.interval(300L)
							.executor(timerFactory)
							.connectAtStartup(true)
							.maxConsecutiveTimeouts(1)
							.and()
						.and()
					.build();
		// @formatter:on
		waitUntil(enquireLinkReceived(allSessions.get(0), 1));
		// send one SMS
		sender.send(new Sms().content("sms content 1 / session 1").from(SENDER).to(RECIPIENT));
		// send another SMS with same session
		sender.send(new Sms().content("sms content 2 / session 1").from(SENDER).to(RECIPIENT));
		waitUntil(enquireLinkReceived(allSessions.get(0), 2));
		waitUntil(enquireLinkReceived(allSessions.get(0), 3));
		// send another SMS with same session
		sender.send(new Sms().content("sms content 3 / session 1").from(SENDER).to(RECIPIENT));
		
		/**
		 * THEN
		 */
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
		// and 3 EnquireLink requests have been sent (using only one task)
		verifyThat(this)
			.sessions(opened(1))
			.enquireLinks(sent(requests(3), by(session(0))), createdTimers(1), createdTasks(1));
		// ensure that session is still running
		verifyThat(this)
			.session(0, opened())
			.client(client1, not(cleaned()))
			.enquireLinkTask(0, active())
			.session(1, isNull())
			.client(client2, not(cleaned()))
			.enquireLinkTask(1, isNull());
		
		// manual cleanup
		sender.clean();
		
		// ensure that session is correctly closed
		verifyThat(this)
			.session(0, closed())
			.client(client1, cleaned())
			.enquireLinkTask(0, stopped())
			.session(1, isNull())
			.client(client2, not(cleaned()))
			.enquireLinkTask(1, isNull());
	}
	
	// @formatter:off
	/**
	 * 
	 * <pre>{@code
	 *  ┌─────────┐                                ┌─────────────────┐                                ┌─────────┐
	 *  │ Client  │                                │ EnquireLinkTask │                                │ Server  │
	 *  └────┬────┘                                └────────┬────────┘                                └────┬────┘
	 *       │                                              │                                              ├─┐ start() 
	 *       │                                              │                                              │<┘ 
	 *       │                                              │                                              │ 
	 *       │                                              │                                              │ 
	 *       ├─┐ send(Sms(sms content 1 / session 1))       │                                              │
	 *       │<┘                                            │                                              │
	 *       │ connect [session 1]                          │                                              │
	 *       ├────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       │                                              │                                    connected │
	 *       │<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       │                                              │                                              │
	 *       │ send SubmitSm                                │                                              │
	 *       ├────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       │                                              │                                    response  │ 
	 *       │<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       │                                              │                                              │ 
	 *       │                                              │                                              │ 
	 *       │                                              │                                              ├─┐ stop()            (?) session is closed by the server => need a new session
	 *       │                                              │                                              │<┘
	 *       │                                              │                                              │
	 *       ├─┐ send(Sms(can't be sent))                   │                                              │
	 *       │<┘                                            │                                              │
	 *       │ send SubmitSm                                │                                              │
	 *       ├────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       │                                              │                                 /!\ failure  │ 
	 *       │<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       │                                              │                                              │
	 *       │ unbind [session 1]                           │                                              │
	 *       ├────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       │                                              │                                 disconnected │
	 *       │<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       │                                              │                                              │
	 *       │ connect [session 2]                          │                                              │
	 *       ├────────────────────────────────────────────────────────────────────────────────────────────>│                     (?) automatic reconnection (may be tried several times)
	 *       │                                              │                                              │
	 *       :                                              │                                              ├─┐ start()
	 *       :                                              │                                              │<┘
	 *       :   wait for reconnection                      │                                              │
	 *       :                                              │                                              │
	 *       :                                              │                                              │
	 *       │                                              │                                    connected │
	 *       │<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       │                                              │                                              │
	 *       ├─┐ send(Sms(sms content 2 / session 2))       │                                              │
	 *       │<┘                                            │                                              │
	 *       │ send SubmitSm                                │                                              │
	 *       ├────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       │                                              │                                    response  │ 
	 *       │<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       │                                              │                                              │
	 *       │                                              │                                              │
	 *       :                                              ├─┐ wait(300ms)                                │
	 *       :                                              │<┘                                            │
	 *       :                                              │ send EnquireLink                             │
	 *       :  wait for EnquireLink sent (350ms)           ├─────────────────────────────────────────────>│
	 *       :                                              │                                    response  │ 
	 *       :                                              │<─────────────────────────────────────────────┤
	 *       :                                              │                                              │
	 *       │                                              │                                              │
	 *       │                                              │                                              │
	 *       ├─┐ send(Sms(sms content 3 / session 2))       │                                              │
	 *       │<┘                                            │                                              │
	 *       │ send SubmitSm                                │                                              │
	 *       ├────────────────────────────────────────────────────────────────────────────────────────────>│
	 *       │                                              │                                    response  │ 
	 *       │<────────────────────────────────────────────────────────────────────────────────────────────┤
	 *       
	 * }</pre>
	 *   
	 */
	@Test
	@SuppressWarnings("javadoc")
	public void keepAliveButSendFailsDueToSessionClosedByServer() throws Exception {
		/**
		 * GIVEN
		 */
		// @formatter:off
		manualServer = new JSMPPServer(new ServerConfig().randomPort().build());
		// @formatter:on
		manualServer.start();
		// @formatter:off
		sender = builder
					.port(manualServer.getPort())
					.session()
						.responseTimeout(500L)
						.keepAlive()
							.enable(true)
							.responseTimeout(100L)
							.interval(300L)
							.executor(timerFactory)
							.maxConsecutiveTimeouts(1)
							.and()
						.connectRetry()
							.fixedDelay()
								.delay(1000L)
								.and()
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
		waitUntil(clientConnected(client2));
		// send another SMS with new session
		sender.send(new Sms().content("sms content 2 / session 2").from(SENDER).to(RECIPIENT));
		// wait
		waitUntil(enquireLinkReceived(allSessions.get(1), 1));
		// send another SMS with new session
		sender.send(new Sms().content("sms content 3 / session 2").from(SENDER).to(RECIPIENT));
		List<SubmitSm> session2Messages = manualServer.getReceivedMessages();
		
		/**
		 * THEN
		 */
		assertThat("should indicate why message couldn't be sent", sendFailure, hasAnyCause(MessageException.class, hasMessage(containsString("current session is broken"))));
		assertThat("should indicate automatic reconnection", sendFailure, hasAnyCause(MessageException.class, hasMessage(containsString("A new SMPP session is requested in background"))));
		assertThat("should indicate source of send failure", sendFailure, hasAnyCause(ClosedChannelException.class));
		
		assertThat(convert(session1Messages))
			.count(is(1))
			.message(0).content(is("sms content 1 / session 1"));
		assertThat(convert(session2Messages))
			.count(is(2))
			.message(0).content(is("sms content 2 / session 2")).and()
			.message(1).content(is("sms content 3 / session 2"));

		// ensure that connection has been done two times
		// and 1 EnquireLink request has been sent by first session (but failed)
		// and that a task is created for each session
		verifyThat(this)
			.sessions(opened(2))
			.enquireLinks(sent(requests(0), by(session(0))))
			.enquireLinks(sent(requests(1), by(session(1))))
			.enquireLinks(createdTimers(2), createdTasks(2));
		// ensure that first session has been automatically closed
		// and second session is still running
		verifyThat(this)
			.session(0, closed())
			.client(client1, cleaned())
			.enquireLinkTask(0, stopped())
			.session(1, opened())
			.client(client2, not(cleaned()))
			.enquireLinkTask(1, active());
		
		// manual cleanup
		sender.clean();
		
		// ensure that all sessions are correctly closed
		verifyThat(this)
			.session(0, closed())
			.client(client1, cleaned())
			.enquireLinkTask(0, stopped())
			.session(1, closed())
			.client(client2, cleaned())
			.enquireLinkTask(1, stopped());
	}
	
	public TestContext get() {
		return new KeepAliveTestContext();
	}
	
	class KeepAliveTestContext implements TestContext, SessionAware, EnquireLinkTaskAware {
		public List<SmppClient> getClients() {
			return clients;
		}
		public List<SmppSession> getSessions() {
			return allSessions;
		}
		public SmppClientSupplier getSupplier() {
			return supplier;
		}
		public List<ScheduledExecutorService> getTimers() {
			return timers;
		}
		public List<ScheduledFuture<?>> getEnquireLinkTasks() {
			return enquireLinkTasks;
		}
		public Supplier<ScheduledExecutorService> getTimerFactory() {
			return timerFactory;
		}
	}
	

	
	private List<fr.sii.ogham.testing.sms.simulator.bean.SubmitSm> convert(List<SubmitSm> rawMessages) {
		return rawMessages.stream().map(m -> new SubmitSmAdapter(m)).collect(toList());
	}
}
