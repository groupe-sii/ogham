package fr.sii.ogham.spring.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.sms.CloudhopperConstants;
import fr.sii.ogham.sms.builder.cloudhopper.CloudhopperBuilder;
import fr.sii.ogham.spring.common.SpringMessagingConfigurer;

/**
 * This configurer is also useful to support property naming variants (see
 * <a href="https://github.com/spring-projects/spring-boot/wiki/relaxed-binding-2.0">Relaxed Binding</a>).
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
		// use same environment as parent builder
		builder.sms().sender(CloudhopperBuilder.class).environment(builder.environment());
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
				.systemId(smppProperties.getSystemId())
				.password(smppProperties.getPassword())
				.host(smppProperties.getHost())
				.port(smppProperties.getPort());
		// @formatter:on
	}

	private void applyCloudhopperConfiguration(MessagingBuilder builder) {
		LOG.debug("[{}] apply Cloudhopper configuration properties to {}", this, builder);
		// @formatter:off
		builder.sms()
			.sender(CloudhopperBuilder.class)
				.charset()
					.convert(cloudhopperProperties.getDefaultAppCharset(), cloudhopperProperties.getSmppCharset())
					.detector()
						.defaultCharset(cloudhopperProperties.getDefaultAppCharset())
						.and()
					.and()
				.systemId(cloudhopperProperties.getSystemId())
				.password(cloudhopperProperties.getPassword())
				.host(cloudhopperProperties.getHost())
				.port(cloudhopperProperties.getPort())
				.interfaceVersion(cloudhopperProperties.getInterfaceVersion())
				.session()
					.sessionName(cloudhopperProperties.getSession().getSessionName())
					.bindTimeout(cloudhopperProperties.getSession().getBindTimeout())
					.connectTimeout(cloudhopperProperties.getSession().getConnectTimeout())
					.requestExpiryTimeout(cloudhopperProperties.getSession().getRequestExpiryTimeout())
					.windowMonitorInterval(cloudhopperProperties.getSession().getWindowMonitorInterval())
					.windowSize(cloudhopperProperties.getSession().getWindowSize())
					.windowWait(cloudhopperProperties.getSession().getWindowWaitTimeout())
					.writeTimeout(cloudhopperProperties.getSession().getWriteTimeout())
					.responseTimeout(cloudhopperProperties.getSession().getResponseTimeout())
					.unbindTimeout(cloudhopperProperties.getSession().getUnbindTimeout())
					.connectRetry()
						.fixedDelay()
							.maxRetries(cloudhopperProperties.getSession().getConnectRetry().getConnectMaxRetry())
							.delay(cloudhopperProperties.getSession().getConnectRetry().getConnectRetryDelay());
		// @formatter:on
	}

	@Override
	public int getOrder() {
		return CloudhopperConstants.DEFAULT_CLOUDHOPPER_CONFIGURER_PRIORITY + 1000;
	}

}
