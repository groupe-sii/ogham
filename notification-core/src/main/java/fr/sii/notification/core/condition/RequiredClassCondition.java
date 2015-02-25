package fr.sii.notification.core.condition;

import fr.sii.notification.core.util.ClasspathHelper;

public class RequiredClassCondition<T> implements Condition<T> {

	private String className;
	
	public RequiredClassCondition(String className) {
		super();
		this.className = className;
	}

	@Override
	public boolean accept(T obj) {
		return ClasspathHelper.exists(className);
	}

}
