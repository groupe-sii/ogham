package fr.sii.ogham.core.condition.fluent;

import java.util.List;
import java.util.regex.Pattern;

import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.message.Message;

/**
 * Helper to write fluent conditions like:
 * 
 * <pre>
 * requiredClass("jakarta.mail.Transport").and(not(requiredClass("foo.Bar")));
 * </pre>
 * 
 * To do this, you need to add the following static import:
 * 
 * <pre>
 * import static fr.sii.ogham.core.builder.condition.MessageConditions.*;
 * </pre>
 * 
 * <p>
 * This implementation is specialized for {@link Message}s that may be needed if
 * you are using Java 7 (because generic chaining resolution doesn't work well
 * with Java 7).
 * 
 * @author Aurélien Baudet
 *
 */
public final class MessageConditions {
	/**
	 * Parenthesis operator to handle priorities:
	 * 
	 * <pre>
	 * $(requiredProperty(propertyResolver, "mail.host").or(requiredProperty(propertyResolver, "mail.smtp.host")))
	 * 		.and(requiredProperty(propertyResolver, "mail.port").or(requiredProperty(propertyResolver, "mail.smtp.port")))
	 * </pre>
	 * 
	 * Meaning "(mail.host is defined OR mail.smtp.host is defined) AND
	 * (mail.port is defined OR mail.smtp.port is defined)".
	 * 
	 * <p>
	 * Without this operator, you had to write this:
	 * 
	 * <pre>
	 * requiredProperty(propertyResolver, "mail.host").or(requiredProperty(propertyResolver, "mail.smtp.host"))
	 * .and(requiredProperty(propertyResolver, "mail.port").or(requiredProperty(propertyResolver, "mail.smtp.port"))
	 * </pre>
	 * 
	 * Meaning "mail.host is defined OR mail.smtp.host is defined AND (mail.port
	 * is defined OR mail.smtp.port is defined)".
	 * 
	 * So the behavior won't be the same.
	 * 
	 * @param condition
	 *            the condition to surround
	 * @return the fluent condition
	 */
	@SuppressWarnings("squid:S00100")
	public static FluentCondition<Message> $(Condition<Message> condition) {
		return Conditions.$(condition);
	}

	/**
	 * And operator between the provided conditions:
	 * 
	 * <pre>
	 * and(requiredClass("jakarta.mail.Transport"), requiredClass("foo.Bar"));
	 * </pre>
	 * 
	 * Means that the result will be true only if
	 * <code>jakarta.mail.Transport</code> and <code>foo.Bar</code> classes are
	 * present in the classpath.
	 * 
	 * <p>
	 * If one of the condition result is false, then other conditions are not
	 * evaluated.
	 * </p>
	 * 
	 * @param conditions
	 *            one or several conditions
	 * @return the fluent condition
	 */
	@SafeVarargs
	public static FluentCondition<Message> and(Condition<Message>... conditions) {
		return Conditions.and(conditions);
	}

	/**
	 * And operator between the provided conditions. It is a helper method that
	 * is useful when you need to construct conditions separately:
	 * 
	 * <pre>
	 * List&lt;Condition&lt;Message&gt;&gt; conditions = new ArrayList&lt;&gt;();
	 * conditions.add(requiredClass("jakarta.mail.Transport"));
	 * conditions.add(requiredClass("foo.Bar"));
	 * 
	 * and(conditions);
	 * </pre>
	 * 
	 * Means that the result will be true only if
	 * <code>jakarta.mail.Transport</code> and <code>foo.Bar</code> classes are
	 * present in the classpath.
	 * 
	 * <p>
	 * If one of the condition result is false, then other conditions are not
	 * evaluated.
	 * </p>
	 * 
	 * @param conditions
	 *            one or several conditions
	 * @return the fluent condition
	 */
	public static FluentCondition<Message> and(List<Condition<Message>> conditions) {
		return Conditions.and(conditions);
	}

	/**
	 * Or operator between the provided conditions:
	 * 
	 * <pre>
	 * or(requiredClass("jakarta.mail.Transport"), requiredClass("foo.Bar"));
	 * </pre>
	 * 
	 * Means that the result will be true if either
	 * <code>jakarta.mail.Transport</code> or <code>foo.Bar</code> class is
	 * present in the classpath.
	 * 
	 * <p>
	 * If one of the condition result is true, then other conditions are not
	 * evaluated.
	 * </p>
	 * 
	 * @param conditions
	 *            one or several conditions
	 * @return the fluent condition
	 */
	@SafeVarargs
	public static FluentCondition<Message> or(Condition<Message>... conditions) {
		return Conditions.or(conditions);
	}

