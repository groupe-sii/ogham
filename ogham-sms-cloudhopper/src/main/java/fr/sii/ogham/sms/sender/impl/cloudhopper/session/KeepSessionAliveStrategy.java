package fr.sii.ogham.sms.sender.impl.cloudhopper.session;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.pdu.EnquireLink;

import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.retry.RetryExecutor;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.builder.cloudhopper.SmppClientSupplier;
import fr.sii.ogham.sms.builder.cloudhopper.SmppSessionHandlerSupplier;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.sender.impl.cloudhopper.ExtendedSmppSessionConfiguration;
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.SmppException;

/**
 * Strategy that regularly sends {@link EnquireLink} requests to keep session
 * alive.
 * 
 * <p>
 * A session is initiated either at startup or when the first message is about
 * to be sent. Once the session is initialized, several messages can be sent
 * using the same session. Then it may take some time before a message is sent
 * again. In order to avoid initializing a new SMPP session again, the
 * connection with the server is maintained. To do so, a task is started in the
 * background in order to regularly send {@link EnquireLink} requests to the
 * server. This way the connection is maintained actively.
 * 
 * <p>
 * The opened session is automatically closed when the {@link MessagingService}
 * is released or when a cleanup is explicitly requested.
 * 
 * <p>
 * If the {@link EnquireLinkTask} fails, the error is analyzed using an
 * {@link ErrorAnalyzer}. This analyzer indicates if the triggered error is
 * raised because the connection with the server seems to be lost. In this case,
 * the current connection must be closed and a new session is created.
 * 
 * <p>
 * If a message can't be sent, the raised error is also analyzed to check
 * whether a new session has to be created. The failed SMS is not re-sent.
 * Instead the original error is thrown. This way, the global retry handling can
 * be used. A new session is requested in background.
 * 
 * <p>
 * If the reconnection fails (for example, if the server is down). The
 * reconnection process will be attempted only once (however there may have
 * several bind requests using the {@link RetryExecutor}). When a new message
 * has to be sent, the reconnection will be attempted at this moment.
 * 
 * @author Aurélien Baudet
 *
 */
public class KeepSessionAliveStrategy extends BaseSessionHandlingStrategy implements ErrorHandler {
	private static final Logger LOG = LoggerFactory.getLogger(KeepSessionAliveStrategy.class);

	private final Supplier<ScheduledExecutorService> timerSupplier;
	private final ErrorAnalyzer errorAnalyzer;
	private final ErrorHandler reconnectionErrorHandler;
	private ScheduledExecutorService currentTimer;
	private ScheduledFuture<?> enquireLinkTask;
	private volatile boolean reconnecting;

	public KeepSessionAliveStrategy(ExtendedSmppSessionConfiguration configuration, SmppClientSupplier clientSupplier, SmppSessionHandlerSupplier smppSessionHandlerSupplier, RetryExecutor retry,
			Supplier<ScheduledExecutorService> timerSupplier, ErrorAnalyzer errorAnalyzer, ErrorHandler reconnectionErrorHandler) {
		super(LOG, configuration, clientSupplier, smppSessionHandlerSupplier, retry);
		this.timerSupplier = timerSupplier;
		this.errorAnalyzer = errorAnalyzer;
		this.reconnectionErrorHandler = reconnectionErrorHandler;
		if (configuration.getKeepAlive().isConnectAtStartup(false)) {
			tryConnect();
		}
	}

	@Override
	public SmppSession getSession() throws SmppException {
		initSessionIfNeeded();
		return currentSession;
	}

	@Override
	public void messageSent(Sms sms) {
		// nothing to do
	}

	@Override
	public void messageNotSent(Sms sms, SmppException e) throws MessageException {
		if (errorAnalyzer.requiresNewConnection(e)) {
			reconnectInBackground(e);
			// Throw the original exception so that message may be handled
			// (maybe send will be retried)
			// Adds some contextual information if possible
			throw new MessageException("Failed to send SMS because it seems that the current session is broken. A new SMPP session is requested in background", sms, e);
		}
		throw new MessageException("Failed to send SMS", sms, e);
	}

	@Override
	public void handleFailure(Throwable e) {
		if (errorAnalyzer.requiresNewConnection(e)) {
			reconnectInBackground(e);
		}
	}

	@Override
	public void messageProcessed(Sms sms) {
		// nothing to do
	}

	@Override
	public void clean() {
		stopEnquiredLinkTask();
		destroySession();
		destroyClient();
	}

	private void tryConnect() {
		try {
			initSessionIfNeeded();
		} catch (SmppException e) {
			LOG.warn("Connection at startup was requested but couldn't be achived. Connection will be re-attempted when first message is sent", e);
		}
	}

	private void initSessionIfNeeded() throws SmppException {
		initClient();
		initSession();
		startEnquiredLinkTask();
	}

	private synchronized void startEnquiredLinkTask() {
		if (currentSession == null || isEnquireLinkTaskRunning()) {
			return;
		}
		// TODO: only run when no activity ? take the date of the last sent
		// message ?
		long enquireLinkRequestTimeout = configuration.getKeepAlive().getEnquireLinkTimeout();
		long enquireLinkInterval = configuration.getKeepAlive().getEnquireLinkInterval();
		LOG.debug("Start sending EnquireLink requests every {}ms", enquireLinkInterval);
		currentTimer = timerSupplier.get();
		enquireLinkTask = currentTimer.scheduleWithFixedDelay(new EnquireLinkTask(currentSession, this, enquireLinkRequestTimeout), enquireLinkInterval, enquireLinkInterval, MILLISECONDS);
	}

	private boolean isEnquireLinkTaskRunning() {
		return enquireLinkTask != null && !enquireLinkTask.isCancelled() && !enquireLinkTask.isDone();
	}

	private void stopEnquiredLinkTask() {
		if (currentTimer != null) {
			currentTimer.shutdownNow();
			currentTimer = null;
		}
		if (enquireLinkTask != null) {
			LOG.debug("Stop sending EnquireLink requests");
			enquireLinkTask.cancel(true);
			enquireLinkTask = null;
		}
	}

	private boolean tryReconnect(Throwable failureRequiringReconnection) {
		try {
			LOG.debug("Reconnecting due to error that requires a new session", failureRequiringReconnection);
			reconnect();
			LOG.debug("Reconnected");
			return true;
		} catch (SmppException se) {
			LOG.debug("Failed to reconnect. Stopping active keep alive... Reconnection and active keep alive will be attempted later (either using another strategy or when sending next message)",
					failureRequiringReconnection);
			clean();
			reconnectionErrorHandler.handleFailure(se);
			LOG.debug("Active keep alive stopped...", se);
		}
		return false;
	}

	private void reconnect() throws SmppException {
		clean();
		initSessionIfNeeded();
	}

	private boolean alreadyReconnecting() {
		return reconnecting;
	}

	private void reconnectInBackground(Throwable failureRequiringReconnection) {
		if (alreadyReconnecting()) {
			return;
		}
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(new ReconnectionTask(failureRequiringReconnection));
		executor.shutdown();
	}

	private class ReconnectionTask implements Runnable {
		private final Throwable failureRequiringReconnection;

		public ReconnectionTask(Throwable failureRequiringReconnection) {
			super();
			this.failureRequiringReconnection = failureRequiringReconnection;
		}

		@Override
		public void run() {
			reconnecting = true;
			tryReconnect(failureRequiringReconnection);
			reconnecting = false;
		}

	}
}
