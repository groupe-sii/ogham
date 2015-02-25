package fr.sii.notification.core.condition;

import java.util.Properties;


public class RequiredPropertyCondition<T> implements Condition<T> {

	private Properties properties;
	
	private String key;
	
	public RequiredPropertyCondition(String key) {
		this(key, System.getProperties());
	}

	public RequiredPropertyCondition(String key, Properties properties) {
		super();
		this.key = key;
		this.properties = properties;
	}

	@Override
	public boolean accept(T obj) {
		return properties.containsKey(key);
	}

}
