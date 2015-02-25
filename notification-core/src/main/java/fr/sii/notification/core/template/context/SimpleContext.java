package fr.sii.notification.core.template.context;

import java.util.HashMap;
import java.util.Map;

public class SimpleContext implements Context {

	private Map<String, Object> variables;
	
	public SimpleContext(String variable, Object value) {
		this(new HashMap<String, Object>());
		addValue(variable, value);
	}
	
	public SimpleContext(Map<String, Object> variables) {
		super();
		this.variables = variables;
	}

	public void addValue(String variable, Object value) {
		variables.put(variable, value);
	}

	@Override
	public Map<String, Object> getVariables() {
		return variables;
	}

}
