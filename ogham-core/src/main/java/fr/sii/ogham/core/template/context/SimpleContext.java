package fr.sii.ogham.core.template.context;

import java.util.HashMap;
import java.util.Map;

import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;

/**
 * Simple context that stores variable substitutions into a map. This map in
 * then provided directly when calling {@link #getVariables()}.
 * 
 * @author Aurélien Baudet
 *
 */
public class SimpleContext implements Context {

	/**
	 * The variable values indexed by the variable names
	 */
	private Map<String, Object> variables;

	/**
	 * Initialize the variables with only one entry.
	 * 
	 * @param variable
	 *            the name of the variable
	 * @param value
	 *            the value of the variable
	 */
	public SimpleContext(String variable, Object value) {
		this(new HashMap<>());
		addValue(variable, value);
	}

	/**
	 * Initialize the variables with the provided map. The map is used as is.
	 * 
	 * @param variables
	 *            the variable values indexed by the variable names
	 */
	public SimpleContext(Map<String, Object> variables) {
		super();
		this.variables = variables;
	}

	/**
	 * Add a new variable entry.
	 * 
	 * @param variable
	 *            the name of the variable
	 * @param value
	 *            the value of the variable
	 * @return the context for fluent use
	 */
	public final SimpleContext addValue(String variable, Object value) {
		variables.put(variable, value);
		return this;
	}

	@Override
	public Map<String, Object> getVariables() {
		return variables;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(variables).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("variables").isEqual();
	}
}