	/**
	 * Or operator between the provided conditions. It is a helper method that
	 * is useful when you need to construct conditions separately:
	 * 
	 * <pre>
	 * List&lt;Condition&lt;Message&gt;&gt; conditions = new ArrayList&lt;&gt;();
	 * conditions.add(requiredClass("jakarta.mail.Transport"));
	 * conditions.add(requiredClass("foo.Bar"));
	 * 
	 * or(conditions);
	 * </pre>
	 * 
	 * Means that the result will be true if either
	 * <code>jakarta.mail.Transport</code> or <code>foo.Bar</code> class is
	 * present in the classpath.
	 * 
	 * @param conditions
	 *            one or several conditions
	 * @return the fluent condition
	 */
	public static FluentCondition<Message> or(List<Condition<Message>> conditions) {
		return Conditions.or(conditions);
	}

	/**
	 * Not operator to reverse provided condition:
	 * 
	 * <pre>
	 * not(requiredClass("jakarta.mail.Transport));
	 * </pre>
	 * 
	 * Means that the result will be true if the class
	 * <code>jakarta.mail.Transport</code> is not present in the classpath.
	 * 
	 * <p>
	 * If one of the condition result is true, then other conditions are not
	 * evaluated.
	 * </p>
	 * 
	 * @param condition
	 *            the condition to reverse
	 * @return the fluent condition
	 */
	public static FluentCondition<Message> not(Condition<Message> condition) {
		return Conditions.not(condition);
	}

	/**
	 * Check if a property is present in the configuration properties. The
	 * configuration properties are available through the property resolver.
	 * 
	 * <pre>
	 * requiredProperty(propertyResolver, "mail.host");
	 * </pre>
	 * 
	 * Means that the result will be true only if the property
	 * <code>mail.host</code> is present in the property resolver.
	 * 
	 * @param propertyResolver
	 *            the resolver that is used to access property values
	 * @param property
	 *            the property name
	 * @return the fluent condition
	 */
	public static FluentCondition<Message> requiredProperty(PropertyResolver propertyResolver, String property) {
		return Conditions.requiredProperty(propertyResolver, property);
	}

	/**
	 * Check if a property has a particular value in the configuration
	 * properties. The configuration properties are available through the
	 * property resolver.
	 * 
	 * <pre>
	 * requiredPropertyValue(propertyResolver, "mail.host", "localhost");
	 * </pre>
	 * 
	 * Means that the result will be true only if the property
	 * <code>mail.host</code> is present in the property resolver and its value
	 * is exactly <code>localhost</code>.
	 * 
	 * @param propertyResolver
	 *            the resolver that is used to access property values
	 * @param property
	 *            the property name
	 * @param value
	 *            the exact value to match
	 * @return the fluent condition
	 */
	public static FluentCondition<Message> requiredPropertyValue(PropertyResolver propertyResolver, String property, String value) {
		return Conditions.requiredPropertyValue(propertyResolver, property, value);
	}

	/**
	 * Check if a property value matches the pattern in the configuration
	 * properties. The configuration properties are available through the
	 * property resolver.
	 * 
	 * <pre>
	 * requiredPropertyValue(propertyResolver, "mail.host", Pattern.compile("local.*"));
	 * </pre>
	 * 
	 * Means that the result will be true only if the property
	 * <code>mail.host</code> is present in the property resolver and its value
	 * matches the pattern <code>local.*</code>.
	 * 
	 * @param propertyResolver
	 *            the resolver that is used to access property values
	 * @param property
	 *            the property name
	 * @param pattern
	 *            the pattern used to check if the value matches
	 * @return the fluent condition
	 */
	public static FluentCondition<Message> requiredPropertyValue(PropertyResolver propertyResolver, String property, Pattern pattern) {
		return Conditions.requiredPropertyValue(propertyResolver, property, pattern);
	}

	/**
	 * Check if a class is present in the classpath.
	 * 
	 * <pre>
	 * requiredClass("jakarta.mail.Transport");
	 * </pre>
	 * 
	 * Means that the result will be true only if the class
	 * <code>jakarta.mail.Transport</code> is present in the classpath.
	 * 
	 * @param className
	 *            the class to check
	 * @return the fluent condition
	 */
	public static FluentCondition<Message> requiredClass(String className) {
		return Conditions.requiredClass(className);
	}

	/**
	 * A condition that always returns true.
	 * 
	 * @return the fluent condition
	 */
	public static FluentCondition<Message> alwaysTrue() {
		return Conditions.alwaysTrue();
	}

	/**
	 * A condition that always returns false.
	 * 
	 * @return the fluent condition
	 */
	public static FluentCondition<Message> alwaysFalse() {
		return Conditions.alwaysFalse();
	}

	private MessageConditions() {
		super();
	}
}
