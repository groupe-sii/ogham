package fr.sii.ogham.sms.sender.impl.cloudhopper.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 * Handler that just write the error in the logs according to the configured
 * level.
 * 
 * @author Aurélien Baudet
 *
 */
public class LogErrorHandler implements ErrorHandler {
	private static final Logger LOG = LoggerFactory.getLogger(LogErrorHandler.class);

	private final String message;
	private final Level level;

	public LogErrorHandler(String message, Level level) {
		super();
		this.message = message;
		this.level = level;
	}

	@SuppressWarnings({ "squid:S00122", "squid:IndentationCheck" })
	@Override
	public void handleFailure(Throwable failure) {
		// @formatter:off
		switch (level) {
			case TRACE:		LOG.trace(message, failure);	break;
			case DEBUG:		LOG.debug(message, failure);	break;
			case INFO:		LOG.info(message, failure);		break;
			case WARN:		LOG.warn(message, failure);		break;
			case ERROR:		LOG.error(message, failure);	break;
		}
		// @formatter:on
	}

}
