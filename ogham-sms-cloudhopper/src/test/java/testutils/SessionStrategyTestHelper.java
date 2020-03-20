package testutils;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Function;
import java.util.function.Supplier;

import org.hamcrest.MatcherAssert;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudhopper.smpp.SmppClient;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.type.RecoverablePduException;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;

import fr.sii.ogham.core.retry.NamedCallable;
import fr.sii.ogham.sms.builder.cloudhopper.SmppClientSupplier;
import testutils.TrackScheduledTasksDecorator.SpayableScheduledFuture;

public class SessionStrategyTestHelper {
	private static final Logger LOG = LoggerFactory.getLogger(SessionStrategyTestHelper.class);
	
	private final TestContext context;
	
	private SessionStrategyTestHelper(TestContext context) {
		super();
		this.context = context;
	}

	public SessionStrategyTestHelper sessions(Verification... verifications) {
		verifyAll(verifications);
		return this;
	}
	
	public SessionStrategyTestHelper enquireLinks(Verification... verifications) {
		verifyAll(verifications);
		return this;
	}

	public SessionStrategyTestHelper session(int sessionIdx, Verification... verifications) {
		List<SmppSession> sessions = ((SessionAware) context).getSessions();
		SmppSession session = sessionIdx>=sessions.size() ? null : sessions.get(sessionIdx);
		for (Verification verification : verifications) {
			verification.verify(new SessionContext(session));
		}
		return this;
	}
	
	public SessionStrategyTestHelper session(SmppSession session, Verification... verifications) {
		for (Verification verification : verifications) {
			verification.verify(new SessionContext(session));
		}
		return this;
	}
	
	public SessionStrategyTestHelper client(int clientIdx, Verification... verifications) {
		List<SmppClient> clients = ((SessionAware) context).getClients();
		SmppClient client = clientIdx>=clients.size() ? null : clients.get(clientIdx);
		for (Verification verification : verifications) {
			verification.verify(new ClientContext(client));
		}
		return this;
	}
	
	public SessionStrategyTestHelper client(SmppClient client, Verification... verifications) {
		for (Verification verification : verifications) {
			verification.verify(new ClientContext(client));
		}
		return this;
	}
	
	public SessionStrategyTestHelper enquireLinkTask(int requestIdx, Verification... verifications) {
		EnquireLinkTaskAware c = (EnquireLinkTaskAware) context;
		ScheduledExecutorService timer = requestIdx>=c.getTimers().size() ? null : c.getTimers().get(requestIdx);
		ScheduledFuture<?> task = requestIdx>=c.getEnquireLinkTasks().size() ? null : c.getEnquireLinkTasks().get(requestIdx);
		for (Verification verification : verifications) {
			verification.verify(new EnquireLinkRequestContext(timer, task));
		}
		return this;
	}
	

	private void verifyAll(Verification... verifications) {
		for (Verification verification : verifications) {
			verification.verify(context);
		}
	}
	
	public static SessionStrategyTestHelper verifyThat(Supplier<TestContext> test) {
		return new SessionStrategyTestHelper(test.get());
	}
	
	public static Verification opened(int numberOpened) {
		return opened(numberOpened, context -> {
			try {
				SessionAware c = (SessionAware) context;
				for (int i=0 ; i<numberOpened ; i++) {
					verify(c.getClients().get(i), times(1).description("session["+i+"] should be opened by calling clients["+i+"].bind()"))
							.bind(any(SmppSessionConfiguration.class), any());
				}
				for (int i=numberOpened ; i<c.getClients().size() ; i++) {
					verify(c.getClients().get(i), never().description("session["+i+"] should not be opened"))
							.bind(any(SmppSessionConfiguration.class), any());
				}
				verify(c.getSupplier(), times(numberOpened).description(numberOpened+" clients should be created"))
							.get();
			} catch (InterruptedException | SmppTimeoutException | SmppChannelException | UnrecoverablePduException e) {
				throw new RuntimeException(e);
			}
		});
	}
	
