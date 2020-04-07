package fr.sii.ogham.core.sender;

import static fr.sii.ogham.core.retry.NamedCallable.named;

import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.exception.MessageNotSentException;
import fr.sii.ogham.core.exception.retry.RetryException;
import fr.sii.ogham.core.exception.retry.RetryExecutionInterruptedException;
import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.retry.RetryExecutor;
import fr.sii.ogham.core.retry.RetryStrategy;

/**
 * If a message couldn't be sent, the message may be sent again several times
 * until it succeeds or maximum attempts are reached.
 * 
 * <p>
 * The retry management is delegated to a {@link RetryExecutor}.
 * 
 * @author Aurélien Baudet
 * @see RetryExecutor
 * @see RetryStrategy
 */
public class AutoRetrySender implements ConditionalSender {
	private final MessageSender delegate;
	private final RetryExecutor retry;

	/**
	 * Initializes with the wrapped sender that will really send the message and
	 * the {@link RetryExecutor} used to execute the action several times if
	 * needed.
	 * 
	 * @param delegate
	 *            the sender that really sends the messages
	 * @param retry
	 *            the retry manager
	 */
	public AutoRetrySender(MessageSender delegate, RetryExecutor retry) {
		super();
		this.delegate = delegate;
		this.retry = retry;
	}

	@Override
	public void send(Message message) throws MessageException {
		try {
			retry.execute(named("Send message", () -> delegate.send(message)));
		} catch (RetryExecutionInterruptedException e) {
			Thread.currentThread().interrupt();
			throw new MessageNotSentException("Failed to send message (interrupted)", message, e);
		} catch (RetryException e) {
			throw new MessageNotSentException("Failed to send message", message, e);
		}
	}

	@Override
	public boolean supports(Message message) {
		if (delegate instanceof ConditionalSender) {
			return ((ConditionalSender) delegate).supports(message);
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AutoRetrySender [executor=").append(retry).append(", delegate=").append(delegate).append("]");
		return builder.toString();
	}
}
