package fr.sii.ogham.core.util.classpath;


public class SimpleClasspathHelper implements ClasspathHelper {
	private ClassLoader classLoader;

	/**
	 * Test if the class name is defined in the classpath.
	 * 
	 * @param className
	 *            the class name
	 * @return true if the class exists in the classpath, false otherwise
	 */
	public boolean exists(String className) {
		try {
			Class.forName(className, false, this.classLoader == null ? Thread.currentThread().getContextClassLoader() : this.classLoader);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public void resetClassLoader() {
		this.classLoader = null;
	}
}