package fr.sii.ogham.core.builder;

import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.condition.fluent.MessageConditions;
import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.sender.MultiImplementationSender;

/**
 * When using {@link MultiImplementationSender}, it will evaluate conditions to
 * check if the associated implementation is able to send the message.
 * 
 * This interface provides the condition to evaluate.
 * 
 * @author Aurélien Baudet
 *
 */
public interface ActivableAtRuntime {
	/**
	 * Provide the condition to evaluate in order to indicate if the associated
	 * implementation can handle the message.
	 * 
	 * <p>
	 * See {@link MessageConditions} to know how to construct your condition.
	 * </p>
	 * 
	 * For example:
	 * 
	 * <pre>
	 * requiredClass("javax.mail.Transport").and(requiredProperty(propertyResolver, "mail.host"));
	 * </pre>
	 * 
	 * Means that the associated implementation will be used only if
	 * {@code javax.mail.Transport} class is present in the classpath and the
	 * property {@code mail.host} exists in defined properties.
	 * 
	 * @return the condition to evaluate
	 */
	Condition<Message> getCondition();
}
