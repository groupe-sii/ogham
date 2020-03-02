package fr.sii.ogham.core.filler;

import java.util.Map;

import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
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
	protected final PropertyResolver resolver;
	protected final Map<String, ConfigurationValueBuilderHelper<?, ?>> defaultValues;
	private final Class<M> messageType;

	/**
	 * The list of properties is indexed by an alias that is known by the
	 * implementation. For example, if the keys is defined like this:
	 * 
	 * <pre>
	 * Map&lt;String, List&lt;String&gt;&gt; keys = new HashMap&lt;&gt;();
	 * keys.put("to", valueBuilder.properties("ogham.email.to.default-value"));
	 * keys.put("from", valueBuilder.properties("ogham.email.from.default-value", "mail.smtp.from"));
	 * </pre>
	 * 
	 * The implementation can then retrieve real property value using map key
	 * (alias):
	 * 
	 * <pre>
	 * getProperty("from");
	 * // will return either the value of "ogham.email.from.default-value" or
	 * // "mail.smtp.from"
	 * </pre>
	 * 
	 * @param resolver
	 *            the property resolver used to check property existence and get
	 *            property values
	 * @param defaultValues
	 *            a list of property keys indexed by an alias
	 * @param messageType
	 *            the class of the message that this implementation can handle
	 */
	protected AbstractMessageAwareFiller(PropertyResolver resolver, Map<String, ConfigurationValueBuilderHelper<?, ?>> defaultValues, Class<M> messageType) {
		super();
		this.resolver = resolver;
		this.defaultValues = defaultValues;
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
		ConfigurationValueBuilderHelper<?, ?> valueBuilder = defaultValues.get(alias);
		return valueBuilder != null && valueBuilder.getValue(resolver) != null;
	}

	/**
	 * Returns the value of first property represented by the provided alias
	 * that has a value (not {@code null}).
	 * 
	 * @param alias
	 *            the property alias to resolve
	 * @param valueClass
	 *            the class of the resulting value
	 * @param <T>
	 *            the type of the resulting value
	 * @return the property value or null
	 */
	@SuppressWarnings("unchecked")
	protected <T> T getProperty(String alias, Class<T> valueClass) {
		ConfigurationValueBuilderHelper<?, T> valueBuilder = (ConfigurationValueBuilderHelper<?, T>) defaultValues.get(alias);
		return valueBuilder == null ? null : valueBuilder.getValue(resolver);
	}
}
