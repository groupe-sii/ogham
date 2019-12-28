package fr.sii.ogham.spring.sms;

import static java.util.Optional.ofNullable;

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.sms.OvhSmsConstants;
import fr.sii.ogham.sms.builder.ovh.OvhSmsBuilder;
import fr.sii.ogham.spring.common.SpringMessagingConfigurer;

/**
 * This configurer is also useful to support property naming variants (see
 * <a href="https://github.com/spring-projects/spring-boot/wiki/relaxed-binding-2.0">Relaxed Binding</a>).
 * 
 * @author Aurélien Baudet
 *
 */
public class SpringOvhSmsConfigurer implements SpringMessagingConfigurer {
	private static final Logger LOG = LoggerFactory.getLogger(SpringOvhSmsConfigurer.class);
	
	private final OghamOvhSmsProperties properties;

	public SpringOvhSmsConfigurer(OghamOvhSmsProperties properties) {
		super();
		this.properties = properties;
	}

	@Override
	public void configure(MessagingBuilder builder) {
		LOG.debug("[{}] apply configuration", this);
		// use same environment as parent builder
		builder.sms().sender(OvhSmsBuilder.class).environment(builder.environment());
		if (properties != null) {
			applyOghamConfiguration(builder);
		}
	}

	private void applyOghamConfiguration(MessagingBuilder builder) {
		LOG.debug("[{}] apply ogham configuration properties to {}", this, builder);
		// @formatter:off
		builder.sms().sender(OvhSmsBuilder.class)
			.url().value(ofNullable(getUrl())).and()
			.account().value(ofNullable(properties.getAccount())).and()
			.login().value(ofNullable(properties.getLogin())).and()
			.password().value(ofNullable(properties.getPassword())).and()
			.options()
				.noStop().value(ofNullable(properties.getOptions().isNoStop())).and()
				.smsCoding().value(ofNullable(properties.getOptions().getSmsCoding())).and()
				.tag().value(ofNullable(properties.getOptions().getTag()));
		// @formatter:on
	}

	private URL getUrl() {
		String url = properties.getUrl();
		try {
			return url == null ? null : new URL(url);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Invalid URL "+url, e);
		}
	}

	@Override
	public int getOrder() {
		return OvhSmsConstants.DEFAULT_OVHSMS_CONFIGURER_PRIORITY + 1000;
	}

}
