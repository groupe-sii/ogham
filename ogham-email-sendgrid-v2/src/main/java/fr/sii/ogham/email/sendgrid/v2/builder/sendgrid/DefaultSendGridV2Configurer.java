package fr.sii.ogham.email.sendgrid.v2.builder.sendgrid;

import static fr.sii.ogham.core.builder.configurer.ConfigurationPhase.AFTER_INIT;
import static fr.sii.ogham.email.sendgrid.SendGridConstants.DEFAULT_SENDGRID_CONFIGURER_PRIORITY;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.mimetype.MimetypeDetectionBuilder;
import fr.sii.ogham.core.util.ClasspathUtils;

/**
 * Default SendGrid configurer that is automatically applied every time a
 * {@link MessagingBuilder} instance is created through
 * {@link MessagingBuilder#standard()}.
 * 
 * <p>
 * The configurer has a priority of 30000 in order to be applied after
 * templating configurers and JavaMail configurer.
 * </p>
 * 
 * This configurer is applied only if {@code com.sendgrid.SendGrid} is present
 * in the classpath. If not present, SendGrid implementation is not registered
 * at all.
 * 
 * <p>
 * This configurer inherits environment configuration (see
 * {@link EnvironmentBuilder} and
 * {@link SendGridV2Builder#environment(EnvironmentBuilder)}).
 * </p>
 * <p>
 * This configurer inherits mimetype configuration (see
 * {@link MimetypeDetectionBuilder} and
 * {@link SendGridV2Builder#mimetype(MimetypeDetectionBuilder)}).
 * </p>
 * 
 * <p>
 * This configurer applies the following configuration:
 * <ul>
 * <li>Configures authentication:
 * <ul>
 * <li>Either by providing an <a href=
 * "https://sendgrid.com/docs/Classroom/Send/How_Emails_Are_Sent/api_keys.html">API
 * key</a>: using the property "ogham.email.sengrid.api-key"</li>
 * <li>Or using username/password: using the properties
 * "ogham.email.sengrid.username" and "ogham.email.sengrid.password"</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public final class DefaultSendGridV2Configurer {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSendGridV2Configurer.class);
	
	@ConfigurerFor(targetedBuilder = "standard", priority = DEFAULT_SENDGRID_CONFIGURER_PRIORITY, phase = AFTER_INIT)
	public static class EnvironmentPropagator implements MessagingConfigurer {
		@Override
		public void configure(MessagingBuilder msgBuilder) {
			if (canUseSendGrid()) {
				SendGridV2Builder builder = msgBuilder.email().sender(SendGridV2Builder.class);
				// use same environment as parent builder
				builder.environment(msgBuilder.environment());
				builder.mimetype(msgBuilder.mimetype());
			}
		}
	}

	@ConfigurerFor(targetedBuilder = "standard", priority = DEFAULT_SENDGRID_CONFIGURER_PRIORITY)
	public static class SendGridV2Configurer implements MessagingConfigurer {
		@Override
		public void configure(MessagingBuilder msgBuilder) {
			if (!canUseSendGrid()) {
				LOG.debug("[{}] skip configuration", this);
				return;
			}
			LOG.debug("[{}] apply configuration", this);
			// @formatter:off
			SendGridV2Builder builder = msgBuilder.email().sender(SendGridV2Builder.class);
			builder
				.apiKey().properties("${ogham.email.sengrid.api-key}").and()
				.username().properties("${ogham.email.sendgrid.username}").and()
				.password().properties("${ogham.email.sendgrid.password}");
			// @formatter:on
		}
	}
	
	private static boolean canUseSendGrid() {
		return ClasspathUtils.exists("com.sendgrid.SendGrid") && ClasspathUtils.exists("com.sendgrid.SendGrid$Email");
	}
	
	private DefaultSendGridV2Configurer() {
		super();
	}
}
