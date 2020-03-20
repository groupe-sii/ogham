package fr.sii.ogham.core.util;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;

/**
 * Utility class used by loggers to log only useful information.
 * 
 * According to the log level and the message, logged information is adapted:
 * <ul>
 * <li>If log level is configured to INFO, uses default {@link Email#toString()}
 * and {@link Sms#toString()} behavior i.e. log useful information about the
 * message (content is not displayed).</li>
 * <li>If log level is configured to TRACE or DEBUG, {@link Email#toLogString()}
 * and {@link Sms#toLogString()}, more information is displayed such as the
 * content</li>
 * <li>If log level is configured to WARN or ERROR, only an identifier is
 * logged.</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public final class LogUtils {
	private static final Logger LOG = LoggerFactory.getLogger(LogUtils.class);

	/**
	 * Generate a string to be logged for the provided message.
	 * 
	 * According to the log level and the message, logged information is
	 * adapted:
	 * <ul>
	 * <li>If log level is configured to INFO, uses default
	 * {@link Email#toString()} and {@link Sms#toString()} behavior i.e. log
	 * useful information about the message (content is not displayed).</li>
	 * <li>If log level is configured to TRACE or DEBUG,
	 * {@link Email#toLogString()} and {@link Sms#toLogString()}, more
	 * information is displayed such as the content</li>
	 * <li>If log level is configured to WARN or ERROR, only an identifier is
	 * logged.</li>
	 * </ul>
	 * 
	 * <p>
	 * The {@link Lazy} proxy is used in order to avoid early string
	 * construction while the log may be skipped.
	 * 
	 * @param message
	 *            the message to log
	 * @return an intermediate object that will be evaluated when log is written
	 *         only
	 */
	public static Lazy logString(Message message) {
		if (LOG.isDebugEnabled() || LOG.isTraceEnabled()) {
			return new Lazy(message::toString);
		}
		if (LOG.isInfoEnabled() && message instanceof Loggable) {
			return new Lazy(((Loggable) message)::toLogString);
		}
		// TODO: each message should have a unique identifier (useful for
		// debugging, logs, ...)? or use a hash
		return new Lazy("<hidden>");
	}

	/**
	 * Utility class that delegates {@link #toString()} call to a supplier that
	 * provides the real string.
	 * 
	 * <p>
	 * The aim is to construct tge string only when necessary.
	 * 
	 * @author Aurélien Baudet
	 *
	 */
	public static class Lazy {
		private final Supplier<String> supplier;

		public Lazy(String str) {
			this(() -> str);
		}

		public Lazy(Supplier<String> supplier) {
			super();
			this.supplier = supplier;
		}

		@Override
		public String toString() {
			return supplier.get();
		}
	}

	private LogUtils() {
		super();
	}
}
