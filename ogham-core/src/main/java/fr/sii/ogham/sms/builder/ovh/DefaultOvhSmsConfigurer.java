package fr.sii.ogham.sms.builder.ovh;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;

@ConfigurerFor(targetedBuilder="standard", priority=700)
public class DefaultOvhSmsConfigurer implements MessagingConfigurer {

	@Override
	public void configure(MessagingBuilder msgBuilder) {
		OvhSmsBuilder builder = msgBuilder.sms().sender(OvhSmsBuilder.class);
		// use same environment as parent builder
		builder.environment(msgBuilder.environment());
		// @formatter:off
		builder
			.url("${ogham.sms.ovh.url}", "https://www.ovh.com/cgi-bin/sms/http2sms.cgi")
			.account("${ogham.sms.ovh.account}")
			.login("${ogham.sms.ovh.login}")
			.password("${ogham.sms.ovh.password}")
			.options()
				.noStop("${ogham.sms.ovh.no-stop}", "true")
				.smsCoding("${ogham.sms.ovh.sms-coding}")
				.tag("${ogham.sms.ovh.tag}");
		// @formatter:on
	}

}
