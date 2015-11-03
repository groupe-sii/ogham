package fr.sii.ogham.email.builder.javamail;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import fr.sii.ogham.core.util.BuilderUtils;

public class OverrideJavaMailProperties extends Properties {
	private final Properties delegate;
	private List<String> hosts;
	private List<String> ports;
	private Integer port;

	public OverrideJavaMailProperties(Properties delegate, List<String> hosts, List<String> ports, Integer port) {
		super();
		this.delegate = delegate;
		this.hosts = hosts;
		this.ports = ports;
		this.port = port;
	}

	@Override
	public String getProperty(String key) {
		if(isPortKey(key) && getPortValue()!=null) {
			return getPortValue();
		}
		if(isHostKey(key) && getHostValue()!=null) {
			return getHostValue();
		}
		return delegate.getProperty(getOverridenKey(key));
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		if(isPortKey(key) && getPortValue()!=null) {
			return getPortValue();
		}
		if(isHostKey(key) && getHostValue()!=null) {
			return getHostValue();
		}
		return delegate.getProperty((String) getOverridenKey(key), defaultValue);
	}

	@Override
	public synchronized boolean containsKey(Object key) {
		if(isPortKey(key) && getPortValue()!=null) {
			return true;
		}
		if(isHostKey(key) && getHostValue()!=null) {
			return true;
		}
		return delegate.containsKey(getOverridenKey((String) key));
	}

	@Override
	public synchronized Object get(Object key) {
		if(isPortKey(key) && getPortValue()!=null) {
			return getPortValue();
		}
		if(isHostKey(key) && getHostValue()!=null) {
			return getHostValue();
		}
		return delegate.get(getOverridenKey((String) key));
	}

	@Override
	public Enumeration<?> propertyNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> stringPropertyNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void list(PrintStream out) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void list(PrintWriter out) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized Enumeration<Object> keys() {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized Enumeration<Object> elements() {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized boolean contains(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Object> keySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<java.util.Map.Entry<Object, Object>> entrySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<Object> values() {
		throw new UnsupportedOperationException();
	}

	private String getPortValue() {
		return port==null ? null : port.toString();
	}
	
	private boolean isHostKey(Object key) {
		return "mail.smtp.host".equals(key) || "mail.host".equals(key);
	}

	private boolean isPortKey(Object key) {
		return "mail.smtp.port".equals(key) || "mail.port".equals(key);
	}
	
	private boolean containsPropertyExpression(String prop) {
		return BuilderUtils.isExpression(prop) && delegate.containsKey(BuilderUtils.getPropertyKey(prop));
	}

	private String getHostValue() {
		for(String host : hosts) {
			if(!BuilderUtils.isExpression(host)) {
				return host;
			}
		}
		return null;
	}
	
	private String getOverridenKey(String key) {
		String overrideKey = key;
		if(isHostKey(key)) {
			for(String hostProp : hosts) {
				if(containsPropertyExpression(hostProp)) {
					overrideKey = BuilderUtils.getPropertyKey(hostProp);
					break;
				}
			}
		}
		if(isPortKey(key)) {
			for(String portProp : ports) {
				if(containsPropertyExpression(portProp)) {
					overrideKey = BuilderUtils.getPropertyKey(portProp);
					break;
				}
			}
		}
		return overrideKey;
	}

	@Override
	public synchronized String toString() {
		return delegate.toString();
	}
	
}