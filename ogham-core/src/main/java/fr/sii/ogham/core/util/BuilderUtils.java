package fr.sii.ogham.core.util;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;
import java.util.StringJoiner;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.convert.Converter;
import fr.sii.ogham.core.convert.DefaultConverter;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.fluent.AbstractParent;
import fr.sii.ogham.core.fluent.Parent;
import fr.sii.ogham.email.builder.EmailBuilder;

/**
 * Helper class for {@link Builder} implementations. It separates the builder
 * implementations from the environment.
 * 
 * @author Aurélien Baudet
 * @see Builder
 */
public final class BuilderUtils {
	private static Converter converter;

	/**
	 * Provide the default properties. For now, it provides only
	 * {@link System#getProperties()}. But according to the environment or the
	 * future of the module, properties may come from other source.
	 * 
	 * @return the default properties
	 */
	public static Properties getDefaultProperties() {
		return System.getProperties();
	}

	/**
	 * If the property value is an expression ({@code "${property.key}"}), then
	 * it is evaluated to get the value of "property.key". If the value is not
	 * an expression, the value is returned and converted to the result class.
	 * 
	 * @param <T>
	 *            the type of the resulting property
	 * @param property
	 *            the property that may be an expression
	 * @param propertyResolver
	 *            the property resolver used to find property value (if it is an
	 *            expression)
	 * @param resultClass
	 *            the result class
	 * @return the resulting value of the expression, the value or null
	 */
	public static <T> T evaluate(String property, PropertyResolver propertyResolver, Class<T> resultClass) {
		if (isExpression(property)) {
			return propertyResolver.getProperty(getPropertyKey(property), resultClass);
		}
		return getConverter().convert(property, resultClass);
	}

