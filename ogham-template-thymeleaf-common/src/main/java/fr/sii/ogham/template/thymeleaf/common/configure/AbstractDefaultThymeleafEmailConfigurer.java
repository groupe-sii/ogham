package fr.sii.ogham.template.thymeleaf.common.configure;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.DefaultMessagingConfigurer;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurerAdapter;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.resolution.ResourceResolutionBuilder;
import fr.sii.ogham.core.exception.configurer.ConfigureException;
import fr.sii.ogham.core.message.content.EmailVariant;
import fr.sii.ogham.template.thymeleaf.common.buider.AbstractThymeleafMultiContentBuilder;

/**
 * Default configurer for Thymeleaf template engine that is automatically
 * applied every time a {@link MessagingBuilder} instance is created through
 * {@link MessagingBuilder#standard()} or {@link MessagingBuilder#minimal()}.
 * 
 * <p>
 * The configurer has a priority of 90000 in order to be applied after global
 * configurer but before any sender implementation.
 * </p>
 * 
 * This configurer is applied only if {@code org.thymeleaf.TemplateEngine} is
 * present in the classpath. If not present, template engine is not registered
 * at all.
 * 
 * <p>
 * This configurer inherits environment configuration (see
 * {@link EnvironmentBuilder})
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
 * <li>"ogham.email.thymeleaf.classpath.path-prefix"</li>
 * <li>"ogham.email.template.classpath.path-prefix"</li>
 * <li>"ogham.email.thymeleaf.path-prefix"</li>
 * <li>"ogham.email.template.path-prefix"</li>
 * <li>"ogham.template.path-prefix"</li>
 * </ol>
 * </li>
 * <li>Uses the first property that has a value for classpath resolution suffix:
 * <ol>
 * <li>"ogham.email.thymeleaf.classpath.path-suffix"</li>
 * <li>"ogham.email.template.classpath.path-suffix"</li>
 * <li>"ogham.email.thymeleaf.path-suffix"</li>
 * <li>"ogham.email.template.path-suffix"</li>
 * <li>"ogham.template.path-suffix"</li>
 * </ol>
 * </li>
 * <li>Uses the first property that has a value for file resolution prefix:
 * <ol>
 * <li>"ogham.email.thymeleaf.file.path-prefix"</li>
 * <li>"ogham.email.template.file.path-prefix"</li>
 * <li>"ogham.email.thymeleaf.path-prefix"</li>
 * <li>"ogham.email.template.path-prefix"</li>
 * <li>"ogham.template.path-prefix"</li>
 * </ol>
 * </li>
 * <li>Uses the first property that has a value for file resolution suffix:
 * <ol>
 * <li>"ogham.email.thymeleaf.file.path-suffix"</li>
 * <li>"ogham.email.template.file.path-suffix"</li>
 * <li>"ogham.email.thymeleaf.path-suffix"</li>
 * <li>"ogham.email.template.path-suffix"</li>
 * <li>"ogham.template.path-suffix"</li>
 * </ol>
 * </li>
 * </ul>
 * </li>
 * <li>Configures email alternative content:
 * <ul>
 * <li>Automatically loads HTML template if extension is .html</li>
 * <li>Automatically loads text template if extension is .txt</li>
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
public abstract class AbstractDefaultThymeleafEmailConfigurer implements MessagingConfigurer {
	private final MessagingConfigurerAdapter delegate;

	public AbstractDefaultThymeleafEmailConfigurer() {
		this(new DefaultMessagingConfigurer());
	}

	public AbstractDefaultThymeleafEmailConfigurer(MessagingConfigurerAdapter delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public void configure(MessagingBuilder msgBuilder) throws ConfigureException {
		checkCanUseThymeleaf();

		AbstractThymeleafMultiContentBuilder<?, ?, ?> builder = msgBuilder.email().template(getBuilderClass());
		// apply default resource resolution configuration
		if (delegate != null) {
			delegate.configure(builder);
		}
		// @formatter:off
		builder
			.classpath()
				.pathPrefix()
					.properties("${ogham.email.thymeleaf.classpath.path-prefix}", 
								"${ogham.email.template.classpath.path-prefix}", 
								"${ogham.email.thymeleaf.path-prefix}", 
								"${ogham.email.template.path-prefix}", 
								"${ogham.template.path-prefix}")
					.and()
				.pathSuffix()
					.properties("${ogham.email.thymeleaf.classpath.path-suffix}", 
								"${ogham.email.template.classpath.path-suffix}", 
								"${ogham.email.thymeleaf.path-suffix}", 
								"${ogham.email.template.path-suffix}", 
								"${ogham.template.path-suffix}")
					.and()
				.and()
			.file()
				.pathPrefix()
					.properties("${ogham.email.thymeleaf.file.path-prefix}", 
								"${ogham.email.template.file.path-prefix}", 
								"${ogham.email.thymeleaf.path-prefix}", 
								"${ogham.email.template.path-prefix}", 
								"${ogham.template.path-prefix}")
					.and()
				.pathSuffix()
					.properties("${ogham.email.thymeleaf.file.path-suffix}", 
								"${ogham.email.template.file.path-suffix}", 
								"${ogham.email.thymeleaf.path-suffix}", 
								"${ogham.email.template.path-suffix}", 
								"${ogham.template.path-suffix}")
					.and()
				.and()
			.string()
				.and()
			.variant(EmailVariant.HTML, "html")
			.variant(EmailVariant.HTML, "xhtml")
			.variant(EmailVariant.TEXT, "txt")
			.cache()
				.properties("${ogham.email.thymeleaf.cache}",
							"${ogham.email.template.cache}",
							"${ogham.template.cache}");
		// @formatter:on
	}

	protected abstract Class<? extends AbstractThymeleafMultiContentBuilder<?, ?, ?>> getBuilderClass();

	protected abstract void checkCanUseThymeleaf() throws ConfigureException;

}
