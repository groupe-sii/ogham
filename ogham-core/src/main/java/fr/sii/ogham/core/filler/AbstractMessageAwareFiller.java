package fr.sii.ogham.core.filler;

import java.util.List;
import java.util.Map;

import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.filler.FillMessageException;
import fr.sii.ogham.core.message.Message;

/**
 * Base class to help to fill a particular {@link Message} type.
 * 
 * @author Aurélien Baudet
 *
 * @param <M>
 *            the type of the message that the filler is able to handle
 */
public abstract class AbstractMessageAwareFiller<M> implements MessageFiller {
	protected PropertyResolver resolver;
	protected Map<String, List<String>> aliases;
	private Class<M> messageType;

	/**
	 * The list of properties is indexed by an alias that is known by the
	 * implementation. For example, if the keys is defined like this:
	 * 
	 * <pre>
	 * Map&lt;String, List&lt;String&gt;&gt; keys = new HashMap&lt;&gt;();
	 * keys.put("to", Arrays.asList("ogham.email.to"));
	 * keys.put("from", Arrays.asList("ogham.email.from", "mail.smtp.from"));
	 * </pre>
	 * 
	 * The implementation can then retrieve real property value using map key
	 * (alias):
	 * 
	 * <pre>
	 * getProperty("from");
	 * // will return either the value of "ogham.email.from" or "mail.smtp.from"
	 * </pre>
	 * 
	 * @param resolver
	 *            the property resolver used to check property existence and get
	 *            property values
	 * @param aliases
	 *            a list of property keys indexed by an alias
	 * @param messageType
	 *            the class of the message that this implementation can handle
	 */
	protected AbstractMessageAwareFiller(PropertyResolver resolver, Map<String, List<String>> aliases, Class<M> messageType) {
		super();
		this.resolver = resolver;
		this.aliases = aliases;
		this.messageType = messageType;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void fill(Message message) throws FillMessageException {
		if (messageType.isAssignableFrom(message.getClass())) {
			fill((M) message);
		}
	}

	protected abstract void fill(M message);

	/**
	 * Return whether the given property alias has at least one property key
	 * that is available for resolution, i.e., the value for the given key is
	 * not {@code null}.
	 * 
	 * @param alias
	 *            the property alias to resolve
	 * @return true if property exists, false otherwise
	 */
	protected boolean containsProperty(String alias) {
		String k = resolveKey(alias);
		return k == null ? false : resolver.containsProperty(k);
	}

	/**
	 * Returns the value of first property represented by the provided alias
	 * that has a value (not {@code null}).
	 * 
	 * @param alias
	 *            the property alias to resolve
	 * @return the property value or null
	 */
	protected String getProperty(String alias) {
		String k = resolveKey(alias);
		return k == null ? null : resolver.getProperty(k);
	}

	/**
	 * Returns the value of first property represented by the provided alias
	 * that has a value (not {@code null}).
	 * 
	 * @param alias
	 *            the property alias to resolve
	 * @param targetType
	 *            the expected type of the property value
	 * @param <T>
	 *            the type of the property value
	 * @return The property value or null
	 */
	protected <T> T getProperty(String alias, Class<T> targetType) {
		String k = resolveKey(alias);
		return k == null ? null : resolver.getProperty(k, targetType);
	}

	/**
	 * Find the property key that represents the provided alias that has a value
	 * (not {@code null}).
	 * 
	 * @param alias
	 *            the property alias to resolve
	 * @return the property key or null if all keys represented by the alias
	 *         have no value
	 */
	protected String resolveKey(String alias) {
		List<String> possibleKeys = aliases.get(alias);
		for (String key : possibleKeys) {
			if (resolver.containsProperty(key)) {
				return key;
			}
		}
		return null;
	}
}
