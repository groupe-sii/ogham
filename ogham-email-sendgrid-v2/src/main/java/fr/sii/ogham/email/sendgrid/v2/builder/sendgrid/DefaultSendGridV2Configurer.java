package fr.sii.ogham.email.sendgrid.v2.builder.sendgrid;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.builder.mimetype.MimetypeDetectionBuilder;
import fr.sii.ogham.core.exception.configurer.ConfigureException;
import fr.sii.ogham.core.exception.configurer.MissingImplementationException;
import fr.sii.ogham.core.util.ClasspathUtils;

import static fr.sii.ogham.email.sendgrid.SendGridConstants.DEFAULT_SENDGRID_CONFIGURER_PRIORITY;

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
 * {@link BuildContext}).
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
 * key</a>: using the property "ogham.email.sendgrid.api-key"</li>
 * <li>Or using username/password: using the properties
 * "ogham.email.sendgrid.username" and "ogham.email.sendgrid.password"</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public final class DefaultSendGridV2Configurer {
	@ConfigurerFor(targetedBuilder = "standard", priority = DEFAULT_SENDGRID_CONFIGURER_PRIORITY)
	public static class SendGridV2Configurer implements MessagingConfigurer {
		@Override
		public void configure(MessagingBuilder msgBuilder) throws ConfigureException {
			checkCanUseSendGrid();

			// @formatter:off
			SendGridV2Builder builder = msgBuilder.email().sender(SendGridV2Builder.class);
			// inherit mimetype configuration as parent builder
			builder.mimetype(msgBuilder.mimetype());
			builder
				.apiKey().properties("${ogham.email.sendgrid.api-key}").and()
				.username().properties("${ogham.email.sendgrid.username}").and()
				.password().properties("${ogham.email.sendgrid.password}").and()
				.url().properties("${ogham.email.sendgrid.url}");
			// @formatter:on
		}

		private static void checkCanUseSendGrid() throws ConfigureException {
			if (!isSendGridV2Present()) {
				throw new MissingImplementationException("Can't send Email using SendGrid v2 sender because SendGrid v2 implementation is not present in the classpath", "com.sendgrid.SendGrid", "com.sendgrid.SendGrid$Email");
			}
		}

		private static boolean isSendGridV2Present() {
			return ClasspathUtils.exists("com.sendgrid.SendGrid") && ClasspathUtils.exists("com.sendgrid.SendGrid$Email");
		}
	}

	private DefaultSendGridV2Configurer() {
		super();
	}
}
