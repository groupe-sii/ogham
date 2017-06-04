package fr.sii.ogham.spring.sms;

import org.springframework.boot.bind.RelaxedNames;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.sms.OvhSmsConstants;
import fr.sii.ogham.sms.builder.ovh.OvhSmsBuilder;
import fr.sii.ogham.spring.common.SpringMessagingConfigurer;

/**
 * This configurer is useful to support property naming variants (see
 * {@link RelaxedNames}).
 * 
 * @author Aurélien Baudet
 *
 */
public class SpringOvhSmsConfigurer implements SpringMessagingConfigurer {
	private final OghamOvhSmsProperties properties;

	public SpringOvhSmsConfigurer(OghamOvhSmsProperties properties) {
		super();
		this.properties = properties;
	}

	@Override
	public void configure(MessagingBuilder builder) {
		// use same environment as parent builder
		builder.sms().sender(OvhSmsBuilder.class).environment(builder.environment());
		if (properties != null) {
			applyOghamConfiguration(builder);
		}
	}

	private void applyOghamConfiguration(MessagingBuilder builder) {
		// @formatter:off
		builder.sms().sender(OvhSmsBuilder.class)
			.url(properties.getUrl())
			.account(properties.getAccount())
			.login(properties.getLogin())
			.password(properties.getPassword())
			.options()
				.noStop(properties.getOptions().isNoStop())
				.smsCoding(properties.getOptions().getSmsCoding())
				.tag(properties.getOptions().getTag());
		// @formatter:on
	}

	@Override
	public int getOrder() {
		return OvhSmsConstants.DEFAULT_OVHSMS_CONFIGURER_PRIORITY + 1000;
	}

}
