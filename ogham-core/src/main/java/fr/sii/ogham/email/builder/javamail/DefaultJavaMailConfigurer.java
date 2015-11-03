package fr.sii.ogham.email.builder.javamail;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.util.ClasspathUtils;

@ConfigurerFor(targetedBuilder="standard", priority=800)
public class DefaultJavaMailConfigurer implements MessagingConfigurer {

	public void configure(MessagingBuilder msgBuilder) {
		// TODO: check JavaMail available at startup or after (depends on properties) ?
		if(canUseJavaMail()) {
			JavaMailBuilder builder = msgBuilder.email().sender(JavaMailBuilder.class);
			// use same environment as parent builder
			builder.environment(msgBuilder.environment());
			// @formatter:off
			builder
				.host("${ogham.email.host}", "${mail.smtp.host}", "${mail.host}")
				.port("${ogham.email.port}", "${mail.smtp.port}", "${mail.port}")
				.authenticator()
					.username("${ogham.email.authenticator.username}")
					.password("${ogham.email.authenticator.password}")
					.and()
				.charset("${ogham.email.body.charset}", "UTF-8")
				.mimetype()
					.tika()
						.failIfOctetStream(false);
			// @formatter:on
		}
	}

	private boolean canUseJavaMail() {
		return ClasspathUtils.exists("javax.mail.Transport") && ClasspathUtils.exists("com.sun.mail.smtp.SMTPTransport");
	}
}
