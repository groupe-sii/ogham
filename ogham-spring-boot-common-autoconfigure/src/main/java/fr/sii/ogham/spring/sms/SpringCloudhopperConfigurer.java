package fr.sii.ogham.spring.sms;

import static java.util.Optional.ofNullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.sms.CloudhopperConstants;
import fr.sii.ogham.sms.builder.cloudhopper.CloudhopperBuilder;
import fr.sii.ogham.sms.builder.cloudhopper.InterfaceVersion;
import fr.sii.ogham.spring.common.SpringMessagingConfigurer;

/**
 * This configurer is also useful to support property naming variants (see
 * <a href=
 * "https://github.com/spring-projects/spring-boot/wiki/relaxed-binding-2.0">Relaxed
 * Binding</a>).
 * 
 * @author Aurélien Baudet
 *
 */
public class SpringCloudhopperConfigurer implements SpringMessagingConfigurer {
	private static final Logger LOG = LoggerFactory.getLogger(SpringCloudhopperConfigurer.class);

	private final OghamSmppProperties smppProperties;
	private final OghamCloudhopperProperties cloudhopperProperties;

	public SpringCloudhopperConfigurer(OghamSmppProperties smppProperties, OghamCloudhopperProperties cloudhopperProperties) {
		super();
		this.smppProperties = smppProperties;
		this.cloudhopperProperties = cloudhopperProperties;
	}

	@Override
	public void configure(MessagingBuilder builder) {
		LOG.debug("[{}] apply configuration", this);
		if (smppProperties != null) {
			applySmppConfiguration(builder);
		}
		if (cloudhopperProperties != null) {
			applyCloudhopperConfiguration(builder);
		}
	}

	private void applySmppConfiguration(MessagingBuilder builder) {
		LOG.debug("[{}] apply general SMPP configuration properties to {}", this, builder);
		// @formatter:off
		builder.sms()
			.sender(CloudhopperBuilder.class)
				.systemId().value(ofNullable(smppProperties.getSystemId())).and()
				.password().value(ofNullable(smppProperties.getPassword())).and()
				.host().value(ofNullable(smppProperties.getHost())).and()
				.port().value(ofNullable(smppProperties.getPort()));
		// @formatter:on
	}

	private void applyCloudhopperConfiguration(MessagingBuilder builder) {
		LOG.debug("[{}] apply Cloudhopper configuration properties to {}", this, builder);
		// @formatter:off
		builder.sms()
			.sender(CloudhopperBuilder.class)
				.systemId().value(ofNullable(cloudhopperProperties.getSystemId())).and()
				.password().value(ofNullable(cloudhopperProperties.getPassword())).and()
				.host().value(ofNullable(cloudhopperProperties.getHost())).and()
				.port().value(ofNullable(cloudhopperProperties.getPort())).and()
				.interfaceVersion().value(ofNullable(InterfaceVersion.of(cloudhopperProperties.getInterfaceVersion()))).and()
				.session()
					.sessionName().value(ofNullable(cloudhopperProperties.getSession().getName())).and()
					.bindTimeout().value(ofNullable(cloudhopperProperties.getSession().getBindTimeout())).and()
					.connectTimeout().value(ofNullable(cloudhopperProperties.getSession().getConnectTimeout())).and()
					.requestExpiryTimeout().value(ofNullable(cloudhopperProperties.getSession().getRequestExpiryTimeout())).and()
					.windowMonitorInterval().value(ofNullable(cloudhopperProperties.getSession().getWindowMonitorInterval())).and()
					.windowSize().value(ofNullable(cloudhopperProperties.getSession().getWindowSize())).and()
					.windowWait().value(ofNullable(cloudhopperProperties.getSession().getWindowWaitTimeout())).and()
					.writeTimeout().value(ofNullable(cloudhopperProperties.getSession().getWriteTimeout())).and()
					.responseTimeout().value(ofNullable(cloudhopperProperties.getSession().getResponseTimeout())).and()
					.unbindTimeout().value(ofNullable(cloudhopperProperties.getSession().getUnbindTimeout())).and()
					.connectRetry()
						.fixedDelay()
							.maxRetries().value(ofNullable(cloudhopperProperties.getSession().getConnectRetry().getMaxAttempts())).and()
							.delay().value(ofNullable(cloudhopperProperties.getSession().getConnectRetry().getDelayBetweenAttempts()));
		// @formatter:on
	}

	@Override
	public int getOrder() {
		return CloudhopperConstants.DEFAULT_CLOUDHOPPER_CONFIGURER_PRIORITY + 1000;
	}

}
