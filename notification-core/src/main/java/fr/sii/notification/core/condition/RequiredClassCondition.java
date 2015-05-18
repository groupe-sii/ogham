package fr.sii.notification.core.condition;

import fr.sii.notification.core.util.ClasspathHelper;

/**
 * Condition that checks if the provided class is available in the classpath.
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            The type of the object to test for acceptance. Has no effect on
 *            the acceptance
 */
public class RequiredClassCondition<T> implements Condition<T> {
	/**
	 * The class to check if exists in the classpath
	 */
	private String className;

	/**
	 * Initialize the condition with the class name
	 * 
	 * @param className
	 *            The class to check availability for
	 */
	public RequiredClassCondition(String className) {
		super();
		this.className = className;
	}

	@Override
	public boolean accept(T obj) {
		return ClasspathHelper.exists(className);
	}

}
