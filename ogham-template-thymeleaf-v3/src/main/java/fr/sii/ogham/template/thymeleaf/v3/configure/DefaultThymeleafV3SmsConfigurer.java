package fr.sii.ogham.template.thymeleaf.v3.configure;

import static fr.sii.ogham.core.builder.configurer.ConfigurationPhase.AFTER_INIT;
import static fr.sii.ogham.template.thymeleaf.common.ThymeleafConstants.DEFAULT_THYMELEAF_SMS_CONFIGURER_PRIORITY;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.DefaultMessagingConfigurer;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurerAdapter;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.resolution.ResourceResolutionBuilder;
import fr.sii.ogham.core.util.ClasspathUtils;
import fr.sii.ogham.template.thymeleaf.common.buider.AbstractThymeleafBuilder;
import fr.sii.ogham.template.thymeleaf.common.configure.AbstractDefaultThymeleafSmsConfigurer;
import fr.sii.ogham.template.thymeleaf.v3.ThymeleafV3TemplateDetector;
import fr.sii.ogham.template.thymeleaf.v3.buider.ThymeleafV3EmailBuilder;
import fr.sii.ogham.template.thymeleaf.v3.buider.ThymeleafV3SmsBuilder;

/**
 * Default configurer for Thymeleaf template engine that is automatically
 * applied every time a {@link MessagingBuilder} instance is created through
 * {@link MessagingBuilder#standard()} or {@link MessagingBuilder#minimal()}.
 * 
 * <p>
 * The configurer has a priority of 70000 in order to be applied after global
 * configurer but before any sender implementation.
 * </p>
 * 
 * This configurer is applied only if {@code org.thymeleaf.TemplateEngine} is
 * present in the classpath. If not present, template engine is not registered
 * at all.
 * 
 * <p>
 * This configurer inherits environment configuration (see
 * {@link EnvironmentBuilder},
 * {@link ThymeleafV3EmailBuilder#environment(EnvironmentBuilder)} and
 * {@link ThymeleafV3SmsBuilder#environment(EnvironmentBuilder)}).
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
 * <li>"ogham.sms.thymeleaf.classpath.path-prefix"</li>
 * <li>"ogham.sms.template.classpath.path-prefix"</li>
 * <li>"ogham.sms.thymeleaf.path-prefix"</li>
 * <li>"ogham.sms.template.path-prefix"</li>
 * <li>"ogham.template.path-prefix"</li>
 * </ol>
 * </li>
 * <li>Uses the first property that has a value for classpath resolution suffix:
 * <ol>
 * <li>"ogham.sms.thymeleaf.classpath.path-suffix"</li>
 * <li>"ogham.sms.template.classpath.path-suffix"</li>
 * <li>"ogham.sms.thymeleaf.path-suffix"</li>
 * <li>"ogham.sms.template.path-suffix"</li>
 * <li>"ogham.template.path-suffix"</li>
 * </ol>
 * </li>
 * <li>Uses the first property that has a value for file resolution prefix:
 * <ol>
 * <li>"ogham.sms.thymeleaf.file.path-prefix"</li>
 * <li>"ogham.sms.template.file.path-prefix"</li>
 * <li>"ogham.sms.thymeleaf.path-prefix"</li>
 * <li>"ogham.sms.template.path-prefix"</li>
 * <li>"ogham.template.path-prefix"</li>
 * </ol>
 * </li>
 * <li>Uses the first property that has a value for file resolution suffix:
 * <ol>
 * <li>"ogham.sms.thymeleaf.file.path-suffix"</li>
 * <li>"ogham.sms.template.file.path-suffix"</li>
 * <li>"ogham.sms.thymeleaf.path-suffix"</li>
 * <li>"ogham.sms.template.path-suffix"</li>
 * <li>"ogham.template.path-suffix"</li>
 * </ol>
 * </li>
 * </ul>
 * </li>
 * <li>Configures template detection:
 * <ul>
 * <li>Uses {@link ThymeleafV3TemplateDetector} to detect if templates are
 * parseable by Thymeleaf</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public final class DefaultThymeleafV3SmsConfigurer {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultThymeleafV3SmsConfigurer.class);

	@ConfigurerFor(targetedBuilder = { "minimal", "standard" }, priority = DEFAULT_THYMELEAF_SMS_CONFIGURER_PRIORITY, phase = AFTER_INIT)
	public static class EnvironmentPropagator implements MessagingConfigurer {
		@Override
		public void configure(MessagingBuilder msgBuilder) {
			if (canUseThymeleafV3()) {
				AbstractThymeleafBuilder<?, ?, ?> builder = msgBuilder.sms().template(ThymeleafV3SmsBuilder.class);
				// use same environment as parent builder
				builder.environment(msgBuilder.environment());
			}
		}
	}
	
	@ConfigurerFor(targetedBuilder = { "minimal", "standard" }, priority = DEFAULT_THYMELEAF_SMS_CONFIGURER_PRIORITY)
	public static class ThymeleafV3SmsConfigurer extends AbstractDefaultThymeleafSmsConfigurer { 
		public ThymeleafV3SmsConfigurer() {
			super(LOG);
		}
	
		public ThymeleafV3SmsConfigurer(MessagingConfigurerAdapter delegate) {
			super(LOG, delegate);
		}
	
		@Override
		protected boolean canUseThymeleaf() {
			return canUseThymeleafV3();
		}

		@Override
		protected Class<? extends AbstractThymeleafBuilder<?, ?, ?>> getBuilderClass() {
			return ThymeleafV3SmsBuilder.class;
		}
	}

	private static boolean canUseThymeleafV3() {
		return ClasspathUtils.exists("org.thymeleaf.TemplateEngine") && ClasspathUtils.exists("org.thymeleaf.IEngineConfiguration");
	}

	private DefaultThymeleafV3SmsConfigurer() {
		super();
	}
}
