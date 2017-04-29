package fr.sii.ogham.email.sender.impl;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;

import fr.sii.ogham.core.env.PropertyResolver;

public class PropertiesBridge extends Properties {
	private static final long serialVersionUID = 1L;
	private final transient PropertyResolver propertyResolver;
	
	public PropertiesBridge(PropertyResolver propertyResolver) {
		super();
		this.propertyResolver = propertyResolver;
	}

	@Override
	public String getProperty(String key) {
		return propertyResolver.getProperty(key);
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		return propertyResolver.getProperty(key, defaultValue);
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
	public synchronized boolean containsKey(Object key) {
		return propertyResolver.containsProperty((String) key);
	}

	@Override
	public synchronized Object get(Object key) {
		return propertyResolver.getProperty((String) key, Object.class);
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

}
