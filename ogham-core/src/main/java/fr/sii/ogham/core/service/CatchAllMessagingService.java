package fr.sii.ogham.core.service;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.Message;

/**
 * Decorator that catch all exceptions including {@link RuntimeException}. It
 * translates other exceptions into {@link MessagingException}.
 * 
 * @author Aurélien Baudet
 */
public class CatchAllMessagingService implements MessagingService {
	/**
	 * The delegate service that will really send messages
	 */
	private MessagingService delegate;
	
	public CatchAllMessagingService(MessagingService delegate) {
		super();
		this.delegate = delegate;
	}

	/**
	 * Sends the message. The message can be anything with any content and that
	 * must be delivered to something or someone.
	 * 
	 * If there is any exception, it caught and translated in {@link MessagingException}.
	 * 
	 * @param message
	 *            the message to send
	 * @throws MessagingException
	 *             when the message couldn't be sent
	 */
	@Override
	public void send(Message message) throws MessagingException {
		try {
			delegate.send(message);
		} catch(MessagingException e) {
			throw e;
		} catch(IllegalArgumentException e) {
			throw new MessagingException("Message can't be sent due to precondition not met. Cause: "+e.getMessage(), e);
		} catch(IllegalStateException e) {
			throw new MessagingException("Message can't be sent due to some illegal use. Cause: "+e.getMessage(), e);
		} catch(Throwable e) {
			throw new MessagingException("Message can't be sent due to uncaught exception. Cause: "+e.getMessage(), e);
		}
	}
}
