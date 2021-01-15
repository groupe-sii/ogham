package fr.sii.ogham.testing.mock.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ClassLoader} decorator that can be used to mock some missing classes.
 * It can be useful to test how some code behaves if a dependency is missing.
 * 
 * This is mostly useful for Ogham itself since it adatps to what is present in
 * the classpath.
 * 
 * NOTE: On some Java versions (especially Eclipse OpenJ9), the
 * {@link ClassLoader} initializes assertions directly in the constructor (see
 * https://github.com/eclipse/openj9/blob/cef51dfd748a15b6f8ceb9be065d8a9eb8389a6b/jcl/src/java.base/share/classes/java/lang/ClassLoader.java#L438).
 * So we need to register method calls to apply them once delegate has been set.
 * 
 * @author Aurélien Baudet
 *
 */
public class FilterableClassLoader extends ClassLoader {
	private static final Logger LOG = LoggerFactory.getLogger(FilterableClassLoader.class);

	private final ClassLoader delegate;
	private final Predicate<String> predicate;
	private final List<Consumer<ClassLoader>> lateCalls = new ArrayList<>();

	public FilterableClassLoader(ClassLoader delegate, Predicate<String> predicate) {
		super();
		this.delegate = delegate;
		this.predicate = predicate;
	}

	@SuppressWarnings("squid:S2658")
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if (predicate.test(name)) {
			return delegate.loadClass(name);
		} else {
			LOG.info("Class {} not accepted", name);
			throw new ClassNotFoundException("Class " + name + " not accepted");
		}
	}

	@Override
	public URL getResource(String name) {
		if (predicate.test(name)) {
			return delegate.getResource(name);
		} else {
			LOG.info("Resource {} not accepted", name);
			return null;
		}
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		if (predicate.test(name)) {
			return delegate.getResources(name);
		} else {
			LOG.info("Resources {} not accepted", name);
			return Collections.emptyEnumeration();
		}
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		if (predicate.test(name)) {
			return delegate.getResourceAsStream(name);
		} else {
			LOG.info("Resource {} not accepted", name);
			return null;
		}
	}

	@Override
	public void setDefaultAssertionStatus(boolean enabled) {
		super.setDefaultAssertionStatus(enabled);
//		if (delegate != null) {
			delegate.setDefaultAssertionStatus(enabled);
//		} else {
//			lateCalls.add(new SetDefaultAssertionStatusCall(enabled));
//		}
	}

	@Override
	public void setPackageAssertionStatus(String packageName, boolean enabled) {
		super.setPackageAssertionStatus(packageName, enabled);
//		if (delegate != null) {
			delegate.setPackageAssertionStatus(packageName, enabled);
//		} else {
//			lateCalls.add(new SetPackageAssertionStatusCall(packageName, enabled));
//		}
	}

	@Override
	public void setClassAssertionStatus(String className, boolean enabled) {
		super.setClassAssertionStatus(className, enabled);
//		if (delegate != null) {
			delegate.setClassAssertionStatus(className, enabled);
//		} else {
//			lateCalls.add(new SetClassAssertionStatusCall(className, enabled));
//		}
	}

	@Override
	public void clearAssertionStatus() {
		super.clearAssertionStatus();
//		if (delegate != null) {
			delegate.clearAssertionStatus();
//		} else {
//			lateCalls.add(new ClearAssertionStatusCall());
//		}
	}

	protected void applyLateCalls() {
		for (Consumer<ClassLoader> lateCall : lateCalls) {
			lateCall.accept(delegate);
		}
	}

	private static class SetDefaultAssertionStatusCall implements Consumer<ClassLoader> {
		private final boolean enabled;

		public SetDefaultAssertionStatusCall(boolean enabled) {
			super();
			this.enabled = enabled;
		}

		@Override
		public void accept(ClassLoader delegate) {
			delegate.setDefaultAssertionStatus(enabled);
		}
	}

	private static class SetPackageAssertionStatusCall implements Consumer<ClassLoader> {
		private final String packageName;
		private final boolean enabled;

		public SetPackageAssertionStatusCall(String packageName, boolean enabled) {
			super();
			this.packageName = packageName;
			this.enabled = enabled;
		}

		@Override
		public void accept(ClassLoader delegate) {
			delegate.setPackageAssertionStatus(packageName, enabled);
		}
	}

	private static class SetClassAssertionStatusCall implements Consumer<ClassLoader> {
		private final String className;
		private final boolean enabled;

		public SetClassAssertionStatusCall(String className, boolean enabled) {
			super();
			this.className = className;
			this.enabled = enabled;
		}

		@Override
		public void accept(ClassLoader delegate) {
			delegate.setPackageAssertionStatus(className, enabled);
		}
	}

	private static class ClearAssertionStatusCall implements Consumer<ClassLoader> {
		@Override
		public void accept(ClassLoader delegate) {
			delegate.clearAssertionStatus();
		}
	}
}
