package fr.sii.ogham.email.builder.javamail;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.util.ClasspathUtils;

@ConfigurerFor(targetedBuilder="standard", priority=800)
public class DefaultJavaMailConfigurer implements MessagingConfigurer {

	public void configure(MessagingBuilder msgBuilder) {
		if(canUseJavaMail()) {
			JavaMailBuilder builder = msgBuilder.email().sender(JavaMailBuilder.class);
			// use same environment as parent builder
			builder.environment(msgBuilder.environment());
			// @formatter:off
			builder
				.host("${ogham.email.javamail.host}", "${mail.smtp.host}", "${mail.host}")
				.port("${ogham.email.javamail.port}", "${mail.smtp.port}", "${mail.port}")
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
