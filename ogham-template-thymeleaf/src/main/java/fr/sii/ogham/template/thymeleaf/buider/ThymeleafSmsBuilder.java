package fr.sii.ogham.template.thymeleaf.buider;

import org.thymeleaf.TemplateEngine;

import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.sms.builder.SmsBuilder;

/**
 * Configures parsing of templates using Thymeleaf.
 * 
 * Specific resource resolution can be configured to use template prefix/suffix
 * paths:
 * 
 * <pre>
 * <code>
 * .classpath()
 *   .pathPrefix("email/")
 *   .pathSuffix(".html")
 *   .and()
 * .file()
 *   .pathPrefix("/data/myapplication/templates/email")
 *   .pathSuffix(".html")
 * </code>
 * </pre>
 * 
 * You can customize default Thymeleaf {@link TemplateEngine}:
 * 
 * <pre>
 * <code>
 * .engine()
 *   .addDialect("foo", myDialect)
 *   .addMessageResolver(myMessageResolver)
 * </code>
 * </pre>
 * 
 * Or you can use a particular Thymeleaf {@link TemplateEngine}:
 * 
 * <pre>
 * <code>
 * .engine(new MyTemplateEngine())
 * </code>
 * </pre>
 * 
 * @author Aurélien Baudet
 *
 */
public class ThymeleafSmsBuilder extends AbstractThymeleafBuilder<ThymeleafSmsBuilder, SmsBuilder> {
	/**
	 * Default constructor when using Thymeleaf without all Ogham work.
	 * 
	 * <strong>WARNING: use is only if you know what you are doing !</strong>
	 */
	public ThymeleafSmsBuilder() {
		super(ThymeleafSmsBuilder.class);
	}

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method. The {@link EnvironmentBuilder} is
	 * used to evaluate properties when {@link #build()} method is called.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param environmentBuilder
	 *            the configuration for property resolution and evaluation
	 */
	public ThymeleafSmsBuilder(SmsBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(ThymeleafSmsBuilder.class, parent, environmentBuilder);
	}
}
