package fr.sii.ogham.core.builder.annotation;

import java.util.regex.Pattern;

/**
 * This annotation is used with {@link RequiredProperties} and its aim is to
 * provide more options on how to handle required properties.
 * 
 * <p>
 * This annotation is used to indicate the following information:
 * <ul>
 * <li>One of several keys must be present</li>
 * <li>The key must be present with a particular value</li>
 * <li>The key must be present with any value but an excepted one</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public @interface RequiredProperty {
	/**
	 * The property key
	 * 
	 * @return the required property key
	 */
	String value();

	/**
	 * Indicates that the property value must be exactly the provided value.
	 * 
	 * <p>
	 * If you want to use a regular expression, use {@link #pattern()} instead.
	 * 
	 * @return the required property value
	 */
	String equals() default "";

	/**
	 * Indicates that the property value must match the provided regular
	 * expression. You can use the attribute {@link #flags()} to change regular
	 * expression behavior. See {@link Pattern} and
	 * {@link Pattern#compile(String, int)} for more information about regular
	 * expressions.
	 * 
	 * <p>
	 * If you want to use a strict value, use {@link #equals()} instead.
	 * 
	 * @return the pattern for the property value
	 */
	String pattern() default "";

	/**
	 * Used in conjunction with {@link #pattern()} to change the regular
	 * expression behavior. See {@link Pattern} and
	 * {@link Pattern#compile(String, int)} for more information about regular
	 * expression flags.
	 * 
	 * The available flags are:
	 * <ul>
	 * <li>{@link Pattern#CANON_EQ}</li>
	 * <li>{@link Pattern#CASE_INSENSITIVE}</li>
	 * <li>{@link Pattern#COMMENTS}</li>
	 * <li>{@link Pattern#DOTALL}</li>
	 * <li>{@link Pattern#LITERAL}</li>
	 * <li>{@link Pattern#MULTILINE}</li>
	 * <li>{@link Pattern#UNICODE_CASE}</li>
	 * <li>{@link Pattern#UNICODE_CHARACTER_CLASS}</li>
	 * <li>{@link Pattern#UNIX_LINES}</li>
	 * </ul>
	 * 
	 * <p>
	 * By default, no flag is applied.
	 * 
	 * @return the flag for the pattern
	 */
	int flags() default 0;

	/**
	 * Indicates which value(s) of the property makes the implementation not
	 * usable.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * &#064;RequiredProperty(value="foo.bar", except="none")
	 * </pre>
	 * 
	 * Means that the property with key <code>foo.bar</code> must be present. If
	 * the value of the property is <code>none</code> then it indicates that the
	 * implementation can't be used. If the value of the property is anything
	 * else then it indicates that the implementation can be used.
	 * 
	 * @return the excluded property values
	 */
	String[] excludes() default {};

	/**
	 * The other property keys that are alternative or aliases of the primary
	 * key provided by attribute {@link #value()}.
	 * 
	 * <p>
	 * Attributes {@link #equals()}, {@link #pattern()}, {@link #excludes()} are
	 * common for every alternative.
	 * 
	 * For Example:
	 * 
	 * <pre>
	 * &#064;RequiredProperty(value="foo.bar", alternatives="foo.baz", equals="true")
	 * </pre>
	 * 
	 * Means that the implementation can be used either if the property
	 * <code>foo.bar</code> is present and its value is <code>true</code> or if
	 * the property <code>foo.baz</code> is present and its value is
	 * <code>true</code>.
	 * 
	 * @return the list of alternative property keys
	 */
	String[] alternatives() default {};
}