	public static Verification opened(int numberOpened, Verification... additionalVerifications) {
		return context -> {
			SessionAware c = (SessionAware) context;
			MatcherAssert.assertThat(numberOpened+" session(s) should be opened", c.getSessions(), hasSize(numberOpened));
		};
	}
	
	public static Verification sent(int numSent, Function<TestContext, SmppSession> sessionProvider) {
		return context -> {
			try {
				SmppSession session = sessionProvider.apply(context);
				verify(session, times(numSent).description(session + " should have sent "+numSent+" EnquireLink(s)"))
						.enquireLink(any(), anyLong());
			} catch (InterruptedException | SmppTimeoutException | SmppChannelException | UnrecoverablePduException | RecoverablePduException e) {
				throw new RuntimeException(e);
			}
		};
	}

	public static int requests(int num) {
		return num;
	}
	
	public static Verification connectionAttempts(int attempts, Function<TestContext, SmppClient> clientProvider) {
		return context -> {
			try {
				SmppClient client = clientProvider.apply((ClientContext) context);
				verify(client, times(attempts).description(client+" should have attempted "+attempts+" connections"))
						.bind(any(SmppSessionConfiguration.class), any());
			} catch (SmppTimeoutException | SmppChannelException | UnrecoverablePduException | InterruptedException e) {
				throw new RuntimeException(e);
			}
		};
	}
	
	public static <T> Function<TestContext, T> by(Function<TestContext, T> provider) {
		return provider;
	}
	
	public static Function<TestContext, SmppSession> session(int sessionIndex) {
		return context -> {
			List<SmppSession> sessions = ((SessionAware) context).getSessions();
			return sessionIndex>=sessions.size() ? null : sessions.get(sessionIndex);
		};
	}
	
	public static Function<TestContext, SmppSession> session(SmppSession session) {
		return context -> {
			return session;
		};
	}
	
	public static Function<TestContext, SmppClient> client(int clientIndex) {
		return context -> {
			List<SmppClient> clients = ((SessionAware) context).getClients();
			return clientIndex>=clients.size() ? null : clients.get(clientIndex);
		};
	}
	
	public static Function<TestContext, SmppClient> client(SmppClient client) {
		return context -> {
			return client;
		};
	}
	
	public static Verification createdTimers(int numCreated) {
		return context -> {
			EnquireLinkTaskAware c = (EnquireLinkTaskAware) context;
			MatcherAssert.assertThat(numCreated + " timer(s) should be created", c.getTimers(), hasSize(numCreated));
			verify(c.getTimerFactory(), times(numCreated)).get();
			for (int i=0 ; i<numCreated ; i++) {
				verify(c.getTimers().get(i), times(1).description("timers["+i+"] should have scheduled 1 EnquireLinkTask"))
						.scheduleWithFixedDelay(any(), anyLong(), anyLong(), any());
			}
			for (int i=numCreated ; i<c.getTimers().size() ; i++) {
				verify(c.getTimers().get(i), never().description("timers["+i+"] should have not scheduled any task"))
						.scheduleWithFixedDelay(any(), anyLong(), anyLong(), any());
			}
		};
	}
	
	public static Verification createdTasks(int numCreated) {
		return context -> {
			EnquireLinkTaskAware c = (EnquireLinkTaskAware) context;
			MatcherAssert.assertThat(numCreated + " tasks(s) should be created", c.getEnquireLinkTasks(), hasSize(numCreated));
		};
	}
	
	public static Negatable cleaned() {
		return new Negatable() {
			@Override
			public void verify(TestContext context) {
				new CleanedOrNotVerification(1).verify(context);
			}
			
			@Override
			public Verification not() {
				return new CleanedOrNotVerification(0);
			}
		};
	}
	
	public static Verification opened() {
		return new SessionOpenedOrClosedVerification(0);
	}
	
