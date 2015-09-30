package fr.sii.ogham.core.util;

import java.util.ArrayList;
import java.util.List;

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
	
	public static <T> List<Class<? extends T>> getImplementations(Class<T> type) {
		
	}
	
	public static <T, M> List<Class<? extends T>> getImplementations(Class<T> type, Class<M> messageType) {
		List<Class<? extends T>> implementations = getImplementations(type);
		List<Class<? extends T>> filtered = new ArrayList<>(implementations.size());
		for(Class<? extends T> implementation : implementations) {
		}
		return filtered;
	}
	
	public static void setHelper(ClasspathHelper helper) {
		ClasspathUtils.helper = helper;
	}
	
	private ClasspathUtils() {
		super();
	}

}
