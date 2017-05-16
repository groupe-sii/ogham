package fr.sii.ogham.sms.util.http;

/**
 * Simple abstraction for manipulating parameters that has a name and a value.
 * 
 * @author Aurélien Baudet
 *
 */
public class Parameter {
	/**
	 * The name of the parameter
	 */
	private final String name;

	/**
	 * The parameter value
	 */
	private final String value;

	public Parameter(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
}