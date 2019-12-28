package fr.sii.ogham.core.condition.fluent;

import java.util.List;
import java.util.regex.Pattern;

import fr.sii.ogham.core.condition.AndCondition;
import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.condition.FixedCondition;
import fr.sii.ogham.core.condition.NotCondition;
import fr.sii.ogham.core.condition.OrCondition;
import fr.sii.ogham.core.condition.PropertyPatternCondition;
import fr.sii.ogham.core.condition.PropertyValueCondition;
import fr.sii.ogham.core.condition.RequiredClassCondition;
import fr.sii.ogham.core.condition.RequiredPropertyCondition;
import fr.sii.ogham.core.env.PropertyResolver;

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
 * import static fr.sii.ogham.core.condition.fluent.Conditions.*;
 * </pre>
 * 
 * <p>
 * If you are using Java 7, you may need to use {@link MessageConditions}
 * instead because Java 7 doesn't resolve correctly generics chaining.
 * 
 * @author Aurélien Baudet
 *
 */
public final class Conditions {
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
	 * @param <T>
	 *            the type of the object that is under condition
	 * @return the fluent condition
	 */
	@SuppressWarnings("squid:S00100")
	public static <T> FluentCondition<T> $(Condition<T> condition) {
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
	 * @param <T>
	 *            the type of the object that is under condition
	 * @return the fluent condition
	 */
	@SafeVarargs
	public static <T> FluentCondition<T> and(Condition<T>... conditions) {
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
	 * @param <T>
	 *            the type of the object that is under condition
	 * @return the fluent condition
	 */
	public static <T> FluentCondition<T> and(List<Condition<T>> conditions) {
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
	 * @param <T>
	 *            the type of the object that is under condition
	 * @return the fluent condition
	 */
	@SafeVarargs
	public static <T> FluentCondition<T> or(Condition<T>... conditions) {
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
	 * <p>
	 * If one of the condition result is true, then other conditions are not
	 * evaluated.
	 * </p>
	 * 
	 * @param conditions
	 *            one or several conditions
	 * @param <T>
	 *            the type of the object that is under condition
	 * @return the fluent condition
	 */
	public static <T> FluentCondition<T> or(List<Condition<T>> conditions) {
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
	 * @param condition
	 *            the condition to reverse
	 * @param <T>
	 *            the type of the object that is under condition
	 * @return the fluent condition
	 */
	public static <T> FluentCondition<T> not(Condition<T> condition) {
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
	 * @param <T>
	 *            the type of the object that is under condition
	 * @return the fluent condition
	 */
	public static <T> FluentCondition<T> requiredProperty(PropertyResolver propertyResolver, String property) {
		return new FluentCondition<>(new RequiredPropertyCondition<T>(property, propertyResolver));
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
	 * @param <T>
	 *            the type of the object that is under condition
	 * @return the fluent condition
	 */
	public static <T> FluentCondition<T> requiredPropertyValue(PropertyResolver propertyResolver, String property, String value) {
		return new FluentCondition<>(new PropertyValueCondition<T>(property, value, propertyResolver));
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
	 * @param <T>
	 *            the type of the object that is under condition
	 * @return the fluent condition
	 */
	public static <T> FluentCondition<T> requiredPropertyValue(PropertyResolver propertyResolver, String property, Pattern pattern) {
		return new FluentCondition<>(new PropertyPatternCondition<T>(property, pattern, propertyResolver));
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
	 * @param <T>
	 *            the type of the object that is under condition
	 * @return the fluent condition
	 */
	public static <T> FluentCondition<T> requiredClass(String className) {
		return new FluentCondition<>(new RequiredClassCondition<T>(className));
	}

	/**
	 * A condition that always returns true.
	 * 
	 * @param <T>
	 *            the type of the object that is under condition
	 * @return the fluent condition
	 */
	public static <T> FluentCondition<T> alwaysTrue() {
		return new FluentCondition<>(new FixedCondition<T>(true));
	}

	/**
	 * A condition that always returns false.
	 * 
	 * @param <T>
	 *            the type of the object that is under condition
	 * @return the fluent condition
	 */
	public static <T> FluentCondition<T> alwaysFalse() {
		return new FluentCondition<>(new FixedCondition<T>(false));
	}

	private Conditions() {
		super();
	}
}
