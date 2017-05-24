package fr.sii.ogham.core.condition.fluent;

import java.util.List;

import fr.sii.ogham.core.condition.AndCondition;
import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.condition.FixedCondition;
import fr.sii.ogham.core.condition.NotCondition;
import fr.sii.ogham.core.condition.OrCondition;
import fr.sii.ogham.core.condition.RequiredClassCondition;
import fr.sii.ogham.core.condition.RequiredPropertyCondition;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.message.Message;

/**
 * Helper to write fluent conditions like:
 * 
 * <pre>
 * requiredClass("javax.mail.Transport").and(not(requiredClass("foo.Bar")));
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
 * you are using Java 7 (because generic resolution doesn't work well with Java
 * 7).
 * 
 * @author Aurélien Baudet
 *
 */
public class MessageConditions {
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
	public static FluentCondition<Message> $(Condition<Message> condition) {
		return new FluentCondition<>(condition);
	}

	/**
	 * And operator between the provided conditions:
	 * 
	 * <pre>
	 * and(requiredClass("javax.mail.Transport"), requiredClass("foo.Bar"));
	 * </pre>
	 * 
	 * Means that the result will be true only if
	 * <code>javax.mail.Transport</code> and <code>foo.Bar</code> classes are
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
		return new FluentCondition<>(new AndCondition<>(conditions));
	}

	/**
	 * And operator between the provided conditions. It is a helper method that
	 * is useful when you need to construct conditions separately:
	 * 
	 * <pre>
	 * List&lt;Condition&lt;Message&gt;&gt; conditions = new ArrayList&lt;&gt;();
	 * conditions.add(requiredClass("javax.mail.Transport"));
	 * conditions.add(requiredClass("foo.Bar"));
	 * 
	 * and(conditions);
	 * </pre>
	 * 
	 * Means that the result will be true only if
	 * <code>javax.mail.Transport</code> and <code>foo.Bar</code> classes are
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
		return new FluentCondition<>(new AndCondition<>(conditions));
	}

	/**
	 * Or operator between the provided conditions:
	 * 
	 * <pre>
	 * or(requiredClass("javax.mail.Transport"), requiredClass("foo.Bar"));
	 * </pre>
	 * 
	 * Means that the result will be true if either
	 * <code>javax.mail.Transport</code> or <code>foo.Bar</code> class is
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
		return new FluentCondition<>(new OrCondition<>(conditions));
	}

	/**
	 * Or operator between the provided conditions. It is a helper method that
	 * is useful when you need to construct conditions separately:
	 * 
	 * <pre>
	 * List&lt;Condition&lt;Message&gt;&gt; conditions = new ArrayList&lt;&gt;();
	 * conditions.add(requiredClass("javax.mail.Transport"));
	 * conditions.add(requiredClass("foo.Bar"));
	 * 
	 * or(conditions);
	 * </pre>
	 * 
	 * Means that the result will be true if either
	 * <code>javax.mail.Transport</code> or <code>foo.Bar</code> class is
	 * present in the classpath.
	 * 
	 * @param conditions
	 *            one or several conditions
	 * @return the fluent condition
	 */
	public static FluentCondition<Message> or(List<Condition<Message>> conditions) {
		return new FluentCondition<>(new OrCondition<>(conditions));
	}

	/**
	 * Not operator to reverse provided condition:
	 * 
	 * <pre>
	 * not(requiredClass("javax.mail.Transport));
	 * </pre>
	 * 
	 * Means that the result will be true if the class
	 * <code>javax.mail.Transport</code> is not present in the classpath.
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
		return new FluentCondition<>(new NotCondition<>(condition));
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
		return new FluentCondition<>(new RequiredPropertyCondition<Message>(property, propertyResolver));
	}

	/**
	 * Check if a class is present in the classpath.
	 * 
	 * <pre>
	 * requiredClass("javax.mail.Transport");
	 * </pre>
	 * 
	 * Means that the result will be true only if the class
	 * <code>javax.mail.Transport</code> is present in the classpath.
	 * 
	 * @param className
	 *            the class to check
	 * @return the fluent condition
	 */
	public static FluentCondition<Message> requiredClass(String className) {
		return new FluentCondition<>(new RequiredClassCondition<Message>(className));
	}

	/**
	 * A condition that always returns true.
	 * 
	 * @return the fluent condition
	 */
	public static FluentCondition<Message> alwaysTrue() {
		return new FluentCondition<>(new FixedCondition<Message>(true));
	}

	/**
	 * A condition that always returns false.
	 * 
	 * @return the fluent condition
	 */
	public static FluentCondition<Message> alwaysFalse() {
		return new FluentCondition<>(new FixedCondition<Message>(false));
	}

	private MessageConditions() {
		super();
	}
}
