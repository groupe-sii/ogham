package fr.sii.ogham.core.builder.configurer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import fr.sii.ogham.core.builder.MessagingBuilder;

/**
 * Annotation to mark a {@link Configurer} instance in order to indicate which
 * builder is targeted.
 * 
 * <p>
 * The annotation also provides a priority that is used to order configurers in
 * order to configure the targeted builders in right order.
 * 
 * <p>
 * The annotation is only used by any of static builder methods:
 * <ul>
 * <li>{@link MessagingBuilder#standard()}</li>
 * <li>{@link MessagingBuilder#minimal()}</li>
 * </ul>
 * 
 * <p>
 * The static methods are in charge to handle the annotation.
 * 
 * <p>
 * Example:
 * 
 * <pre>
 * &#64;ConfigurerFor(targetedBuilder = "standard", priority = Integer.MAX_VALUE)
 * public class MyConfigurer implements MessagingConfigurer {
 * 	&#64;Override
 * 	public void configure(MessagingBuilder builder) {
 * 		// custom configuration here
 * 	}
 * }
 * </pre>
 * 
 * The configurer {@code MyConfigurer} will be automatically registered each
 * time {@link MessagingBuilder#standard()} is called. Its priority is the
 * highest possible so the configure method will be called before all other
 * configurers.
 * 
 * @author Aurélien Baudet
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigurerFor {
	/**
	 * The builder names that are targeted by the {@link Configurer}.
	 * 
	 * <p>
	 * Targetting a builder means that the configurer will be registered into
	 * the builder and then automatically applied on that builder in order to
	 * apply a particular configuration on it.
	 * </p>
	 * 
	 * Default builder names are:
	 * <ul>
	 * <li>{@code minimal}: see {@link MessagingBuilder#minimal()}</li>
	 * <li>{@code standard}: see {@link MessagingBuilder#standard()}</li>
	 * </ul>
	 * 
	 * @return the list of builders that will be configured by the configurer
	 */
	String[] targetedBuilder();

	/**
	 * The configurer priority. The priority is used to determine the orders for
	 * all the registered configurers.
	 * 
	 * <p>
	 * The highest priority for a configurer means that it is applied before
	 * other configurers with lower priorities.
	 * </p>
	 * 
	 * Default priorities are:
	 * <ul>
	 * <li><code>DefaultMessagingConfigurer</code>: 100000</li>
	 * <li><code>DefaultThymeleafEmailConfigurer</code>: 90000</li>
	 * <li><code>DefaultFreemarkerEmailConfigurer</code>: 80000</li>
	 * <li><code>DefaultThymeleafSmsConfigurer</code>: 70000</li>
	 * <li><code>DefaultFreemarkerSmsConfigurer</code>: 60000</li>
	 * <li><code>DefaultJavaMailConfigurer</code>: 50000</li>
	 * <li><code>DefaultCloudhopperConfigurer</code>: 40000</li>
	 * <li><code>DefaultSendGridConfigurer</code>: 30000</li>
	 * <li><code>DefaultOvhSmsConfigurer</code>: 20000</li>
	 * </ul>
	 * 
	 * @return the configurer priority
	 */
	int priority();

	/**
	 * The configuration phase for the configurer.
	 * 
	 * It indicates when the configurer should be executed.
	 * 
	 * @return the configurer phase
	 */
	ConfigurationPhase phase() default ConfigurationPhase.BEFORE_BUILD;
}
