package fr.sii.ogham.email.builder.sendgrid;

import static fr.sii.ogham.email.SendGridConstants.DEFAULT_SENDGRID_CONFIGURER_PRIORITY;

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
 * {@link SendGridBuilder#environment(EnvironmentBuilder)}).
 * </p>
 * <p>
 * This configurer inherits mimetype configuration (see
 * {@link MimetypeDetectionBuilder} and
 * {@link SendGridBuilder#mimetype(MimetypeDetectionBuilder)}).
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
@ConfigurerFor(targetedBuilder = "standard", priority = DEFAULT_SENDGRID_CONFIGURER_PRIORITY)
public class DefaultSendGridConfigurer implements MessagingConfigurer {

	@Override
	public void configure(MessagingBuilder msgBuilder) {
		if (canUseSendGrid()) {
			// @formatter:off
			SendGridBuilder builder = msgBuilder.email().sender(SendGridBuilder.class);
			builder
				.apiKey("${ogham.email.sengrid.api-key}")
				.username("${ogham.email.sendgrid.username}")
				.password("${ogham.email.sendgrid.password}");
			// @formatter:on
			// use same environment as parent builder
			builder.environment(msgBuilder.environment());
			builder.mimetype(msgBuilder.mimetype());
		}
	}

	private boolean canUseSendGrid() {
		return ClasspathUtils.exists("com.sendgrid.SendGrid");
	}
}
