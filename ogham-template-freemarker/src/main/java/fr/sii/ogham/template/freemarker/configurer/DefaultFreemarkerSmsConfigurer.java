package fr.sii.ogham.template.freemarker.configurer;

import static fr.sii.ogham.core.builder.configuration.MayOverride.overrideIfNotSet;
import static fr.sii.ogham.template.freemarker.FreemarkerConstants.DEFAULT_FREEMARKER_SMS_CONFIGURER_PRIORITY;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.DefaultMessagingConfigurer;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurerAdapter;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.builder.resolution.ResourceResolutionBuilder;
import fr.sii.ogham.core.util.ClasspathUtils;
import fr.sii.ogham.template.freemarker.FreeMarkerTemplateDetector;
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
 * {@link BuildContext}).
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
 * <li>"ogham.sms.freemarker.classpath.path-prefix"</li>
 * <li>"ogham.sms.template.classpath.path-prefix"</li>
 * <li>"ogham.sms.freemarker.path-prefix"</li>
 * <li>"ogham.sms.template.path-prefix"</li>
 * <li>"ogham.template.path-prefix"</li>
 * </ol>
 * </li>
 * <li>Uses the first property that has a value for classpath resolution suffix:
 * <ol>
 * <li>"ogham.sms.freemarker.classpath.path-suffix"</li>
 * <li>"ogham.sms.template.classpath.path-suffix"</li>
 * <li>"ogham.sms.freemarker.path-suffix"</li>
 * <li>"ogham.sms.template.path-suffix"</li>
 * <li>"ogham.template.path-suffix"</li>
 * </ol>
 * </li>
 * <li>Uses the first property that has a value for file resolution prefix:
 * <ol>
 * <li>"ogham.sms.freemarker.file.path-prefix"</li>
 * <li>"ogham.sms.template.file.path-prefix"</li>
 * <li>"ogham.sms.freemarker.path-prefix"</li>
 * <li>"ogham.sms.template.path-prefix"</li>
 * <li>"ogham.template.path-prefix"</li>
 * </ol>
 * </li>
 * <li>Uses the first property that has a value for file resolution suffix:
 * <ol>
 * <li>"ogham.sms.freemarker.file.path-suffix"</li>
 * <li>"ogham.sms.template.file.path-suffix"</li>
 * <li>"ogham.sms.freemarker.path-suffix"</li>
 * <li>"ogham.sms.template.path-suffix"</li>
 * <li>"ogham.template.path-suffix"</li>
 * </ol>
 * </li>
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
 * <li>Configures static method access from templates:
 * <ul>
 * <li>Uses property value of ${ogham.freemarker.static-method-access.enable} if
 * provided to enable/disable static method access from templates (default is
 * enabled is nothing is configured)</li>
 * <li>Uses property value of
 * ${ogham.freemarker.static-method-access.variable-name} if provided to set the
 * name used to access static methods from templates (default is 'statics')</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public final class DefaultFreemarkerSmsConfigurer {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultFreemarkerSmsConfigurer.class);

	@ConfigurerFor(targetedBuilder = { "minimal", "standard" }, priority = DEFAULT_FREEMARKER_SMS_CONFIGURER_PRIORITY)
	public static class FreemakerConfigurer implements MessagingConfigurer {
		private final MessagingConfigurerAdapter delegate;

		public FreemakerConfigurer() {
			this(new DefaultMessagingConfigurer());
		}

		public FreemakerConfigurer(MessagingConfigurerAdapter delegate) {
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
			FreemarkerSmsBuilder builder = msgBuilder.sms().template(FreemarkerSmsBuilder.class);
			// apply default resource resolution configuration
			if (delegate != null) {
				delegate.configure(builder);
			}
			// @formatter:off
			builder
				.classpath()
					.pathPrefix()
						.properties("${ogham.sms.freemarker.classpath.path-prefix}",
									"${ogham.sms.template.classpath.path-prefix}", 
									"${ogham.sms.freemarker.path-prefix}", 
									"${ogham.sms.template.path-prefix}", 
									"${ogham.template.path-prefix}")
						.and()
					.pathSuffix()
						.properties("${ogham.sms.freemarker.classpath.path-suffix}", 
									"${ogham.sms.template.classpath.path-suffix}", 
									"${ogham.sms.freemarker.path-suffix}", 
									"${ogham.sms.template.path-suffix}", 
									"${ogham.template.path-suffix}")
						.and()
					.and()
				.file()
					.pathPrefix()
						.properties("${ogham.sms.freemarker.file.path-prefix}", 
									"${ogham.sms.template.file.path-prefix}", 
									"${ogham.sms.freemarker.path-prefix}", 
									"${ogham.sms.template.path-prefix}", 
									"${ogham.template.path-prefix}")
						.and()
					.pathSuffix()
						.properties("${ogham.sms.freemarker.file.path-suffix}", 
									"${ogham.sms.template.file.path-suffix}", 
									"${ogham.sms.freemarker.path-suffix}", 
									"${ogham.sms.template.path-suffix}", 
									"${ogham.template.path-suffix}")
						.and()
					.and()
				.configuration()
					.defaultEncoding().properties("${ogham.freemarker.default-encoding}").defaultValue(overrideIfNotSet("UTF-8")).and()
					.templateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER)
					.enableStaticMethodAccess().properties("${ogham.freemarker.static-method-access.enable}").defaultValue(overrideIfNotSet(true)).and()
					.staticMethodAccessVariableName().properties("${ogham.freemarker.static-method-access.variable-name}").defaultValue(overrideIfNotSet("statics"));
			// @formatter:on
		}

		private static boolean canUseFreemaker() {
			return ClasspathUtils.exists("freemarker.template.Configuration") && ClasspathUtils.exists("freemarker.template.Template");
		}
	}

	private DefaultFreemarkerSmsConfigurer() {
		super();
	}
}
