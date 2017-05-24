package fr.sii.ogham.core.condition.provider;

import fr.sii.ogham.core.builder.ActivableAtRuntime;
import fr.sii.ogham.core.builder.annotation.RequiredClass;
import fr.sii.ogham.core.builder.annotation.RequiredClasses;
import fr.sii.ogham.core.builder.annotation.RequiredProperties;
import fr.sii.ogham.core.builder.annotation.RequiredProperty;
import fr.sii.ogham.core.condition.AndCondition;
import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.message.Message;

/**
 * The aim is to look at the object and get defined conditions. Conditions may
 * be defined by several ways:
 * <ul>
 * <li>By using annotations (see {@link RequiredClass},
 * {@link RequiredProperty}, {@link RequiredClasses} and
 * {@link RequiredProperties})</li>
 * <li>By implementing {@link ActivableAtRuntime} interface</li>
 * </ul>
 * 
 * <p>
 * If both are defined, the final condition is a and between conditions bring by
 * annotations and condition returned by
 * {@link ActivableAtRuntime#getCondition()} method.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public class ImplementationConditionProvider implements ConditionProvider<Object, Message> {
	private final AnnotationConditionProvider<Message> annotationProvider;
	private final ActivableAtRuntimeConditionProvider runtimeProvider;

	/**
	 * Initializes with a {@link PropertyResolver} instance. The
	 * {@link PropertyResolver} is used by the
	 * {@link AnnotationConditionProvider} (specifically by
	 * {@link RequiredPropertiesAnnotationProvider} and
	 * {@link RequiredPropertyAnnotationProvider}) to check existence of
	 * properties.
	 * 
	 * @param propertyResolver
	 *            the property resolver used to check existence of properties
	 */
	public ImplementationConditionProvider(PropertyResolver propertyResolver) {
		super();
		annotationProvider = new AnnotationConditionProvider<>(propertyResolver);
		runtimeProvider = new ActivableAtRuntimeConditionProvider();
	}

	@Override
	public Condition<Message> provide(Object source) {
		AndCondition<Message> mainCondition = new AndCondition<>();
		mainCondition.and(annotationProvider.provide(source.getClass()));
		mainCondition.and(runtimeProvider.provide(source));
		return mainCondition;
	}

}
