package fr.sii.ogham.email.builder.javamail;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.exception.configurer.ClasspathConsistencyException;
import fr.sii.ogham.core.exception.configurer.ConfigureException;
import fr.sii.ogham.core.exception.configurer.MissingImplementationException;
import fr.sii.ogham.core.util.ClasspathUtils;
import fr.sii.ogham.email.builder.javamail.JavaMailConsistencyChecker.JavaMailConsistencyException;
import fr.sii.ogham.email.sender.impl.javamail.UsernamePasswordAuthenticator;
import jakarta.mail.internet.MimeMessage;

import static fr.sii.ogham.core.builder.configuration.MayOverride.overrideIfNotSet;
import static fr.sii.ogham.email.JavaMailConstants.DEFAULT_JAVAMAIL_CONFIGURER_PRIORITY;
import static fr.sii.ogham.email.builder.javamail.JavaMailConsistencyChecker.checkDataHandlersAvailable;
import static fr.sii.ogham.email.builder.javamail.JavaMailConsistencyChecker.checkMailProvidersAvailable;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Default JavaMail configurer that is automatically applied every time a
 * {@link MessagingBuilder} instance is created through
 * {@link MessagingBuilder#standard()}.
 * 
 * <p>
 * The configurer has a priority of 50000 in order to be applied after
 * templating configurers.
 * </p>
 * 
 * This configurer is applied only if {@code jakarta.mail.Transport} and
 * {@code jakarta.mail.internet.MimeMessage} are present in the classpath. If not
 * present, JavaMail implementation is not registered at all.
 * 
 * <p>
 * This configurer inherits environment configuration (see
 * {@link BuildContext}).
 * </p>
 * 
 * <p>
 * This configurer applies the following configuration:
 * <ul>
 * <li>Configures host and port:
 * <ul>
 * <li>It uses one of "ogham.email.javamail.host", "mail.smtp.host" or
 * "mail.hofromst" property if defined for mail server host address (IP or
 * hostname)</li>
 * <li>It uses one of "ogham.email.javamail.port", "mail.smtp.port" or
 * "mail.port" property if defined for mail server port. Default port is 25</li>
 * </ul>
 * </li>
 * <li>Configures authentication:
 * <ul>
 * <li>If property "ogham.email.javamail.authenticator.username" and
 * "ogham.email.javamail.authenticator.password" are defined, then an
 * {@link UsernamePasswordAuthenticator} is used to handle username/password
 * authentication</li>
 * </ul>
 * </li>
 * <li>Configures encoding:
 * <ul>
 * <li>It uses "ogham.email.javamail.body.charset" property value as charset for
 * email body if defined. Default charset is UTF-8</li>
 * </ul>
 * </li>
 * <li>Configures mimetype detection:
 * <ul>
 * <li>Uses Apache Tika to detect mimetype</li>
 * <li>Explicitly use "text/html" mimetype instead of more specific ones like
 * "application/xhtml" (XHTML)</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public final class DefaultJavaMailConfigurer {
	private static final int DEFAULT_SMTP_PORT = 25;

	@ConfigurerFor(targetedBuilder = "standard", priority = DEFAULT_JAVAMAIL_CONFIGURER_PRIORITY)
	public static class JavaMailConfigurer implements MessagingConfigurer {
		@Override
		public void configure(MessagingBuilder msgBuilder) throws ConfigureException {
			checkCanUseJavaMail();

			JavaMailBuilder builder = msgBuilder.email().sender(JavaMailBuilder.class);
			// @formatter:off
			builder
				.host().properties("${ogham.email.javamail.host}", "${mail.smtp.host}", "${mail.host}").and()
				.port().properties("${ogham.email.javamail.port}", "${mail.smtp.port}", "${mail.port}").defaultValue(overrideIfNotSet(DEFAULT_SMTP_PORT)).and()
				.authenticator()
					.username().properties("${ogham.email.javamail.authenticator.username}").and()
					.password().properties("${ogham.email.javamail.authenticator.password}").and()
					.and()
				.charset().properties("${ogham.email.javamail.body.charset}").defaultValue(overrideIfNotSet(UTF_8)).and()
				.mimetype()
					.tika()
						.failIfOctetStream().defaultValue(overrideIfNotSet(false)).and()
						.and()
					.replace()
						// the distinction between xhtml and html can be useful in some cases
						// most email clients don't understand xhtml mimetype
						// for emails, this distinction must not be done
						.pattern("application/xhtml[^;]*(;.*)?", "text/html$1");
			// @formatter:on
		}

		private static void checkCanUseJavaMail() throws ConfigureException {
			if (!isJakartaMailPresent()) {
				throw new MissingImplementationException("Can't send Email using Java mail (Jakarta) because jakarta.mail API is not present in the classpath", "jakarta.mail.Transport", "jakarta.mail.internet.MimeMessage");
			}
			if (!isJakartaActivationPresent()) {
				throw new MissingImplementationException("Can't send Email using Java mail (Jakarta) because jakarta.activation API is present in the classpath but jakarta.activation is not", "jakarta.activation.DataHandler");
			}
			try {
				checkMailProvidersAvailable();
			} catch (JavaMailConsistencyException e) {
				throw new ClasspathConsistencyException(e.getMessage(), e);
			}
			try {
				checkDataHandlersAvailable();
			} catch (JavaMailConsistencyException e) {
				throw new ClasspathConsistencyException(e.getMessage(), e);
			}
		}

		private static boolean isJakartaMailPresent() {
			return ClasspathUtils.exists("jakarta.mail.Transport")
					&& ClasspathUtils.exists("jakarta.mail.internet.MimeMessage")
					&& ClasspathUtils.exists("jakarta.mail.Session");
		}

		private static boolean isJakartaActivationPresent() {
			return ClasspathUtils.exists("jakarta.activation.DataHandler");
		}

	}

	private DefaultJavaMailConfigurer() {
		super();
	}
}
