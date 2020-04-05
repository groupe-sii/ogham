package fr.sii.ogham.spring.sms;

import static fr.sii.ogham.core.util.ConfigurationValueUtils.firstValue;
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

	private final OghamSmsProperties smsProperties;
	private final OghamSmppProperties smppProperties;
	private final OghamCloudhopperProperties cloudhopperProperties;


	public SpringCloudhopperConfigurer(OghamSmsProperties smsProperties, OghamSmppProperties smppProperties, OghamCloudhopperProperties cloudhopperProperties) {
		super();
		this.smsProperties = smsProperties != null ? smsProperties : new OghamSmsProperties();
		this.smppProperties = smppProperties != null ? smppProperties : new OghamSmppProperties();
		this.cloudhopperProperties = cloudhopperProperties != null ? cloudhopperProperties : new OghamCloudhopperProperties();
	}

	@Override
	public void configure(MessagingBuilder builder) {
		LOG.debug("[{}] apply Cloudhopper configuration properties to {}", this, builder);
		// @formatter:off
		builder.sms()
			.sender(CloudhopperBuilder.class)
				.systemId().value(ofNullable(firstValue(cloudhopperProperties.getSystemId(), smppProperties.getSystemId()))).and()
				.password().value(ofNullable(firstValue(cloudhopperProperties.getPassword(), smppProperties.getPassword()))).and()
				.host().value(ofNullable(firstValue(cloudhopperProperties.getHost(), smppProperties.getHost()))).and()
				.port().value(ofNullable(firstValue(cloudhopperProperties.getPort(), smppProperties.getPort()))).and()
				.bindType().value(ofNullable(firstValue(cloudhopperProperties.getBindType(), smppProperties.getBindType()))).and()
				.systemType().value(ofNullable(firstValue(cloudhopperProperties.getSystemType(), smppProperties.getSystemType()))).and()
				.interfaceVersion().value(ofNullable(InterfaceVersion.of(cloudhopperProperties.getInterfaceVersion()))).and()
				.userData()
					.useShortMessage().value(ofNullable(firstValue(cloudhopperProperties.getUserData().getUseShortMessage(), smppProperties.getUserData().getUseShortMessage()))).and()
					.useTlvMessagePayload().value(ofNullable(firstValue(cloudhopperProperties.getUserData().getUseTlvMessagePayload(), smppProperties.getUserData().getUseTlvMessagePayload()))).and()
					.and()
				.encoder()
					.gsm7bitPacked().value(ofNullable(firstValue(cloudhopperProperties.getEncoder().getGsm7bitPacked().getPriority(), smppProperties.getEncoder().getGsm7bitPacked().getPriority()))).and()
					.gsm8bit().value(ofNullable(firstValue(cloudhopperProperties.getEncoder().getGsm8bit().getPriority(), smppProperties.getEncoder().getGsm8bit().getPriority()))).and()
					.latin1().value(ofNullable(firstValue(cloudhopperProperties.getEncoder().getLatin1().getPriority(), smppProperties.getEncoder().getLatin1().getPriority()))).and()
					.ucs2().value(ofNullable(firstValue(cloudhopperProperties.getEncoder().getUcs2().getPriority(), smppProperties.getEncoder().getUcs2().getPriority()))).and()
					.autoGuess().value(ofNullable(firstValue(cloudhopperProperties.getEncoder().getAutoGuess().getEnable(), smppProperties.getEncoder().getAutoGuess().getEnable()))).and()
					.fallback().value(ofNullable(firstValue(cloudhopperProperties.getEncoder().getDefaultCharset(), smppProperties.getEncoder().getDefaultCharset()))).and()
					.and()
				.splitter()
					.enable().value(ofNullable(firstValue(cloudhopperProperties.getSplit().getEnable(), smppProperties.getSplit().getEnable(), smsProperties.getSplit().getEnable()))).and()
					.and()
				.dataCodingScheme()
					.auto().value(ofNullable(firstValue(cloudhopperProperties.getDataCodingScheme().getAuto().getEnable(), smppProperties.getDataCodingScheme().getAuto().getEnable()))).and()
					.and()
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
					.reuseSession()
						.enable().value(ofNullable(cloudhopperProperties.getSession().getReuseSession().getEnable())).and()
						.lastInteractionExpiration().value(ofNullable(cloudhopperProperties.getSession().getReuseSession().getLastInteractionExpirationDelay())).and()
						.responseTimeout().value(ofNullable(cloudhopperProperties.getSession().getReuseSession().getEnquireLinkTimeout())).and()
						.and()
					.keepAlive()
						.enable().value(ofNullable(cloudhopperProperties.getSession().getKeepAlive().getEnable())).and()
						.interval().value(ofNullable(cloudhopperProperties.getSession().getKeepAlive().getEnquireLinkInterval())).and()
						.responseTimeout().value(ofNullable(cloudhopperProperties.getSession().getKeepAlive().getEnquireLinkTimeout())).and()
						.connectAtStartup().value(ofNullable(cloudhopperProperties.getSession().getKeepAlive().getConnectAtStartup())).and()
						.maxConsecutiveTimeouts().value(ofNullable(cloudhopperProperties.getSession().getKeepAlive().getMaxConsecutiveTimeouts())).and()
						.and()
					.connectRetry()
						.fixedDelay()
							.maxRetries().value(ofNullable(cloudhopperProperties.getSession().getConnectRetry().getMaxAttempts())).and()
							.delay().value(ofNullable(cloudhopperProperties.getSession().getConnectRetry().getDelayBetweenAttempts())).and()
							.and()
						.exponentialDelay()
							.maxRetries().value(ofNullable(cloudhopperProperties.getSession().getConnectRetry().getMaxAttempts())).and()
							.initialDelay().value(ofNullable(cloudhopperProperties.getSession().getConnectRetry().getExponentialInitialDelay())).and()
							.and()
						.perExecutionDelay()
							.maxRetries().value(ofNullable(cloudhopperProperties.getSession().getConnectRetry().getMaxAttempts())).and()
							.delays().value(ofNullable(cloudhopperProperties.getSession().getConnectRetry().getPerExecutionDelaysAsArray())).and()
							.and()
						.fixedInterval()
							.maxRetries().value(ofNullable(cloudhopperProperties.getSession().getConnectRetry().getMaxAttempts())).and()
							.interval().value(ofNullable(cloudhopperProperties.getSession().getConnectRetry().getExecutionInterval()));
		// @formatter:on
	}

	@Override
	public int getOrder() {
		return CloudhopperConstants.DEFAULT_CLOUDHOPPER_CONFIGURER_PRIORITY + 1000;
	}

}
