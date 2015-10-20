package fr.sii.ogham.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper for classpath management.
 * 
 * @author Aurélien Baudet
 *
 */
public final class ClasspathHelper {
	private static final Logger LOG = LoggerFactory.getLogger(ClasspathHelper.class);
	
	/**
	 * Test if the class name is defined in the classpath.
	 * 
	 * @param className
	 *            the class name
	 * @return true if the class exists in the classpath, false otherwise
	 */
	public static boolean exists(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException e) {
			LOG.debug("Class "+className+" not found", e);
			return false;
		}
	}
	
	private ClasspathHelper() {
		super();
	}
}