	public static Verification closed() {
		return new SessionOpenedOrClosedVerification(1);
	}
	
	
	public static Verification not(Negatable verification) {
		return verification.not();
	}
	
	public static Verification stopped() {
		return new ActiveOrStoppedVerification(1);
	}
	
	public static Verification active() {
		return new ActiveOrStoppedVerification(0);
	}
	
	public static Verification isNull() {
		return context -> {
			if (context instanceof SessionContext) {
				assertThat("session should be null", ((SessionContext) context).getSession(), nullValue());
			} else if (context instanceof ClientContext) {
				assertThat("client should be null", ((ClientContext) context).getClient(), nullValue());
			} else if (context instanceof EnquireLinkRequestContext) {
				assertThat("timer should be null", ((EnquireLinkRequestContext) context).getTimer(), nullValue());
				assertThat("task should be null", ((EnquireLinkRequestContext) context).getTask(), nullValue());
			} else {
				throw new IllegalStateException("Can't verify using isNull() for "+context);
			}
		};
	}
	
	public static void waitUntil(Callable<Boolean> until) {
		LOG.debug("waiting until {}...", until);
		await().pollDelay(ofMillis(0)).pollInterval(ofMillis(10)).atMost(ofSeconds(10)).ignoreExceptions().until(until);
		LOG.debug("finished waiting until {}", until);
	}
	
	public static Callable<Boolean> enquireLinkReceived(SmppSession session, int num) {
		return new NamedCallable<>("enquireLinkReceived("+num+")", () -> {
			verify(session, times(num)).enquireLink(any(), anyLong());
			return true;
		});
	}
	
	public static Callable<Boolean> enquireLinkTimeout(SmppSession session) {
		return new NamedCallable<>("enquireLinkTimeout", () -> {
			verify(session, times(1)).enquireLink(any(), anyLong());
			return true;
		});
	}
	
	public static Callable<Boolean> enquireLinkFailureReceived(SmppSession session) {
		return new NamedCallable<>("enquireLinkFailureReceived", () -> {
			verify(session, times(1)).enquireLink(any(), anyLong());
			return true;
		});
	}
	
	public static Callable<Boolean> clientConnected(SmppClient client) {
		return new NamedCallable<>("clientConnected", () -> {
			verify(client, times(1)).bind(any(), any());
			return true;
		});
	}

	public static Callable<Boolean> reconnectionFailed(SmppClient client, int attempts) {
		return new NamedCallable<>("reconnectionFailed", () -> {
			verify(client, times(attempts)).bind(any(), any());
			verify(client, times(1)).destroy();
			return true;
		});
	}

	public static Callable<Boolean> expirationOfLastRequest(int delay) {
		long start = System.currentTimeMillis();
		return new NamedCallable<>("expirationOfLastRequest", () -> {
			return System.currentTimeMillis() - start >= delay;
		});
	}
	
	public static ScheduledExecutorService track(ScheduledExecutorService tracked, List<ScheduledFuture<?>> trackedTasks, int timerIdx) {
		return new IndexedTimerDecorator(new TrackScheduledTasksDecorator(tracked, trackedTasks), timerIdx);
	}
	
	public static SmppClient track(SmppClient tracked, List<SmppSession> trackedSessions, int clientIdx) {
		return new IndexedSmppClientDecorator(new TrackSessionsDecorator(tracked, trackedSessions), clientIdx);		
	}
	
	public static ScheduledFuture<?> track(ScheduledFuture<?> tracked, int taskIdx) {
		return new IndexedTaskDecorator<>(new SpayableScheduledFuture<>(tracked), taskIdx);
	}
	
	public static SmppSession track(SmppSession tracked, int sessionIdx) {
		return new IndexedSmppSessionDecorator(tracked, sessionIdx);
	}
	
	public interface TestContext {
	}

	
	public interface Verification {
		void verify(TestContext context);
	}

