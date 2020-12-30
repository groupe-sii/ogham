package fr.sii.ogham.testing.mock.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilterableClassLoader extends ClassLoader {
	private static final Logger LOG = LoggerFactory.getLogger(FilterableClassLoader.class);
	
	private ClassLoader delegate;
	private Predicate<String> predicate;

	public FilterableClassLoader(ClassLoader delegate, Predicate<String> predicate) {
		super();
		this.delegate = delegate;
		this.predicate = predicate;
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if(predicate.test(name)) {
			return delegate.loadClass(name);
		} else {
			LOG.info("Class {} not accepted", name);
			throw new ClassNotFoundException("Class "+name+" not accepted");
		}
	}

	@Override
	public URL getResource(String name) {
		if(predicate.test(name)) {
			return delegate.getResource(name);
		} else {
			LOG.info("Resource {} not accepted", name);
			return null;
		}
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		if(predicate.test(name)) {
			return delegate.getResources(name);
		} else {
			LOG.info("Resources {} not accepted", name);
			return Collections.emptyEnumeration();
		}
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		if(predicate.test(name)) {
			return delegate.getResourceAsStream(name);
		} else {
			LOG.info("Resource {} not accepted", name);
			return null;
		}
	}

	@Override
	public void setDefaultAssertionStatus(boolean enabled) {
		super.setDefaultAssertionStatus(enabled);
		if (delegate != null) {
			delegate.setDefaultAssertionStatus(enabled);
		}
		// TODO: should set value when delegate is initialized ?
	}

	@Override
	public void setPackageAssertionStatus(String packageName, boolean enabled) {
		super.setPackageAssertionStatus(packageName, enabled);
		if (delegate != null) {
			delegate.setPackageAssertionStatus(packageName, enabled);
		}
		// TODO: should set values when delegate is initialized ?
	}

	@Override
	public void setClassAssertionStatus(String className, boolean enabled) {
		super.setClassAssertionStatus(className, enabled);
		if (delegate != null) {
			delegate.setClassAssertionStatus(className, enabled);
		}
		// TODO: should set values when delegate is initialized ?
	}

	@Override
	public void clearAssertionStatus() {
		super.clearAssertionStatus();
		if (delegate != null) {
			delegate.clearAssertionStatus();
		}
		// TODO: should call when delegate is initialized ?
	}
	
	
}
