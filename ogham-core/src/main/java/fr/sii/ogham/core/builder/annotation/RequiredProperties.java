package fr.sii.ogham.core.builder.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the implementation needs some property configuration keys to
 * be usable.
 * 
 * <p>
 * The annotation can handle simple cases to indicate that the presence of one
 * or several properties is mandatory for being able to use the associated
 * implementation. In this case, use the {@link #value()} attribute.
 * 
 * <p>
 * The annotation can also handle complex cases:
 * <ul>
 * <li>Indicate that one of several keys must be present</li>
 * <li>The key must be present with a particular value</li>
 * <li>The key must be present with any value but an excepted one</li>
 * </ul>
 * In this case, use {@link #props()} attribute.
 * 
 * @author Aurélien Baudet
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RequiredProperties {
	/**
	 * Provide the list of required property keys.
	 * 
	 * <p>
	 * If there is only one required key, a single string can be provided. It
	 * indicates that the key must be present in properties for being able to
	 * use the implementation. For example:
	 * 
	 * <pre>
	 * &#064;RequiredProperties(&quot;foo.bar&quot;)
	 * public class FooImplementationBuilder implements ImplementationBuilder&lt;FooImplementation&gt; {
	 * }
	 * </pre>
	 * 
	 * Means that the property key <code>foo.bar</code> must be present in
	 * properties for using the implementation <code>FooImplementation</code>.
	 * 
	 * 
	 * <p>
	 * If there are several required keys, multiple strings can be provided. It
	 * indicates that all the keys must be present in properties for being able
	 * to use the implementation. For Example:
	 * 
	 * <pre>
	 * &#064;RequiredProperties(&quot;foo.bar&quot;, &quot;foo.baz&quot;)
	 * public class FooImplementationBuilder implements ImplementationBuilder&lt;FooImplementation&gt; {
	 * }
	 * </pre>
	 * 
	 * Means that the property keys <code>foo.bar</code> and
	 * <code>foo.baz</code> must be present in properties for using the
	 * implementation <code>FooImplementation</code>.
	 * 
	 * 
	 */
	String[] value() default {};

	/**
	 * <p>
	 * Use this attribute to specify complex cases:
	 * <ul>
	 * <li>Indicate that one of several keys must be present</li>
	 * <li>The key must be present with a particular value</li>
	 * <li>The key must be present with any value but an excepted one</li>
	 * </ul>
	 * 
	 * For example:
	 * 
	 * <pre>
	 * &#064;RequiredProperties(props = { &#064;RequiredProperty(value = &quot;mail.host&quot;, alternatives = &quot;mail.smtp.host&quot;) })
	 * public class JavaMailBuilder implements ImplementationBuilder&lt;JavaMailSender&gt; {
	 * }
	 * </pre>
	 * 
	 * Means that <code>JavaMailSender</code> implementation can be used if
	 * </code>mail.host</code> or <code>mail.smtp.host</code> property is
	 * present.
	 * 
	 * <pre>
	 * &#064;RequiredProperties(props = &#064;RequiredProperty(value = &quot;mail.host&quot;, equals = &quot;smtp.gmail.com&quot;))
	 * public class JavaMailBuilder implements ImplementationBuilder&lt;JavaMailSender&gt; {
	 * }
	 * </pre>
	 * 
	 * Means that <code>JavaMailSender</code> implementation can be used if
	 * </code>mail.host</code> is present and its value is
	 * <code>smtp.gmail.com</code>.
	 */
	RequiredProperty[] props() default {};
}
