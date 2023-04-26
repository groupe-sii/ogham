package fr.sii.ogham.email.builder.javaxmail;

import static fr.sii.ogham.core.builder.configuration.MayOverride.overrideIfNotSet;
import static fr.sii.ogham.email.JavaxMailConstants.DEFAULT_JAVAX_MAIL_CONFIGURER_PRIORITY;
import static fr.sii.ogham.email.builder.javaxmail.JavaxMailConsistencyChecker.checkDataHandlersAvailable;
import static fr.sii.ogham.email.builder.javaxmail.JavaxMailConsistencyChecker.checkMailProvidersAvailable;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;

import fr.sii.ogham.core.exception.configurer.*;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.util.ClasspathUtils;
import fr.sii.ogham.email.builder.javaxmail.JavaxMailConsistencyChecker.JavaxMailConsistencyException;
import fr.sii.ogham.email.sender.impl.javaxmail.UsernamePasswordAuthenticator;

import javax.mail.Provider;
import javax.mail.Session;
import java.util.*;

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
 * This configurer is applied only if {@code javax.mail.Transport} and
 * {@code javax.mail.internet.MimeMessage} are present in the classpath. If not
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
public final class DefaultJavaxMailConfigurer {
	private static final int DEFAULT_SMTP_PORT = 25;

	@ConfigurerFor(targetedBuilder = "standard", priority = DEFAULT_JAVAX_MAIL_CONFIGURER_PRIORITY)
	public static class JavaxMailConfigurer implements MessagingConfigurer {
		@Override
		public void configure(MessagingBuilder msgBuilder) throws ConfigureException {
			checkCanUseJavaMail();

			JavaxMailBuilder builder = msgBuilder.email().sender(JavaxMailBuilder.class);
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
			if (!isJavaxMailApiPresent()) {
				throw new MissingImplementationException("Can't send Email through javax.mail because javax.mail API is not present in the classpath", "javax.mail.Transport", "javax.mail.internet.MimeMessage", "javax.mail.Session");
			}
			if (!isJavaxActivationPresent()) {
				throw new MissingImplementationException("Can't send Email through javax.mail because javax.activation API is not present in the classpath", "javax.activation.DataHandler");
			}
			if (!isSunMailPresent()) {
				throw new MissingImplementationException("Can't send Email through javax.mail because javax.mail API is present in the classpath but it requires the class 'com.sun.mail.util.MailLogger' which is not present in the classpath", "com.sun.mail.util.MailLogger");
			}
			try {
				checkMailProvidersAvailable();
			} catch (JavaxMailConsistencyException e) {
				throw new ClasspathConsistencyException(e.getMessage(), e);
			}
			try {
				checkDataHandlersAvailable();
			} catch (JavaxMailConsistencyException e) {
				throw new ClasspathConsistencyException(e.getMessage(), e);
			}
		}

		private static boolean isJavaxMailApiPresent() {
			return ClasspathUtils.exists("javax.mail.Transport")
					&& ClasspathUtils.exists("javax.mail.internet.MimeMessage")
					&& ClasspathUtils.exists("javax.mail.Session");
		}

		private static boolean isJavaxActivationPresent() {
			return ClasspathUtils.exists("javax.activation.DataHandler");
		}

		private static boolean isSunMailPresent() {
			// javax.mail <= 1.6.7 has direct dependency to com.sun.mail (which is stupid by the way)
			// but it can't work if this class is not present
			return ClasspathUtils.exists("com.sun.mail.util.MailLogger");
		}

	}

	private DefaultJavaxMailConfigurer() {
		super();
	}
}
