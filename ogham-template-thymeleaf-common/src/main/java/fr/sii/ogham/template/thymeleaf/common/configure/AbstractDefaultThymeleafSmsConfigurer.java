package fr.sii.ogham.template.thymeleaf.common.configure;

import org.slf4j.Logger;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.DefaultMessagingConfigurer;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurerAdapter;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.resolution.ResourceResolutionBuilder;
import fr.sii.ogham.template.thymeleaf.common.buider.AbstractThymeleafBuilder;

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
 * {@link EnvironmentBuilder}).
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
 * <li>Uses ThymeleafTemplateDetector to detect if templates are
 * parseable by Thymeleaf</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public abstract class AbstractDefaultThymeleafSmsConfigurer implements MessagingConfigurer {
	private final Logger log;
	private final MessagingConfigurerAdapter delegate;

	public AbstractDefaultThymeleafSmsConfigurer(Logger log) {
		this(log, new DefaultMessagingConfigurer());
	}

	public AbstractDefaultThymeleafSmsConfigurer(Logger log, MessagingConfigurerAdapter delegate) {
		super();
		this.log = log;
		this.delegate = delegate;
	}

	@Override
	public void configure(MessagingBuilder msgBuilder) {
		if (!canUseThymeleaf()) {
			log.debug("[{}] skip configuration", this);
			return;
		}
		log.debug("[{}] apply configuration", this);
		AbstractThymeleafBuilder<?, ?, ?> builder = msgBuilder.sms().template(getBuilderClass());
		// apply default resource resolution configuration
		if (delegate != null) {
			delegate.configure(builder);
		}
		// @formatter:off
		builder
			.classpath()
				.pathPrefix()
					.properties("${ogham.sms.thymeleaf.classpath.path-prefix}", 
								"${ogham.sms.template.classpath.path-prefix}", 
								"${ogham.sms.thymeleaf.path-prefix}",
								"${ogham.sms.template.path-prefix}", 
								"${ogham.template.path-prefix}")
					.and()
				.pathSuffix()
					.properties("${ogham.sms.thymeleaf.classpath.path-suffix}", 
								"${ogham.sms.template.classpath.path-suffix}", 
								"${ogham.sms.thymeleaf.path-suffix}",
								"${ogham.sms.template.path-suffix}", 
								"${ogham.template.path-suffix}")
					.and()
				.and()
			.file()
				.pathPrefix()
					.properties("${ogham.sms.thymeleaf.file.path-prefix}", 
								"${ogham.sms.template.file.path-prefix}", 
								"${ogham.sms.thymeleaf.path-prefix}",
								"${ogham.sms.template.path-prefix}", 
								"${ogham.template.path-prefix}")
					.and()
				.pathSuffix()
					.properties("${ogham.sms.thymeleaf.file.path-suffix}", 
								"${ogham.sms.template.file.path-suffix}", 
								"${ogham.sms.thymeleaf.path-suffix}",
								"${ogham.sms.template.path-suffix}", 
								"${ogham.template.path-suffix}")
					.and()
				.and()
			.cache()
				.properties("${ogham.sms.thymeleaf.cache}",
							"${ogham.sms.template.cache}",
							"${ogham.template.cache}");
		// @formatter:on
	}

	protected abstract Class<? extends AbstractThymeleafBuilder<?, ?, ?>> getBuilderClass();

	protected abstract boolean canUseThymeleaf();

}
