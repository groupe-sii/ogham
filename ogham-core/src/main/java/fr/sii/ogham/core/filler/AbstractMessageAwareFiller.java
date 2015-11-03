package fr.sii.ogham.core.filler;

import java.util.List;
import java.util.Map;

import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.filler.FillMessageException;
import fr.sii.ogham.core.message.Message;

public abstract class AbstractMessageAwareFiller<M> implements MessageFiller {
	protected PropertyResolver resolver;
	protected Map<String, List<String>> keys;
	private Class<M> messageType;

	public AbstractMessageAwareFiller(PropertyResolver resolver, Map<String, List<String>> keys, Class<M> messageType) {
		super();
		this.resolver = resolver;
		this.keys = keys;
		this.messageType = messageType;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void fill(Message message) throws FillMessageException {
		if(messageType.isAssignableFrom(message.getClass())) {
			fill((M) message);
		}
	}

	protected abstract void fill(M message);

	protected boolean containsProperty(String keyName) {
		String k = resolveKey(keyName);
		return k==null ? false : resolver.containsProperty(k);
	}
	
	protected String getProperty(String keyName) {
		String k = resolveKey(keyName);
		return k==null ? null : resolver.getProperty(k);
	}

	protected <T> T getProperty(String keyName, Class<T> targetType) {
		String k = resolveKey(keyName);
		return k==null ? null : resolver.getProperty(k, targetType);
	}

	protected String resolveKey(String keyName) {
		List<String> possibleKeys = keys.get(keyName);
		for(String key : possibleKeys) {
			if(resolver.containsProperty(key)) {
				return key;
			}
		}
		return null;
	}
}
