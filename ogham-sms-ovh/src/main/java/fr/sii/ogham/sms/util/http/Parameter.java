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

	/**
	 * If {@code value} is null, keep null value. Otherwise {@code value} is
	 * converted to string using {@link String#valueOf(Object)}.
	 * 
	 * @param name
	 *            the parameter name
	 * @param value
	 *            the parameter value
	 */
	public Parameter(String name, Object value) {
		this(name, value == null ? null : String.valueOf(value));
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
}