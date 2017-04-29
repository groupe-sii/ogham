package fr.sii.ogham.core.filler;

import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.filler.FillMessageException;
import fr.sii.ogham.core.message.Message;

public abstract class AbstractMessageAwareFiller<T> implements MessageFiller {
	protected PropertyResolver resolver;
	protected String baseKey;
	private Class<T> messageType;

	public AbstractMessageAwareFiller(PropertyResolver resolver, String baseKey, Class<T> messageType) {
		super();
		this.resolver = resolver;
		this.baseKey = baseKey;
		this.messageType = messageType;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void fill(Message message) throws FillMessageException {
		if(messageType.isAssignableFrom(message.getClass())) {
			fill((T) message);
		}
	}

	protected abstract void fill(T message);

	protected String resolveKey(String keyName) {
		return baseKey + "." + keyName;
	}
}
