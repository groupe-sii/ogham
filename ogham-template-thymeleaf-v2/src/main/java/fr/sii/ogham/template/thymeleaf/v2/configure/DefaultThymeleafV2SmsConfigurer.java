package fr.sii.ogham.template.thymeleaf.v2.configure;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.DefaultMessagingConfigurer;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.builder.resolution.ResourceResolutionBuilder;
import fr.sii.ogham.core.exception.configurer.ConfigureException;
import fr.sii.ogham.core.exception.configurer.MissingImplementationException;
import fr.sii.ogham.core.exception.configurer.NewerImplementationAvailableException;
import fr.sii.ogham.core.util.ClasspathUtils;
import fr.sii.ogham.template.thymeleaf.common.buider.AbstractThymeleafBuilder;
import fr.sii.ogham.template.thymeleaf.common.configure.AbstractDefaultThymeleafSmsConfigurer;
import fr.sii.ogham.template.thymeleaf.v2.ThymeleafV2TemplateDetector;
import fr.sii.ogham.template.thymeleaf.v2.buider.ThymeleafV2SmsBuilder;

import static fr.sii.ogham.template.thymeleaf.common.ThymeleafConstants.DEFAULT_THYMELEAF_SMS_CONFIGURER_PRIORITY;

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
 * <li>Uses {@link ThymeleafV2TemplateDetector} to detect if templates are
 * parseable by Thymeleaf</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public final class DefaultThymeleafV2SmsConfigurer {
	@ConfigurerFor(targetedBuilder = { "minimal", "standard" }, priority = DEFAULT_THYMELEAF_SMS_CONFIGURER_PRIORITY)
	public static class ThymeleafV2SmsConfigurer extends AbstractDefaultThymeleafSmsConfigurer {
		public ThymeleafV2SmsConfigurer() {
			super();
		}

		@Override
		protected void checkCanUseThymeleaf() throws ConfigureException {
			if (!isThymeleafV2Present()) {
				throw new MissingImplementationException("Can't parse templates using Thymeleaf v2 engine because Thymeleaf v2 implementation is not present in the classpath", "org.thymeleaf.TemplateEngine");
			}
			if (isThymeleafV3Present()) {
				throw new NewerImplementationAvailableException("Can't parse templates using Thymeleaf v2 engine because Thymeleaf v3 is present in the classpath. Therefore Thymeleaf v3 is preferred");
			}
		}

		@Override
		protected Class<? extends AbstractThymeleafBuilder<?, ?, ?>> getBuilderClass() {
			return ThymeleafV2SmsBuilder.class;
		}


		private static boolean isThymeleafV2Present() {
			return ClasspathUtils.exists("org.thymeleaf.TemplateEngine") && !ClasspathUtils.exists("org.thymeleaf.IEngineConfiguration");
		}

		private boolean isThymeleafV3Present() {
			return ClasspathUtils.exists("org.thymeleaf.IEngineConfiguration");
		}
	}

	private DefaultThymeleafV2SmsConfigurer() {
		super();
	}
}
