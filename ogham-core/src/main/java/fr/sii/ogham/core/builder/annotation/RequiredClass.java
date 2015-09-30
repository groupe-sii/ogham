package fr.sii.ogham.core.builder.annotation;

/**
 * This annotation is used with {@link RequiredClasses} and its aim is to
 * provide more options on how to handle required classes.
 * 
 * <p>
 * This annotation is used to indicate the following information:
 * <ul>
 * <li>One of several class must be present</li>
 * <li>One or several classes must not be present</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public @interface RequiredClass {
	/**
	 * The required class
	 * 
	 * @return the require class
	 */
	String value();

	/**
	 * If one of the classes provided by this attribute is in the classpath,
	 * then the implementation can't be used.
	 * 
	 * <p>
	 * This attribute can be used in conjunction with {@link #value()}.
	 *
	 * For example:
	 * 
	 * <pre>
	 * &#064;RequiredClass(value="com.sendgrid.SendGrid", except="com.cloudhopper.smpp.SmppSession")
	 * </pre>
	 * 
	 * Means that the class <code>com.sendgrid.SendGrid</code> must be present.
	 * If the classpath also contains
	 * <code>com.cloudhopper.smpp.SmppSession</code> then it indicates that the
	 * implementation can't be used.
	 * 
	 * <p>
	 * If both {@link #value()} and {@link #excludes()} are provided, then the
	 * implementation can be used only if all the classes provided in
	 * {@link #value()} are available in the classpath and none of the classes
	 * provided in {@link #excludes()} is in the classpath.
	 * 
	 * @return the list of excluded classes
	 */
	String[] excludes() default {};

	/**
	 * The other classes that are alternative or aliases of the primary class
	 * provided by attribute {@link #value()}.
	 * 
	 * <p>
	 * Attribute {{@link #excludes()} is common for every alternative.
	 * 
	 * For Example:
	 * 
	 * <pre>
	 * &#064;RequiredClass(value="com.sendgrid.SendGrid1", alternatives="com.sendgrid.SendGrid2")
	 * </pre>
	 * 
	 * Means that the implementation can be used either if the class
	 * <code>com.sendgrid.SendGrid1</code> is present or if the class
	 * <code>com.sendgrid.SendGrid2</code> is present.
	 * 
	 * @return the list of alternative classes
	 */
	String[] alternatives() default {};
}
