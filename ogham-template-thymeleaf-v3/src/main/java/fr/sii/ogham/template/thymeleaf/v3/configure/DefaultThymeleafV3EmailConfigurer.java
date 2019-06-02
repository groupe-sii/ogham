package fr.sii.ogham.template.thymeleaf.v3.configure;

import static fr.sii.ogham.template.thymeleaf.common.ThymeleafConstants.DEFAULT_THYMELEAF_EMAIL_CONFIGURER_PRIORITY;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.DefaultMessagingConfigurer;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurerAdapter;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.resolution.ResourceResolutionBuilder;
import fr.sii.ogham.core.util.ClasspathUtils;
import fr.sii.ogham.template.thymeleaf.common.buider.AbstractThymeleafMultiContentBuilder;
import fr.sii.ogham.template.thymeleaf.common.configure.AbstractDefaultThymeleafEmailConfigurer;
import fr.sii.ogham.template.thymeleaf.v3.ThymeleafV3TemplateDetector;
import fr.sii.ogham.template.thymeleaf.v3.buider.ThymeleafV3EmailBuilder;
import fr.sii.ogham.template.thymeleaf.v3.buider.ThymeleafV3SmsBuilder;

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
 * <li>Uses {@link ThymeleafV3TemplateDetector} to detect if templates are
 * parseable by Thymeleaf</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
@ConfigurerFor(targetedBuilder = { "minimal", "standard" }, priority = DEFAULT_THYMELEAF_EMAIL_CONFIGURER_PRIORITY)
public class DefaultThymeleafV3EmailConfigurer extends AbstractDefaultThymeleafEmailConfigurer {

	public DefaultThymeleafV3EmailConfigurer() {
		super();
	}

	public DefaultThymeleafV3EmailConfigurer(MessagingConfigurerAdapter delegate) {
		super(delegate);
	}

	@Override
	protected boolean canUseThymeleaf() {
		return ClasspathUtils.exists("org.thymeleaf.TemplateEngine") && ClasspathUtils.exists("org.thymeleaf.IEngineConfiguration");
	}

	@Override
	protected Class<? extends AbstractThymeleafMultiContentBuilder<?, ?, ?>> getBuilderClass() {
		return ThymeleafV3EmailBuilder.class;
	}

}
