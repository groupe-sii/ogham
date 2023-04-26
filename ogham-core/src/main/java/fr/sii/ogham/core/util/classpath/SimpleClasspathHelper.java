package fr.sii.ogham.core.util.classpath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Helper for classpath management.
 * 
 * @author Aurélien Baudet
 *
 */
public class SimpleClasspathHelper implements ClasspathHelper {
	private static final Logger LOG = LoggerFactory.getLogger(SimpleClasspathHelper.class);
	
	private ClassLoader classLoader;
	
	public SimpleClasspathHelper() {
		super();
		classLoader = null;
	}

	/**
	 * Test if the class name is defined in the classpath.
	 * 
	 * @param className
	 *            the class name
	 * @return true if the class exists in the classpath, false otherwise
	 */
	public boolean exists(String className) {
		if(this.classLoader!=null) {
			if(exists(className, this.classLoader)) {
				LOG.debug("class {} found using class specific class loader", className);
				return true;
			}
			return false;
		}
		if(existsWithDefaultClassLoader(className)) {
			LOG.debug("class {} found using default class loader", className);
			return true;
		}
		if(exists(className, Thread.currentThread().getContextClassLoader())) {
			LOG.debug("class {} found using class loader of current thread", className);
			return true;
		}
		if(exists(className, getClass().getClassLoader())) {
			LOG.debug("class {} found using class loader of current class", className);
			return true;
		}
		return false;
	}

	private static boolean existsWithDefaultClassLoader(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException e) {
			LOG.debug("Class {} not found", className);
			LOG.trace("Cause:", e);
			return false;
		} catch (NoClassDefFoundError e) {
			LOG.debug("Class {} can't be loaded", className);
			LOG.trace("Cause:", e);
			return isForSameClass(className, e);
		}
	}

	private static boolean exists(String className, ClassLoader classLoader) {
		try {
			Class.forName(className, false, classLoader);
			return true;
		} catch (ClassNotFoundException e) {
			LOG.debug("Class {} not found", className);
			LOG.trace("Cause:", e);
			return false;
		} catch (NoClassDefFoundError e) {
			LOG.debug("Class {} can't be loaded", className);
			LOG.trace("Cause:", e);
			return !isForSameClass(className, e);
		}
	}

	private static boolean isForSameClass(String className, NoClassDefFoundError e) {
		return className.equals(e.getMessage().replace("/", "."));
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public void resetClassLoader() {
		this.classLoader = null;
	}
}