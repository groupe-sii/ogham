package fr.sii.ogham.core.builder.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the implementation needs some classes available in the
 * classpath to be usable.
 * 
 * <p>
 * The annotation can handle simple cases to indicate that the presence of one
 * or several classes is mandatory for being able to use the associated
 * implementation. In this case, use the {@link #value()} attribute.
 * 
 * <p>
 * The sub-annotation {@link RequiredClass} is used to indicate the following
 * information:
 * <ul>
 * <li>One of several class must be present</li>
 * <li>One or several classes must not be present</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RequiredClasses {
	/**
	 * Provide the list of required classes needed for being able to use the
	 * implementation.
	 * 
	 * <p>
	 * If there is only one required class, a single string can be provided. It
	 * indicates that the class must be present in the classpath for being able
	 * to use the implementation. For example:
	 * 
	 * <pre>
	 * &#064;RequiredClasses(&quot;javax.mail.Transport&quot;)
	 * public class JavaMailBuilder implements ImplementationBuilder&lt;JavaMailSender&gt; {
	 * }
	 * </pre>
	 * 
	 * Means that the class <code>javax.mail.Transport</code> must be present in
	 * the classpath for using the implementation <code>JavaMailSender</code>.
	 * 
	 * 
	 * <p>
	 * If there are several required classes, multiple strings can be provided.
	 * It indicates that all the classes must be present in the classpath for
	 * being able to use the implementation. For Example:
	 * 
	 * <pre>
	 * &#064;RequiredClasses({ &quot;javax.mail.Transport&quot;, &quot;com.sun.mail.smtp.SMTPTransport&quot; })
	 * public class JavaMailBuilder implements ImplementationBuilder&lt;JavaMailSender&gt; {
	 * }
	 * </pre>
	 * 
	 * Means that the classes <code>javax.mail.Transport</code> and
	 * <code>com.sun.mail.smtp.SMTPTransport</code> must be present in the
	 * classpath for using the implementation <code>JavaMailSender</code>.
	 * 
	 * @return list of required classes
	 */
	String[] value() default {};

	/**
	 * <p>
	 * Use this attribute to specify complex cases:
	 * <ul>
	 * <li>One of several class must be present</li>
	 * <li>One or several classes must not be present</li>
	 * </ul>
	 * 
	 * For example:
	 * 
	 * <pre>
	 * &#064;RequiredClasses(classes = { &#064;RequiredClass(value = &quot;javax.mail.Transport&quot;, except = &quot;org.apache.commons.mail.HtmlEmail&quot;) })
	 * public class JavaMailBuilder implements ImplementationBuilder&lt;JavaMailSender&gt; {
	 * }
	 * </pre>
	 * 
	 * Means that <code>JavaMailSender</code> implementation can be used if
	 * <code>javax.mail.Transport</code> class is present but can't be used if
	 * <code>org.apache.commons.mail.HtmlEmail</code> class is present.
	 * 
	 * <pre>
	 * &#064;RequiredProperties(classes = &#064;RequiredProperty(value = &quot;javax.mail.Transport&quot;, equals = &quot;java.mail.Transport&quot;))
	 * public class JavaMailBuilder implements ImplementationBuilder&lt;JavaMailSender&gt; {
	 * }
	 * </pre>
	 * 
	 * Means that <code>JavaMailSender</code> implementation can be used if
	 * <code>javax.mail.Transport</code> class is present or
	 * <code>javax.mail.Transportm</code> class is present.
	 * 
	 * @return list of advanced class inclusions
	 */
	RequiredClass[] classes() default {};
}
