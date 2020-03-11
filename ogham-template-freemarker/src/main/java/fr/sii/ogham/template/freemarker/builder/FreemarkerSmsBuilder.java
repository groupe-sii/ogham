package fr.sii.ogham.template.freemarker.builder;

import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.sms.builder.SmsBuilder;
import freemarker.template.Configuration;

/**
 * Configures parsing of templates using Freemarker.
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
 * You can customize default Freemarker {@link Configuration}:
 * 
 * <pre>
 * <code>
 * .configuration()
 *   .version(Configuration.VERSION_2_3_25)
 *   .templateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER)
 * </code>
 * </pre>
 * 
 * Or you can use a particular Freemarker {@link Configuration}:
 * 
 * <pre>
 * <code>
 * .configuration(myConfiguration)
 * </code>
 * </pre>
 * 
 * @author Aurélien Baudet
 */
public class FreemarkerSmsBuilder extends AbstractFreemarkerBuilder<FreemarkerSmsBuilder, SmsBuilder> {
	/**
	 * Default constructor when using Freemarker without all Ogham work.
	 * 
	 * <strong>WARNING: use is only if you know what you are doing !</strong>
	 */
	public FreemarkerSmsBuilder() {
		super(FreemarkerSmsBuilder.class);
	}

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method. The {@link EnvironmentBuilder} is
	 * used to evaluate properties when {@link #build()} method is called.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param buildContext
	 *            for registering instances and property evaluation
	 */
	public FreemarkerSmsBuilder(SmsBuilder parent, BuildContext buildContext) {
		super(FreemarkerSmsBuilder.class, parent, buildContext);
	}
}
