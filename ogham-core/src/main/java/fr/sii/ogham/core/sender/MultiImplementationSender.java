package fr.sii.ogham.core.sender;

import static fr.sii.ogham.core.util.LogUtils.summarize;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.util.PriorizedList;

/**
 * Decorator sender that is able to handle a particular type of message. And for
 * handling this message it can rely on several possible implementations. Each
 * implementation is associated to a {@link Condition}. The condition indicates
 * at runtime if the message can be handled by the possible implementation.
 * There can be any kind of condition (for example, based on a required class in
 * the classpath or a particular property value...).
 * 
 * The implementation selection is done in the {@link #supports(Message)}
 * method.
 * 
 * @author Aurélien Baudet
 *
 * @param <M>
 *            The type of message that the implementations can handle
 * @see Condition
 */
public abstract class MultiImplementationSender<M extends Message> implements ConditionalSender {
	private static final Logger LOG = LoggerFactory.getLogger(MultiImplementationSender.class);

	/**
	 * The list of possible implementations indexed by the associated condition
	 */
	private final PriorizedList<Implementation> implementations;

	/**
	 * Initialize with no registered implementation.
	 */
	public MultiImplementationSender() {
		this(new PriorizedList<>());
	}

	/**
	 * Initialize with several implementations.
	 * 
	 * @param implementations
	 *            the list of possible implementations indexed by the condition
	 *            that indicates if the implementation is eligible at runtime
	 */
	public MultiImplementationSender(PriorizedList<Implementation> implementations) {
		super();
		this.implementations = implementations;
	}

	/**
	 * Register a new possible implementation with the associated condition. The
	 * implementation is added at the end so any other possible implementation
	 * will be used before this one if the associated condition allow it.
	 * 
	 * @param condition
	 *            the condition that indicates if the implementation can be used
	 *            at runtime
	 * @param implementation
	 *            the implementation to register
	 * @param priority
	 *            the registration priority
	 * @return this instance for fluent chaining
	 */
	public final MultiImplementationSender<M> addImplementation(Condition<Message> condition, MessageSender implementation, int priority) {
		implementations.register(new Implementation(condition, implementation), priority);
		return this;
	}

	@Override
	public boolean supports(Message message) {
		return getSender(message) != null;
	}

	@Override
	public void send(Message message) throws MessageException {
		MessageSender sender = getSender(message);
		if (sender == null) {
			LOG.warn("No implementation is able to send the message {}. Skipping", summarize(message));
			return;
		}
		LOG.debug("Sending message {} using {} implementation", summarize(message), sender);
		sender.send(message);
	}

	public List<Implementation> getImplementations() {
		return implementations.getOrdered();
	}

	protected boolean supportsMessageType(Message message) {
		return message.getClass().isAssignableFrom(getManagedClass());
	}

	@SuppressWarnings("unchecked")
	protected Class<M> getManagedClass() {
		Type genericSuperclass = getClass().getGenericSuperclass();
		if (genericSuperclass instanceof ParameterizedType) {
			return (Class<M>) ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
		}
		return null;
	}

	private MessageSender getSender(Message message) {
		if (supportsMessageType(message)) {
			LOG.debug("Can handle the message type {}. Is there any implementation available to send it ?", message.getClass());
			for (Implementation impl : implementations.getOrdered()) {
				if (impl.getCondition().accept(message)) {
					LOG.debug("The implementation {} can handle the message {}", impl.getSender(), summarize(message));
					return impl.getSender();
				}
			}
		}
		LOG.debug("Can't handle the message type {}", message.getClass());
		return null;
	}

	public static class Implementation {
		private final Condition<Message> condition;
		private final MessageSender sender;

		public Implementation(Condition<Message> condition, MessageSender sender) {
			super();
			this.condition = condition;
			this.sender = sender;
		}

		public Condition<Message> getCondition() {
			return condition;
		}

		public MessageSender getSender() {
			return sender;
		}
	}
}