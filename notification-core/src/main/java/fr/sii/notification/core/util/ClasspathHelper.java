package fr.sii.notification.core.util;

/**
 * Helper for classpath management.
 * 
 * @author Aurélien Baudet
 *
 */
public class ClasspathHelper {
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
			return false;
		}
	}
}
