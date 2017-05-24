package fr.sii.ogham.email.builder.javamail;

import static fr.sii.ogham.core.util.BuilderUtils.getPropertyKey;
import static fr.sii.ogham.core.util.BuilderUtils.isExpression;

import java.util.List;

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
 * It works the same for ports but in addition, if the port value is defined as
 * an integer, it is used directly.
 * 
 * @author Aurélien Baudet
 *
 */
public class OverrideJavaMailResolver implements PropertyResolver {
	private final PropertyResolver delegate;
	private final Converter converter;
	private final List<String> hosts;
	private final List<String> ports;
	private final Integer port;

	public OverrideJavaMailResolver(PropertyResolver delegate, Converter converter, List<String> hosts, List<String> ports, Integer port) {
		super();
		this.delegate = delegate;
		this.converter = converter;
		this.hosts = hosts;
		this.ports = ports;
		this.port = port;
	}

	@Override
	public boolean containsProperty(String key) {
		if (getDirectValue(key) != null) {
			return true;
		}
		return delegate.containsProperty(getOverridenKey((String) key));
	}

	@Override
	public String getProperty(String key) {
		String value = getDirectValue(key);
		if (value != null) {
			return value;
		}
		return delegate.getProperty(getOverridenKey(key));
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		String value = getDirectValue(key);
		if (value != null) {
			return value;
		}
		return delegate.getProperty((String) getOverridenKey(key), defaultValue);
	}

	@Override
	public <T> T getProperty(String key, Class<T> targetType) {
		String value = getDirectValue(key);
		if (value != null) {
			return converter.convert(value, targetType);
		}
		return delegate.getProperty(getOverridenKey(key), targetType);
	}

	@Override
	public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
		String value = getDirectValue(key);
		if (value != null) {
			return converter.convert(value, targetType);
		}
		return delegate.getProperty(getOverridenKey(key), targetType, defaultValue);
	}

	@Override
	public String getRequiredProperty(String key) throws IllegalStateException {
		String value = getDirectValue(key);
		if (value != null) {
			return value;
		}
		return delegate.getRequiredProperty(getOverridenKey(key));
	}

	@Override
	public <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException {
		String value = getDirectValue(key);
		if (value != null) {
			return converter.convert(value, targetType);
		}
		return delegate.getRequiredProperty(getOverridenKey(key), targetType);
	}

	private String getDirectValue(String key) {
		if (isPortKey(key) && getPortValue() != null) {
			return getPortValue();
		}
		if (isHostKey(key) && getHostValue() != null) {
			return getHostValue();
		}
		return null;
	}

	private String getPortValue() {
		return port == null ? null : port.toString();
	}

	private boolean isHostKey(Object key) {
		return "mail.smtp.host".equals(key) || "mail.host".equals(key);
	}

	private boolean isPortKey(Object key) {
		return "mail.smtp.port".equals(key) || "mail.port".equals(key);
	}

	private boolean containsPropertyExpression(String prop) {
		return isExpression(prop) && delegate.containsProperty(getPropertyKey(prop));
	}

	private String getHostValue() {
		for (String host : hosts) {
			if (!isExpression(host)) {
				return host;
			}
		}
		return null;
	}

	private String getOverridenKey(String key) {
		String overrideKey = key;
		if (isHostKey(key)) {
			for (String hostProp : hosts) {
				if (containsPropertyExpression(hostProp)) {
					overrideKey = getPropertyKey(hostProp);
					break;
				}
			}
		}
		if (isPortKey(key)) {
			for (String portProp : ports) {
				if (containsPropertyExpression(portProp)) {
					overrideKey = getPropertyKey(portProp);
					break;
				}
			}
		}
		return overrideKey;
	}
}