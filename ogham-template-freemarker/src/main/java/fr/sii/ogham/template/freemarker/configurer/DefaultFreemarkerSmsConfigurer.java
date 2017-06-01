package fr.sii.ogham.template.freemarker.configurer;

import static fr.sii.ogham.template.freemarker.FreemarkerConstants.DEFAULT_FREEMARKER_SMS_CONFIGURER_PRIORITY;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.DefaultMessagingConfigurer;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurerAdapter;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.resolution.ResourceResolutionBuilder;
import fr.sii.ogham.core.util.ClasspathUtils;
import fr.sii.ogham.template.freemarker.FreeMarkerTemplateDetector;
import fr.sii.ogham.template.freemarker.builder.FreemarkerEmailBuilder;
import fr.sii.ogham.template.freemarker.builder.FreemarkerSmsBuilder;
import freemarker.template.TemplateExceptionHandler;

/**
 * Default configurer for Freemarker template engine that is automatically
 * applied every time a {@link MessagingBuilder} instance is created through
 * {@link MessagingBuilder#standard()} or {@link MessagingBuilder#minimal()}.
 * 
 * <p>
 * The configurer has a priority of 60000 in order to be applied after global
 * configurer but before any sender implementation.
 * </p>
 * 
 * This configurer is applied only if {@code freemarker.template.Configuration}
 * and {@code freemarker.template.Template} are present in the classpath. If not
 * present, template engine is not registered at all.
 * 
 * <p>
 * This configurer inherits environment configuration (see
 * {@link EnvironmentBuilder},
 * {@link FreemarkerEmailBuilder#environment(EnvironmentBuilder)} and
 * {@link FreemarkerSmsBuilder#environment(EnvironmentBuilder)}).
 * </p>
 * <p>
 * It also copies resource resolution configuration of
 * {@link DefaultMessagingConfigurer} to inherit resource resolution lookups
 * (see {@link ResourceResolutionBuilder}).
 * </p>
 * 
 * <p>
 * This configurer applies the following configuration:
 * <ul>
 * <li>Configures template prefix/suffix paths:
 * <ul>
 * <li>Uses the first property that has a value for classpath resolution prefix:
 * "ogham.sms.freemarker.classpath.path-prefix",
 * "ogham.sms.template.classpath.path-prefix",
 * "ogham.sms.freemarker.path-prefix", "ogham.sms.template.path-prefix",
 * "ogham.template.path-prefix"</li>
 * <li>Uses the first property that has a value for classpath resolution suffix:
 * "ogham.sms.freemarker.classpath.path-suffix",
 * "ogham.sms.template.classpath.path-suffix",
 * "ogham.sms.freemarker.path-suffix", "ogham.sms.template.path-suffix",
 * "ogham.template.path-suffix"</li>
 * <li>Uses the first property that has a value for file resolution prefix:
 * "ogham.sms.freemarker.file.path-prefix",
 * "ogham.sms.template.file.path-prefix", "ogham.sms.freemarker.path-prefix",
 * "ogham.sms.template.path-prefix", "ogham.template.path-prefix"</li>
 * <li>Uses the first property that has a value for file resolution suffix:
 * "ogham.sms.freemarker.file.path-suffix",
 * "ogham.sms.template.file.path-suffix", "ogham.sms.freemarker.path-suffix",
 * "ogham.sms.template.path-suffix", "ogham.template.path-suffix"</li>
 * </ul>
 * </li>
 * <li>Configures encoding:
 * <ul>
 * <li>It uses "ogham.freemarker.default-encoding" property value as charset for
 * template parsing if defined. Default charset is UTF-8</li>
 * </ul>
 * </li>
 * <li>Configures template detection:
 * <ul>
 * <li>Uses {@link FreeMarkerTemplateDetector} to detect if templates are
 * parseable by Freemarker</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
@ConfigurerFor(targetedBuilder = { "minimal", "standard" }, priority = DEFAULT_FREEMARKER_SMS_CONFIGURER_PRIORITY)
public class DefaultFreemarkerSmsConfigurer implements MessagingConfigurer {
	private final MessagingConfigurerAdapter delegate;

	public DefaultFreemarkerSmsConfigurer() {
		this(new DefaultMessagingConfigurer());
	}

	public DefaultFreemarkerSmsConfigurer(MessagingConfigurerAdapter delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public void configure(MessagingBuilder msgBuilder) {
		if (canUseFreemaker()) {
			FreemarkerSmsBuilder builder = msgBuilder.sms().template(FreemarkerSmsBuilder.class);
			// use same environment as parent builder
			builder.environment(msgBuilder.environment());
			// apply default resource resolution configuration
			if (delegate != null) {
				delegate.configure(builder);
			}
			// @formatter:off
			builder
				.classpath()
					.pathPrefix("${ogham.sms.freemarker.classpath.path-prefix}", "${ogham.sms.template.classpath.path-prefix}", "${ogham.sms.freemarker.prefix}", "${ogham.sms.template.path-prefix}", "${ogham.template.path-prefix}")
					.pathSuffix("${ogham.sms.freemarker.classpath.path-suffix}", "${ogham.sms.template.classpath.path-suffix}", "${ogham.sms.freemarker.suffix}", "${ogham.sms.template.path-suffix}", "${ogham.template.path-suffix}")
					.and()
				.file()
					.pathPrefix("${ogham.sms.freemarker.file.path-prefix}", "${ogham.sms.template.file.path-prefix}", "${ogham.sms.freemarker.prefix}", "${ogham.sms.template.path-prefix}", "${ogham.template.path-prefix}")
					.pathSuffix("${ogham.sms.freemarker.file.path-suffix}", "${ogham.sms.template.file.path-suffix}", "${ogham.sms.freemarker.suffix}", "${ogham.sms.template.path-suffix}", "${ogham.template.path-suffix}")
					.and()
				.configuration()
					.defaultEncoding("${ogham.freemarker.default-encoding}", "UTF-8")
					.templateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER)
					.and()
				.detector(new FreeMarkerTemplateDetector(".ftl"));
			// @formatter:on
		}
	}

	private boolean canUseFreemaker() {
		return ClasspathUtils.exists("freemarker.template.Configuration") && ClasspathUtils.exists("freemarker.template.Template");
	}

}
