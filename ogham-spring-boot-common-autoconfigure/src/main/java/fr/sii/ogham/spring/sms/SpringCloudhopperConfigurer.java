package fr.sii.ogham.spring.sms;

import static fr.sii.ogham.core.builder.configuration.MayOverride.overrideIfNotSet;
import static fr.sii.ogham.core.util.ConfigurationValueUtils.firstValue;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_AUTO_DATA_CODING_SCHEME_ENABLED;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_AUTO_GUESS_ENABLED;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_BIND_TIMEOUT;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_BIND_TYPE;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_CHARSET;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_CONNECT_MAX_RETRIES;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_CONNECT_RETRY_DELAY;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_CONNECT_TIMEOUT;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_ENQUIRE_LINK_INTERVAL;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_ENQUIRE_LINK_RESPONSE_TIMEOUT;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_LAST_INTERACTION_EXPIRATION_DELAY;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_ENQUIRE_LINK_REUSE_RESPONSE_TIMEOUT;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_GSM7BIT_PACKED_ENCODING_PRIORITY;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_GSM8_ENCODING_PRIORITY;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_KEEP_ALIVE_CONNECT_AT_STARTUP;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_KEEP_ALIVE_ENABLED;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_KEEP_ALIVE_MAX_CONSECUTIVE_TIMEOUTS;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_LATIN1_ENCODING_PRIORITY;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_REQUEST_EXPIRY_TIMEOUT;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_RESPONSE_TIMEOUT;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_REUSE_SESSION_ENABLED;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_SMPP_PORT;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_SPLIT_ENABLED;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_UCS2_ENCODING_PRIORITY;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_UNBIND_TIMEOUT;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_USE_SHORT_MESSAGE;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_USE_TLV_MESSAGE_PAYLOAD;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_WINDOW_MONITOR_INTERVAL;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_WINDOW_SIZE;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_WINDOW_WAIT_TIMEOUT;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_WRITE_TIMEOUT;
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
				.port().value(ofNullable(firstValue(cloudhopperProperties.getPort(), smppProperties.getPort()))).defaultValue(overrideIfNotSet(DEFAULT_SMPP_PORT)).and()
				.bindType().value(ofNullable(firstValue(cloudhopperProperties.getBindType(), smppProperties.getBindType()))).defaultValue(overrideIfNotSet(DEFAULT_BIND_TYPE)).and()
				.systemType().value(ofNullable(firstValue(cloudhopperProperties.getSystemType(), smppProperties.getSystemType()))).and()
				.interfaceVersion().value(ofNullable(InterfaceVersion.of(cloudhopperProperties.getInterfaceVersion()))).and()
				.userData()
					.useShortMessage().value(ofNullable(firstValue(cloudhopperProperties.getUserData().getUseShortMessage(), smppProperties.getUserData().getUseShortMessage()))).defaultValue(overrideIfNotSet(DEFAULT_USE_SHORT_MESSAGE)).and()
					.useTlvMessagePayload().value(ofNullable(firstValue(cloudhopperProperties.getUserData().getUseTlvMessagePayload(), smppProperties.getUserData().getUseTlvMessagePayload()))).defaultValue(overrideIfNotSet(DEFAULT_USE_TLV_MESSAGE_PAYLOAD)).and()
					.and()
				.encoder()
					.gsm7bitPacked().value(ofNullable(firstValue(cloudhopperProperties.getEncoder().getGsm7bitPacked().getPriority(), smppProperties.getEncoder().getGsm7bitPacked().getPriority()))).defaultValue(overrideIfNotSet(DEFAULT_GSM7BIT_PACKED_ENCODING_PRIORITY)).and()
					.gsm8bit().value(ofNullable(firstValue(cloudhopperProperties.getEncoder().getGsm8bit().getPriority(), smppProperties.getEncoder().getGsm8bit().getPriority()))).defaultValue(overrideIfNotSet(DEFAULT_GSM8_ENCODING_PRIORITY)).and()
					.latin1().value(ofNullable(firstValue(cloudhopperProperties.getEncoder().getLatin1().getPriority(), smppProperties.getEncoder().getLatin1().getPriority()))).defaultValue(overrideIfNotSet(DEFAULT_LATIN1_ENCODING_PRIORITY)).and()
					.ucs2().value(ofNullable(firstValue(cloudhopperProperties.getEncoder().getUcs2().getPriority(), smppProperties.getEncoder().getUcs2().getPriority()))).defaultValue(overrideIfNotSet(DEFAULT_UCS2_ENCODING_PRIORITY)).and()
					.autoGuess().value(ofNullable(firstValue(cloudhopperProperties.getEncoder().getAutoGuess().getEnable(), smppProperties.getEncoder().getAutoGuess().getEnable()))).defaultValue(overrideIfNotSet(DEFAULT_AUTO_GUESS_ENABLED)).and()
					.fallback().value(ofNullable(firstValue(cloudhopperProperties.getEncoder().getDefaultCharset(), smppProperties.getEncoder().getDefaultCharset()))).defaultValue(overrideIfNotSet(DEFAULT_CHARSET)).and()
					.and()
				.splitter()
					.enable().value(ofNullable(firstValue(cloudhopperProperties.getSplit().getEnable(), smppProperties.getSplit().getEnable(), smsProperties.getSplit().getEnable()))).defaultValue(overrideIfNotSet(DEFAULT_SPLIT_ENABLED)).and()
					.and()
				.dataCodingScheme()
					.auto().value(ofNullable(firstValue(cloudhopperProperties.getDataCodingScheme().getAuto().getEnable(), smppProperties.getDataCodingScheme().getAuto().getEnable()))).defaultValue(overrideIfNotSet(DEFAULT_AUTO_DATA_CODING_SCHEME_ENABLED)).and()
					.and()
				.session()
					.sessionName().value(ofNullable(cloudhopperProperties.getSession().getName())).and()
					.bindTimeout().value(ofNullable(cloudhopperProperties.getSession().getBindTimeout())).defaultValue(overrideIfNotSet(DEFAULT_BIND_TIMEOUT)).and()
					.connectTimeout().value(ofNullable(cloudhopperProperties.getSession().getConnectTimeout())).defaultValue(overrideIfNotSet(DEFAULT_CONNECT_TIMEOUT)).and()
					.requestExpiryTimeout().value(ofNullable(cloudhopperProperties.getSession().getRequestExpiryTimeout())).defaultValue(overrideIfNotSet(DEFAULT_REQUEST_EXPIRY_TIMEOUT)).and()
					.windowMonitorInterval().value(ofNullable(cloudhopperProperties.getSession().getWindowMonitorInterval())).defaultValue(overrideIfNotSet(DEFAULT_WINDOW_MONITOR_INTERVAL)).and()
					.windowSize().value(ofNullable(cloudhopperProperties.getSession().getWindowSize())).defaultValue(overrideIfNotSet(DEFAULT_WINDOW_SIZE)).and()
					.windowWait().value(ofNullable(cloudhopperProperties.getSession().getWindowWaitTimeout())).defaultValue(overrideIfNotSet(DEFAULT_WINDOW_WAIT_TIMEOUT)).and()
					.writeTimeout().value(ofNullable(cloudhopperProperties.getSession().getWriteTimeout())).defaultValue(overrideIfNotSet(DEFAULT_WRITE_TIMEOUT)).and()
					.responseTimeout().value(ofNullable(cloudhopperProperties.getSession().getResponseTimeout())).defaultValue(overrideIfNotSet(DEFAULT_RESPONSE_TIMEOUT)).and()
					.unbindTimeout().value(ofNullable(cloudhopperProperties.getSession().getUnbindTimeout())).defaultValue(overrideIfNotSet(DEFAULT_UNBIND_TIMEOUT)).and()
					.reuseSession()
						.enable().value(ofNullable(cloudhopperProperties.getSession().getReuseSession().getEnable())).defaultValue(overrideIfNotSet(DEFAULT_REUSE_SESSION_ENABLED)).and()
						.lastInteractionExpiration().value(ofNullable(cloudhopperProperties.getSession().getReuseSession().getLastInteractionExpirationDelay())).defaultValue(overrideIfNotSet(DEFAULT_LAST_INTERACTION_EXPIRATION_DELAY)).and()
						.responseTimeout().value(ofNullable(cloudhopperProperties.getSession().getReuseSession().getEnquireLinkTimeout())).defaultValue(overrideIfNotSet(DEFAULT_ENQUIRE_LINK_REUSE_RESPONSE_TIMEOUT)).and()
						.and()
					.keepAlive()
						.enable().value(ofNullable(cloudhopperProperties.getSession().getKeepAlive().getEnable())).defaultValue(overrideIfNotSet(DEFAULT_KEEP_ALIVE_ENABLED)).and()
						.interval().value(ofNullable(cloudhopperProperties.getSession().getKeepAlive().getEnquireLinkInterval())).defaultValue(overrideIfNotSet(DEFAULT_ENQUIRE_LINK_INTERVAL)).and()
						.responseTimeout().value(ofNullable(cloudhopperProperties.getSession().getKeepAlive().getEnquireLinkTimeout())).defaultValue(overrideIfNotSet(DEFAULT_ENQUIRE_LINK_RESPONSE_TIMEOUT)).and()
						.connectAtStartup().value(ofNullable(cloudhopperProperties.getSession().getKeepAlive().getConnectAtStartup())).defaultValue(overrideIfNotSet(DEFAULT_KEEP_ALIVE_CONNECT_AT_STARTUP)).and()
						.maxConsecutiveTimeouts().value(ofNullable(cloudhopperProperties.getSession().getKeepAlive().getMaxConsecutiveTimeouts())).defaultValue(overrideIfNotSet(DEFAULT_KEEP_ALIVE_MAX_CONSECUTIVE_TIMEOUTS)).and()
						.and()
					.connectRetry()
						.fixedDelay()
							.maxRetries().value(ofNullable(cloudhopperProperties.getSession().getConnectRetry().getMaxAttempts())).defaultValue(overrideIfNotSet(DEFAULT_CONNECT_MAX_RETRIES)).and()
							.delay().value(ofNullable(cloudhopperProperties.getSession().getConnectRetry().getDelayBetweenAttempts())).defaultValue(overrideIfNotSet(DEFAULT_CONNECT_RETRY_DELAY));
		// @formatter:on
	}

	@Override
	public int getOrder() {
		return CloudhopperConstants.DEFAULT_CLOUDHOPPER_CONFIGURER_PRIORITY + 1000;
	}

}