	public interface Negatable extends Verification {
		Verification not();
	}
	
	public interface SessionAware {
		List<SmppClient> getClients();
		List<SmppSession> getSessions();
		SmppClientSupplier getSupplier();
	}
	
	public interface EnquireLinkTaskAware {
		List<ScheduledExecutorService> getTimers();
		List<ScheduledFuture<?>> getEnquireLinkTasks();
		Supplier<ScheduledExecutorService> getTimerFactory();
	}
	
	public static class EnquireLinkRequestContext implements TestContext {
		private final ScheduledExecutorService timer;
		private final ScheduledFuture<?> task;
		public EnquireLinkRequestContext(ScheduledExecutorService timer, ScheduledFuture<?> task) {
			super();
			this.timer = timer;
			this.task = task;
		}
		public ScheduledExecutorService getTimer() {
			return timer;
		}
		public ScheduledFuture<?> getTask() {
			return task;
		}
		
	}

	
	public static class SessionContext implements TestContext {
		private final SmppSession session;

		public SessionContext(SmppSession session) {
			super();
			this.session = session;
		}

		public SmppSession getSession() {
			return session;
		}
	}
	
	public static class ClientContext implements TestContext {
		private final SmppClient client;

		public ClientContext(SmppClient client) {
			super();
			this.client = client;
		}

		public SmppClient getClient() {
			return client;
		}
	}

	
	private static class CleanedOrNotVerification implements Verification {
		private final int calls;
		
		public CleanedOrNotVerification(int calls) {
			super();
			this.calls = calls;
		}

		@Override
		public void verify(TestContext context) {
			if (context instanceof SessionContext) {
				new SessionOpenedOrClosedVerification(calls).verify(context);
			}
			if (context instanceof ClientContext) {
				new ClientCleanedOrNotVerification(calls).verify(context);
			}
		}
	}
	
	private static class SessionOpenedOrClosedVerification implements Verification {
		private final int calls;
		
		public SessionOpenedOrClosedVerification(int calls) {
			super();
			this.calls = calls;
		}

		@Override
		public void verify(TestContext context) {
			SmppSession session = ((SessionContext) context).getSession();
			assertThat("session should not be null", session, notNullValue());
			String haveOrNot = calls == 0 ? "not " : "";
			Mockito.verify(session, times(calls).description(session+" should "+haveOrNot+"have cleaned")).unbind(anyLong());
			Mockito.verify(session, times(calls).description(session+" should "+haveOrNot+"have cleaned")).destroy();
		}
	}

	private static class ClientCleanedOrNotVerification implements Verification {
		private final int calls;
		
		public ClientCleanedOrNotVerification(int calls) {
			super();
			this.calls = calls;
		}

		@Override
		public void verify(TestContext context) {
			SmppClient client = ((ClientContext) context).getClient();
			assertThat("client should not be null", client, notNullValue());
			String haveOrNot = calls == 0 ? "not " : "";
			Mockito.verify(client, times(calls).description(client+" should "+haveOrNot+"have been cleaned")).destroy();
		}
	}
	
	private static class ActiveOrStoppedVerification implements Verification {
		private final int calls;
		
		public ActiveOrStoppedVerification(int calls) {
			super();
			this.calls = calls;
		}

		@Override
		public void verify(TestContext context) {
			EnquireLinkRequestContext c = (EnquireLinkRequestContext) context;
			ScheduledExecutorService timer = c.getTimer();
			ScheduledFuture<?> task = c.getTask();
			assertThat("timer should not be null", timer, notNullValue());
			assertThat("task should not be null", task, notNullValue());
			String haveOrNot = calls == 0 ? "not " : "";
			Mockito.verify(timer, times(calls).description(timer+" should "+haveOrNot+"have been stopped")).shutdownNow();
			Mockito.verify(task, times(calls).description(task+" should "+haveOrNot+"have been stopped")).cancel(anyBoolean());
		}
	}

}