	/**
	 * Evaluate a list of properties that may contain expressions. It internally
	 * calls {@link #evaluate(String, PropertyResolver, Class)}. It tries on
	 * first property in the list. If {@code null} value is returned then the
	 * next property is tried and so on until one property returns a non-null
	 * value.
	 * 
	 * <p>
	 * If all properties return null, it returns null.
	 * 
	 * @param <T>
	 *            the type of resulting value
	 * @param properties
	 *            the list of properties to try in sequence
	 * @param propertyResolver
	 *            the property resolver used to find property value (if it is an
	 *            expression)
	 * @param resultClass
	 *            the result class
	 * @return the resulting value or null
	 */
	public static <T> T evaluate(List<String> properties, PropertyResolver propertyResolver, Class<T> resultClass) {
		if (properties == null) {
			return null;
		}
		for (String prop : properties) {
			T value = evaluate(prop, propertyResolver, resultClass);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	/**
	 * Get the property of inside the expression
	 * 
	 * @param expression
	 *            the property expression
	 * @return the property key
	 */
	public static String getPropertyKey(String expression) {
		return expression.substring(2, expression.length() - 1);
	}

	/**
	 * Indicates if the property is the form of an expression
	 * ("${property.key}") or not.
	 * 
	 * @param property
	 *            the property that may be an expression
	 * @return true if it is an expression, false otherwise
	 */
	public static boolean isExpression(String property) {
		return property != null && property.startsWith("${") && property.endsWith("}");
	}

	/**
	 * Change the converter used by ByulderUtils
	 * 
	 * @param converter
	 *            the new converter
	 */
	public static void setConverter(Converter converter) {
		BuilderUtils.converter = converter;
	}

	// @formatter:off
	/**
	 * Utility method used to dynamically instantiate a builder instance.
	 * 
	 * <p>
	 * If you want fluent chaining, your builder class <strong>MUST</strong>
	 * declare parent of type {@code P} as first parameter. The builder can
	 * implement {@link Parent} or even extend {@link AbstractParent}. For
	 * example, if builder is a child of {@link EmailBuilder}:
	 * 
	 * <pre>
	 * {@code
	 * class MyBuilder extends AbstractParent<EmailBuilder> implements Builder<Foo> {
	 *   public MyBuilder(EmailBuilder parent) {
	 *     super(parent);
	 *   }
	 * }
	 * }</pre>
	 * 
	 * 
	 * <p>
	 * You may need {@link BuildContext} in order to be able to evaluate
	 * properties in your {@link Builder#build()} method. Just declare a
	 * parameter of type {@link BuildContext} either as first parameter if
	 * you don't want fluent chaining:
	 * 
	 * <pre>
	 * {@code
	 * class MyBuilder implements Builder<Foo> {
	 *   public MyBuilder(BuildContext buildContext) {
	 *     this.buildContext = buildContext;
	 *   }
	 * }
	 * }</pre>
	 * 
	 * or as second parameter if you want fluent chaining:
	 * 
	 * <pre>
	 * {@code
	 * class MyBuilder extends AbstractParent<EmailBuilder> implements Builder<Foo> {
	 *   public MyBuilder(EmailBuilder parent, BuildContext buildContext) {
	 *     super(parent);
	 *     this.buildContext = buildContext;
	 *   }
	 * }
	 * }</pre>
	 * 
	 * 
	 * <p>
	 * If you need none of these features, you still have to provide a public
	 * default constructor.
	 * 
	 * <p>
	 * If several constructors exist, the following order is used (first
	 * matching constructor is used):
	 * <ul>
	 * <li>{@code contructor(P parent, BuildContext buildContext)}</li>
	 * <li>{@code contructor(P parent}</li>
	 * <li>{@code contructor(BuildContext buildContext)}</li>
	 * <li>{@code contructor(}</li>
	 * </ul>
	 * 
	 * @param <T>
	 *            The type of the built object
	 * @param <B>
	 *            The type of the builder that builds T
	 * @param <P>
	 *            The type of the parent builder (used for fluent chaining)
	 * @param builderClass
	 *            The builder class to instantiate
	 * @param parent
	 *            The parent builder for fluent chaining
	 * @param buildContext
	 *            The current build context
	 * @return the builder instance
	 * @throws BuildException
	 *             when builder can't be instantiated
	 */
	// @formatter:on
	@SuppressWarnings("squid:RedundantThrowsDeclarationCheck")
	public static <T, B extends Builder<? extends T>, P> B instantiateBuilder(Class<B> builderClass, P parent, BuildContext buildContext) throws BuildException {
		try {
			return instantiate(builderClass, parent, buildContext);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | SecurityException | IllegalArgumentException e) {
			throw new BuildException("Can't instantiate builder from class " + builderClass.getSimpleName(), e);
		}
	}

	/**
	 * Build the instance using the provided builder.
	 * 
	 * <p>
	 * If builder is {@code null}, it returns {@code null}.
	 * 
	 * <p>
	 * If builder is not {@code null}, the value of {@link Builder#build()} is
	 * used. The returned value may be {@code null}.
	 * 
	 * @param <T>
	 *            the type of the built instance
	 * @param builder
	 *            the builder
	 * @return the built instance or null if builder is null or if it returns
	 *         null
	 */
	public static <T> T build(Builder<T> builder) {
		if (builder == null) {
			return null;
		}
		return builder.build();
	}

	private static <T, B extends Builder<? extends T>, P> B instantiate(Class<B> builderClass, P parent, BuildContext buildContext)
			throws InstantiationException, IllegalAccessException, InvocationTargetException {
		try {
			return builderClass.getConstructor(parent.getClass(), BuildContext.class).newInstance(parent, buildContext);
		} catch (NoSuchMethodException e) {
			// skip
		}
		try {
			return builderClass.getConstructor(parent.getClass()).newInstance(parent);
		} catch (NoSuchMethodException e) {
			// skip
		}
		try {
			return builderClass.getConstructor(BuildContext.class).newInstance(buildContext);
		} catch (NoSuchMethodException e) {
			// skip
		}
		try {
			return builderClass.getConstructor().newInstance();
		} catch (NoSuchMethodException e) {
			// skip
		}
		StringJoiner joiner = new StringJoiner("\n- ", "\n- ", "\n");
		joiner.add("constructor(" + parent.getClass().getName() + ", " + BuildContext.class.getName() + ")\n   if you want fluent chaining and inherit current build context");
		joiner.add("constructor(" + parent.getClass().getName() + ")\n   if you want fluent chaining");
		joiner.add("constructor(" + BuildContext.class.getName() + ")\n   if you don't want fluent chaining but inherit current build context");
		joiner.add("constructor()\n   if you don't want fluent chaining and inherit current build context");
		throw new BuildException("No matching constructor found. The builder implementation must provide one of following constructors:" + joiner.toString());
	}

	private static Converter getConverter() {
		if (converter == null) {
			converter = new DefaultConverter();
		}
		return converter;
	}

	private BuilderUtils() {
		super();
	}

}
