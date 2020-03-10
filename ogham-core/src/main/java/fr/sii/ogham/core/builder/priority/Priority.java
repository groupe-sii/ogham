package fr.sii.ogham.core.builder.priority;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.sender.MessageSender;
import fr.sii.ogham.core.template.parser.TemplateParser;

/**
 * Annotation that is used to configure the priority of an implementation.
 * 
 * <p>
 * A developer that provides an implementation for a {@link MessageSender} or a
 * {@link TemplateParser} should indicate the priority of its implementation.
 * This way the result of {@link MessagingBuilder} is deterministic (always the
 * same result).
 * 
 * <p>
 * However, in a pluggable system, the value set by the developer may not fit
 * the behavior needed by the user of the library. That's why the developer can
 * provide a default value and a list of property keys.
 * 
 * <p>
 * If a property is set, the user of the library can change the priority so if
 * there are several implementations that are suitable to send the message, the
 * selected one can be changed.
 * 
 * <p>
 * The other advantage is that the registration priority of the
 * {@link Configurer}s are independent of the priority of the
 * {@link MessageSender} and {@link TemplateParser} implementations.
 * 
 * @author Aurélien Baudet
 *
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Priority {
	/**
	 * The list of declared property keys of the form "${property.key}".
	 * 
	 * <p>
	 * If several property keys are registered, the first as a higher priority.
	 * 
	 * <p>
	 * If no property is registered, then the priority is not configurable.
	 * 
	 * @return the property keys
	 */
	String[] properties() default {};

	/**
	 * If no property is declared or no value is set for declared properties,
	 * the default value is used as the priority.
	 * 
	 * <p>
	 * This value is generally set directly by the developer of the
	 * implementation.
	 * 
	 * @return the default priority
	 */
	int defaultValue() default 0;
}
