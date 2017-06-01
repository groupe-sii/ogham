package fr.sii.ogham.template.thymeleaf.configure;

import static fr.sii.ogham.template.thymeleaf.ThymeleafConstants.DEFAULT_THYMELEAF_SMS_CONFIGURER_PRIORITY;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.DefaultMessagingConfigurer;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurerAdapter;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.resolution.ResourceResolutionBuilder;
import fr.sii.ogham.core.util.ClasspathUtils;
import fr.sii.ogham.template.thymeleaf.ThymeleafTemplateDetector;
import fr.sii.ogham.template.thymeleaf.buider.ThymeleafEmailBuilder;
import fr.sii.ogham.template.thymeleaf.buider.ThymeleafSmsBuilder;

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
 * {@link ThymeleafEmailBuilder#environment(EnvironmentBuilder)} and
 * {@link ThymeleafSmsBuilder#environment(EnvironmentBuilder)}).
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
 * "ogham.sms.thymeleaf.classpath.path-prefix",
 * "ogham.sms.template.classpath.path-prefix",
 * "ogham.sms.thymeleaf.path-prefix", "ogham.sms.template.path-prefix",
 * "ogham.template.path-prefix"</li>
 * <li>Uses the first property that has a value for classpath resolution suffix:
 * "ogham.sms.thymeleaf.classpath.path-suffix",
 * "ogham.sms.template.classpath.path-suffix",
 * "ogham.sms.thymeleaf.path-suffix", "ogham.sms.template.path-suffix",
 * "ogham.template.path-suffix"</li>
 * <li>Uses the first property that has a value for file resolution prefix:
 * "ogham.sms.thymeleaf.file.path-prefix",
 * "ogham.sms.template.file.path-prefix", "ogham.sms.thymeleaf.path-prefix",
 * "ogham.sms.template.path-prefix", "ogham.template.path-prefix"</li>
 * <li>Uses the first property that has a value for file resolution suffix:
 * "ogham.sms.thymeleaf.file.path-suffix",
 * "ogham.sms.template.file.path-suffix", "ogham.sms.thymeleaf.path-suffix",
 * "ogham.sms.template.path-suffix", "ogham.template.path-suffix"</li>
 * </ul>
 * </li>
 * <li>Configures template detection:
 * <ul>
 * <li>Uses {@link ThymeleafTemplateDetector} to detect if templates are
 * parseable by Thymeleaf</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
@ConfigurerFor(targetedBuilder = { "minimal", "standard" }, priority = DEFAULT_THYMELEAF_SMS_CONFIGURER_PRIORITY)
public class DefaultThymeleafSmsConfigurer implements MessagingConfigurer {
	private final MessagingConfigurerAdapter delegate;

	public DefaultThymeleafSmsConfigurer() {
		this(new DefaultMessagingConfigurer());
	}

	public DefaultThymeleafSmsConfigurer(MessagingConfigurerAdapter delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public void configure(MessagingBuilder msgBuilder) {
		if (canUseThymeleaf()) {
			ThymeleafSmsBuilder builder = msgBuilder.sms().template(ThymeleafSmsBuilder.class);
			// use same environment as parent builder
			builder.environment(msgBuilder.environment());
			// apply default resource resolution configuration
			if (delegate != null) {
				delegate.configure(builder);
			}
			// @formatter:off
			builder
				.classpath()
					.pathPrefix("${ogham.sms.thymeleaf.classpath.path-prefix}", "${ogham.sms.template.classpath.path-prefix}", "${ogham.sms.thymeleaf.path-prefix}", "${ogham.sms.template.path-prefix}", "${ogham.template.path-prefix}")
					.pathSuffix("${ogham.sms.thymeleaf.classpath.path-suffix}", "${ogham.sms.template.classpath.path-suffix}", "${ogham.sms.thymeleaf.path-suffix}", "${ogham.sms.template.path-suffix}", "${ogham.template.path-suffix}")
					.and()
				.file()
					.pathPrefix("${ogham.sms.thymeleaf.file.path-prefix}", "${ogham.sms.template.file.path-prefix}", "${ogham.sms.thymeleaf.path-prefix}", "${ogham.sms.template.path-prefix}", "${ogham.template.path-prefix}")
					.pathSuffix("${ogham.sms.thymeleaf.file.path-suffix}", "${ogham.sms.template.file.path-suffix}", "${ogham.sms.thymeleaf.path-suffix}", "${ogham.sms.template.path-suffix}", "${ogham.template.path-suffix}");
			// @formatter:on
		}
	}

	private boolean canUseThymeleaf() {
		return ClasspathUtils.exists("org.thymeleaf.TemplateEngine");
	}

}
