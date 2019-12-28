package fr.sii.ogham.core.util.classpath;

public interface ClasspathHelper {
	/**
	 * Test if the class name is defined in the classpath.
	 * 
	 * @param className
	 *            the class name
	 * @return true if the class exists in the classpath, false otherwise
	 */
	boolean exists(String className);
}