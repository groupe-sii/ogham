package fr.sii.ogham.core.util;

import fr.sii.ogham.core.util.classpath.ClasspathHelper;
import fr.sii.ogham.core.util.classpath.SimpleClasspathHelper;

/**
 * Helper for classpath management.
 * 
 * @author Aurélien Baudet
 *
 */
public final class ClasspathUtils {
	private static ClasspathHelper helper;
	
	static {
		helper = new SimpleClasspathHelper();
	}

	/**
	 * Test if the class name is defined in the classpath.
	 *
	 * @param className
	 *            the class name
	 * @return true if the class exists in the classpath, false otherwise
	 */
	public static boolean exists(String className) {
		return helper.exists(className);
	}

	public static void setHelper(ClasspathHelper helper) {
		ClasspathUtils.helper = helper;
	}
	
	public static void reset() {
		ClasspathUtils.helper = new SimpleClasspathHelper();
	}
	
	private ClasspathUtils() {
		super();
	}

}
