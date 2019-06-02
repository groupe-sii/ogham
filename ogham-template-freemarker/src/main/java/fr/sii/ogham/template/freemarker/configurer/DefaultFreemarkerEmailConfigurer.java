package fr.sii.ogham.template.freemarker.configurer;

import static fr.sii.ogham.template.freemarker.FreemarkerConstants.DEFAULT_FREEMARKER_EMAIL_CONFIGURER_PRIORITY;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.DefaultMessagingConfigurer;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurerAdapter;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.resolution.ResourceResolutionBuilder;
import fr.sii.ogham.core.message.content.EmailVariant;
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
 * The configurer has a priority of 80000 in order to be applied after global
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
 * <ol>
 * <li>"ogham.email.freemarker.classpath.path-prefix"</li>
 * <li>"ogham.email.template.classpath.path-prefix"</li>
 * <li>"ogham.email.freemarker.path-prefix"</li>
 * <li>"ogham.email.template.path-prefix"</li>
 * <li>"ogham.template.path-prefix"</li>
 * </ol>
 * </li>
 * <li>Uses the first property that has a value for classpath resolution suffix:
 * <ol>
 * <li>"ogham.email.freemarker.classpath.path-suffix"</li>
 * <li>"ogham.email.template.classpath.path-suffix"</li>
 * <li>"ogham.email.freemarker.path-suffix"</li>
 * <li>"ogham.email.template.path-suffix"</li>
 * <li>"ogham.template.path-suffix"</li>
 * </ol>
 * </li>
 * <li>Uses the first property that has a value for file resolution prefix:
 * <ol>
 * <li>"ogham.email.freemarker.file.path-prefix"</li>
 * <li>"ogham.email.template.file.path-prefix"</li>
 * <li>"ogham.email.freemarker.path-prefix"</li>
 * <li>"ogham.email.template.path-prefix"</li>
 * <li>"ogham.template.path-prefix"</li>
 * </ol>
 * </li>
 * <li>Uses the first property that has a value for file resolution suffix:
 * <ol>
 * <li>"ogham.email.freemarker.file.path-suffix"</li>
 * <li>"ogham.email.template.file.path-suffix"</li>
 * <li>"ogham.email.freemarker.path-suffix"</li>
 * <li>"ogham.email.template.path-suffix"</li>
 * <li>"ogham.template.path-suffix"</li>
 * </ol>
 * </li>
 * </ul>
 * </li>
 * <li>Configures email alternative content:
 * <ul>
 * <li>Automatically loads HTML template if extension is .html.ftl</li>
 * <li>Automatically loads text template if extension is .txt.ftl</li>
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
@ConfigurerFor(targetedBuilder = { "minimal", "standard" }, priority = DEFAULT_FREEMARKER_EMAIL_CONFIGURER_PRIORITY)
public class DefaultFreemarkerEmailConfigurer implements MessagingConfigurer {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultFreemarkerEmailConfigurer.class);
	
	private final MessagingConfigurerAdapter delegate;

	public DefaultFreemarkerEmailConfigurer() {
		this(new DefaultMessagingConfigurer());
	}

	public DefaultFreemarkerEmailConfigurer(MessagingConfigurerAdapter delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public void configure(MessagingBuilder msgBuilder) {
		if (!canUseFreemaker()) {
			LOG.debug("[{}] skip configuration", this);
			return;
		}
		LOG.debug("[{}] apply configuration", this);
		FreemarkerEmailBuilder builder = msgBuilder.email().template(FreemarkerEmailBuilder.class);
		// use same environment as parent builder
		builder.environment(msgBuilder.environment());
		// apply default resource resolution configuration
		if (delegate != null) {
			delegate.configure(builder);
		}
		// @formatter:off
		builder
			.classpath()
				.pathPrefix("${ogham.email.freemarker.classpath.path-prefix}", 
							"${ogham.email.template.classpath.path-prefix}", 
							"${ogham.email.freemarker.path-prefix}", 
							"${ogham.email.template.path-prefix}", 
							"${ogham.template.path-prefix}")
				.pathSuffix("${ogham.email.freemarker.classpath.path-suffix}", 
							"${ogham.email.template.classpath.path-suffix}", 
							"${ogham.email.freemarker.path-suffix}", 
							"${ogham.email.template.path-suffix}", 
							"${ogham.template.path-suffix}")
				.and()
			.file()
				.pathPrefix("${ogham.email.freemarker.file.path-prefix}", 
							"${ogham.email.template.file.path-prefix}", 
							"${ogham.email.freemarker.path-prefix}", 
							"${ogham.email.template.path-prefix}", 
							"${ogham.template.path-prefix}")
				.pathSuffix("${ogham.email.freemarker.file.path-suffix}", 
							"${ogham.email.template.file.path-suffix}", 
							"${ogham.email.freemarker.path-suffix}", 
							"${ogham.email.template.path-suffix}", 
							"${ogham.template.path-suffix}")
				.and()
			.string()
				.and()
			.variant(EmailVariant.HTML, "html.ftl")
			.variant(EmailVariant.TEXT, "txt.ftl")
			.configuration()
				.defaultEncoding("${ogham.freemarker.default-encoding}", "UTF-8")
				.templateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		// @formatter:on
	}

	private boolean canUseFreemaker() {
		return ClasspathUtils.exists("freemarker.template.Configuration") && ClasspathUtils.exists("freemarker.template.Template");
	}

}
