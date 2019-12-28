package fr.sii.ogham.email.builder.javamail;

import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.convert.Converter;
import fr.sii.ogham.core.env.PropertyResolver;

/**
 * Decorate original {@link PropertyResolver} to override some values.
 * 
 * For example, if a list of hosts is provided with the following values
 * <code>"ogham.email.host", "mail.smtp.host"</code> then when caller asks for
 * <code>"mail.host"</code>, it returns:
 * <ul>
 * <li>the value of the property "ogham.email.host" if it exists</li>
 * <li>the value of the property "mail.smtp.host" if "ogham.email.host" doesn't
 * exist and "mail.smtp.host" exists</li>
 * <li>the value of "mail.host" otherwise</li>
 * </ul>
 * 
 * It works the same for ports.
 * 
 * @author Aurélien Baudet
 *
 */
public class OverrideJavaMailResolver implements PropertyResolver {
	private final PropertyResolver delegate;
	private final Converter converter;
	private final ConfigurationValueBuilderHelper<?, String> host;
	private final ConfigurationValueBuilderHelper<?, Integer> port;

	public OverrideJavaMailResolver(PropertyResolver delegate, Converter converter, ConfigurationValueBuilderHelper<?, String> host, ConfigurationValueBuilderHelper<?, Integer> port) {
		super();
		this.delegate = delegate;
		this.converter = converter;
		this.host = host;
		this.port = port;
	}

	@Override
	public boolean containsProperty(String key) {
		if (getValue(key) != null) {
			return true;
		}
		return delegate.containsProperty(key);
	}

	@Override
	public String getProperty(String key) {
		String value = getValue(key);
		if (value != null) {
			return value;
		}
		return delegate.getProperty(key);
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		String value = getValue(key);
		if (value != null) {
			return value;
		}
		return delegate.getProperty(key, defaultValue);
	}

	@Override
	public <T> T getProperty(String key, Class<T> targetType) {
		String value = getValue(key);
		if (value != null) {
			return converter.convert(value, targetType);
		}
		return delegate.getProperty(key, targetType);
	}

	@Override
	public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
		String value = getValue(key);
		if (value != null) {
			return converter.convert(value, targetType);
		}
		return delegate.getProperty(key, targetType, defaultValue);
	}

	@Override
	public String getRequiredProperty(String key) {
		String value = getValue(key);
		if (value != null) {
			return value;
		}
		return delegate.getRequiredProperty(key);
	}

	@Override
	public <T> T getRequiredProperty(String key, Class<T> targetType) {
		String value = getValue(key);
		if (value != null) {
			return converter.convert(value, targetType);
		}
		return delegate.getRequiredProperty(key, targetType);
	}

	private String getValue(String key) {
		if (isPortKey(key) && getPortValue() != null) {
			return getPortValue();
		}
		if (isHostKey(key) && getHostValue() != null) {
			return getHostValue();
		}
		return null;
	}

	private String getPortValue() {
		Integer value = port.getValue(delegate);
		if (value == null) {
			return null;
		}
		return String.valueOf(value);
	}

	private static boolean isHostKey(Object key) {
		return "mail.smtp.host".equals(key) || "mail.host".equals(key);
	}

	private static boolean isPortKey(Object key) {
		return "mail.smtp.port".equals(key) || "mail.port".equals(key);
	}

	private String getHostValue() {
		return host.getValue(delegate);
	}
}