package fr.sii.ogham.email.builder.javamail;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.util.ClasspathUtils;
import fr.sii.ogham.email.sender.impl.javamail.UsernamePasswordAuthenticator;

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
 * {@link EnvironmentBuilder} and
 * {@link JavaMailBuilder#environment(EnvironmentBuilder)}).
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
 * <li>Usesfrom Apache Tika to detect mimetype</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
@ConfigurerFor(targetedBuilder = "standard", priority = 50000)
public class DefaultJavaMailConfigurer implements MessagingConfigurer {

	@Override
	public void configure(MessagingBuilder msgBuilder) {
		if (canUseJavaMail()) {
			JavaMailBuilder builder = msgBuilder.email().sender(JavaMailBuilder.class);
			// use same environment as parent builder
			builder.environment(msgBuilder.environment());
			// @formatter:off
			builder
				.host("${ogham.email.javamail.host}", "${mail.smtp.host}", "${mail.host}")
				.port("${ogham.email.javamail.port}", "${mail.smtp.port}", "${mail.port}", "25")
				.authenticator()
					.username("${ogham.email.javamail.authenticator.username}")
					.password("${ogham.email.javamail.authenticator.password}")
					.and()
				.charset("${ogham.email.javamail.body.charset}", "UTF-8")
				.mimetype()
					.tika()
						.failIfOctetStream(false);
			// @formatter:on
		}
	}

	private boolean canUseJavaMail() {
		return ClasspathUtils.exists("javax.mail.Transport") && ClasspathUtils.exists("javax.mail.internet.MimeMessage");
	}
}
