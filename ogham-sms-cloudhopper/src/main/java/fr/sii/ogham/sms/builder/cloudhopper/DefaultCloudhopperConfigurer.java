package fr.sii.ogham.sms.builder.cloudhopper;

import static fr.sii.ogham.core.builder.configuration.MayOverride.overrideIfNotSet;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_AUTO_DATA_CODING_SCHEME_ENABLED;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_AUTO_GUESS_ENABLED;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_BIND_TIMEOUT;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_BIND_TYPE;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_CHARSET;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_CLOUDHOPPER_CONFIGURER_PRIORITY;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_CONNECT_MAX_RETRIES;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_CONNECT_RETRY_DELAY;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_CONNECT_TIMEOUT;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_ENQUIRE_LINK_INTERVAL;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_ENQUIRE_LINK_RESPONSE_TIMEOUT;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_ENQUIRE_LINK_REUSE_RESPONSE_TIMEOUT;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_GSM7BIT_PACKED_ENCODING_PRIORITY;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_GSM8_ENCODING_PRIORITY;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_INTERFACE_VERSION;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_KEEP_ALIVE_CONNECT_AT_STARTUP;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_KEEP_ALIVE_ENABLED;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_KEEP_ALIVE_MAX_CONSECUTIVE_TIMEOUTS;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_LAST_INTERACTION_EXPIRATION_DELAY;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.util.ClasspathUtils;
import fr.sii.ogham.sms.splitter.GsmMessageSplitter;

/**
 * Default configurer for Cloudhoppder that is automatically applied every time
 * a {@link MessagingBuilder} instance is created through
 * {@link MessagingBuilder#standard()}.
 * 
 * <p>
 * The configurer has a priority of 40000 in order to be applied after
 * templating configurers.
 * </p>
 * 
 * This configurer is applied only if {@code com.cloudhopper.smpp.SmppClient} is
 * present in the classpath. If not present, Cloudhopper implementation is not
 * registered at all.
 * 
 * <p>
 * This configurer inherits environment configuration (see
 * {@link BuildContext}).
 * </p>
 * 
 * <p>
 * This configurer applies the following configuration:
 * <ul>
 * <li>Configures SMPP protocol:
 * <ul>
 * <li>It uses one of "ogham.sms.cloudhopper.host" or "ogham.sms.smpp.host"
 * property if defined for SMPP server host address (IP or hostname)</li>
 * <li>It uses one of "ogham.sms.cloudhopper.port" or "ogham.sms.smpp.port"
 * property if defined for SMPP server port. Default port is 25</li>
 * <li>It uses "ogham.sms.cloudhopper.interface-version" property if defined for
 * the version of the protocol. Default is "3.4"</li>
 * </ul>
 * </li>
 * <li>Configures authentication:
 * <ul>
 * <li>It uses one of "ogham.sms.cloudhopper.system-id" or
 * "ogham.sms.smpp.system-id" property if defined for to identify an ESME (
 * External Short Message Entity) or an SMSC (Short Message Service Centre) at
 * bind time</li>
 * <li>It uses one of "ogham.sms.cloudhopper.password" or
 * "ogham.sms.smpp.password" property if defined for an optional password</li>
 * </ul>
 * </li>
 * <li>Configures text message encoding:
 * <ul>
 * <li>It enables <a href="https://en.wikipedia.org/wiki/GSM_03.38">GSM
 * 03.38</a> encoding support. It automatically guess the supported encoding in
 * order to use the minimum octets for the text message:
 * <ul>
 * <li>It can encode using GSM 7-bit default alphabet if the message contains
 * only characters defined in the table. Message is packed so the message can
 * have a maximum length of 160 characters instead of 140.<br>
 * It uses one of "ogham.sms.cloudhopper.encoder.gsm7bit-packed.priority" or
 * "ogham.sms.encoder.gsm7bit-packed.priority" to set priority of GSM 7-bit
 * encoding.<br>
 * Default priority is set to 0 (disabled by default because most of providers
 * don't support it).</li>
 * <li>It encodes using GSM 8-bit data encoding if the message contains only
 * characters that can be encoded on one octet.<br>
 * It uses one of "ogham.sms.cloudhopper.encoder.gsm8bit.priority" or
 * "ogham.sms.encoder.gsm8bit.priority" to set priority of GSM 8-bit
 * encoding.<br>
 * Default priority is set to 99000.</li>
 * <li>It encodes using UCS-2 encoding if the message contains special
 * characters (Unicode characters) that can't be encoded on one octet. Each
 * character is encoded on two octets.<br>
 * It uses one of "ogham.sms.cloudhopper.encoder.ucs2.priority" or
 * "ogham.sms.encoder.ucs2.priority" to set priority of GSM 8-bit encoding.<br>
 * Default priority is set to 98000.</li>
 * </ul>
 * </li>
 * <li>If for any reason the message can't be encoded with standard encoding or
 * auto-guess is disabled, the default behavior is used:
 * <ul>
 * <li>It uses "ogham.sms.cloudhopper.encoder.default-charset" property to
 * encode messages using "GSM" (8-bit) charset</li>
 * </ul>
 * </li>
 * </ul>
 * </li>
 * <li>Configures message splitting:
 * <ul>
 * <li>Uses {@link GsmMessageSplitter} to split messages according to encoding:
 * <ul>
 * <li>If message is encoded using GSM 7-bit alphabet (7 bits per character),
 * one message of 160 characters can fit in a single segment of 140 octets (160
 * characters * 7 / 8). If the message is over 160 characters, the message is
 * split in 140 octet segments including a 6 octet header meaning that each
 * segment can transport 153 characters ((140 - 6) * 8 / 7) and a partial
 * character (remaining octet).</li>
 * <li>If message is encoded using GSM 8-bit alphabet (1 octet per character),
 * one message of 140 characters can fit in a single segment of 140 octets. If
 * the message is over 140 characters, the message is split in 140 octet
 * segments including a 6 octet header meaning that each segment can transport
 * 134 characters (140 - 6).</li>
 * <li>If message is encoded using UCS-2 alphabet (2 octets per character), one
 * message of 70 characters can fit in a single segment of 140 octets (70
 * characters * 2). If the message is over 70 characters, the message is split
 * in 140 octet segments including a 6 octet header meaning that each segment
 * can transport 67 characters ((140 - 6) / 2).</li>
 * </ul>
 * </li>
 * </ul>
 * </li>
 * <li>Configures session management:
 * <ul>
 * <li>Timeouts through properties</li>
 * <li>The window management through properties</li>
 * <li>The connection retry handling through properties</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public final class DefaultCloudhopperConfigurer {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultCloudhopperConfigurer.class);

	@ConfigurerFor(targetedBuilder = "standard", priority = DEFAULT_CLOUDHOPPER_CONFIGURER_PRIORITY)
	public static class CloudhopperConfigurer implements MessagingConfigurer {
		@Override
		public void configure(MessagingBuilder msgBuilder) {
			if (!canUseCloudhopper()) {
				LOG.debug("[{}] skip configuration", this);
				return;
			}
			LOG.debug("[{}] apply configuration", this);
			CloudhopperBuilder builder = msgBuilder.sms().sender(CloudhopperBuilder.class);
			// @formatter:off
			builder
				.systemId().properties("${ogham.sms.cloudhopper.system-id}", "${ogham.sms.smpp.system-id}").and()
				.password().properties("${ogham.sms.cloudhopper.password}", "${ogham.sms.smpp.password}").and()
				.host().properties("${ogham.sms.cloudhopper.host}", "${ogham.sms.smpp.host}").and()
				.port().properties("${ogham.sms.cloudhopper.port}", "${ogham.sms.smpp.port}").defaultValue(overrideIfNotSet(DEFAULT_SMPP_PORT)).and()
				.bindType().properties("${ogham.sms.cloudhopper.bind-type}", "${ogham.sms.smpp.bind-type}").defaultValue(overrideIfNotSet(DEFAULT_BIND_TYPE)).and()
				.systemType().properties("${ogham.sms.cloudhopper.system-type}", "${ogham.sms.smpp.system-type}").and()
				.interfaceVersion().properties("${ogham.sms.cloudhopper.interface-version}").defaultValue(overrideIfNotSet(DEFAULT_INTERFACE_VERSION)).and()
				.userData()
					.useShortMessage().properties("${ogham.sms.cloudhopper.user-data.use-short-message}", "${ogham.sms.smpp.user-data.use-short-message}").defaultValue(overrideIfNotSet(DEFAULT_USE_SHORT_MESSAGE)).and()
					.useTlvMessagePayload().properties("${ogham.sms.cloudhopper.user-data.use-tlv-message-payload}", "${ogham.sms.smpp.user-data.use-tlv-message-payload}").defaultValue(overrideIfNotSet(DEFAULT_USE_TLV_MESSAGE_PAYLOAD)).and()
					.and()
				.encoder()
					// packed algorithm disabled by default because it is not supported by most of providers
					.gsm7bitPacked().properties("${ogham.sms.cloudhopper.encoder.gsm7bit-packed.priority}", "${ogham.sms.smpp.encoder.gsm7bit-packed.priority}").defaultValue(overrideIfNotSet(DEFAULT_GSM7BIT_PACKED_ENCODING_PRIORITY)).and()
					.gsm8bit().properties("${ogham.sms.cloudhopper.encoder.gsm8bit.priority}", "${ogham.sms.smpp.encoder.gsm8bit.priority}").defaultValue(overrideIfNotSet(DEFAULT_GSM8_ENCODING_PRIORITY)).and()
					.latin1().properties("${ogham.sms.cloudhopper.encoder.latin1.priority}", "${ogham.sms.smpp.encoder.latin1.priority}").defaultValue(overrideIfNotSet(DEFAULT_LATIN1_ENCODING_PRIORITY)).and()
					.ucs2().properties("${ogham.sms.cloudhopper.encoder.ucs2.priority}", "${ogham.sms.smpp.encoder.ucs2.priority}").defaultValue(overrideIfNotSet(DEFAULT_UCS2_ENCODING_PRIORITY)).and()
					.autoGuess().properties("${ogham.sms.cloudhopper.encoder.auto-guess.enable}", "${ogham.sms.smpp.encoder.auto-guess.enable}").defaultValue(overrideIfNotSet(DEFAULT_AUTO_GUESS_ENABLED)).and()
					.fallback().properties("${ogham.sms.cloudhopper.encoder.default-charset}", "${ogham.sms.smpp.encoder.default-charset}").defaultValue(overrideIfNotSet(DEFAULT_CHARSET)).and()
					.and()
				.splitter()
					.enable().properties("${ogham.sms.cloudhopper.split.enable}", "${ogham.sms.smpp.split.enable}", "${ogham.sms.split.enable}").defaultValue(overrideIfNotSet(DEFAULT_SPLIT_ENABLED)).and()
					.referenceNumber()
						.random()
						.and()
					.and()
				.dataCodingScheme()
					.auto().properties("${ogham.sms.cloudhopper.data-coding-scheme.auto.enable}", "${ogham.sms.smpp.data-coding-scheme.auto.enable}").defaultValue(overrideIfNotSet(DEFAULT_AUTO_DATA_CODING_SCHEME_ENABLED)).and()
					.value().properties("${ogham.sms.cloudhopper.data-coding-scheme.value}", "${ogham.sms.smpp.data-coding-scheme.value}").and()
					.and()
				.session()
					.sessionName().properties("${ogham.sms.cloudhopper.session.name}").and()
					.bindTimeout().properties("${ogham.sms.cloudhopper.session.bind-timeout}").defaultValue(overrideIfNotSet(DEFAULT_BIND_TIMEOUT)).and()
					.connectTimeout().properties("${ogham.sms.cloudhopper.session.connect-timeout}").defaultValue(overrideIfNotSet(DEFAULT_CONNECT_TIMEOUT)).and()
					.requestExpiryTimeout().properties("${ogham.sms.cloudhopper.session.request-expiry-timeout}").defaultValue(overrideIfNotSet(DEFAULT_REQUEST_EXPIRY_TIMEOUT)).and()
					.windowMonitorInterval().properties("${ogham.sms.cloudhopper.session.window-monitor-interval}").defaultValue(overrideIfNotSet(DEFAULT_WINDOW_MONITOR_INTERVAL)).and()
					.windowSize().properties("${ogham.sms.cloudhopper.session.window-size}").defaultValue(overrideIfNotSet(DEFAULT_WINDOW_SIZE)).and()
					.windowWait().properties("${ogham.sms.cloudhopper.session.window-wait-timeout}").defaultValue(overrideIfNotSet(DEFAULT_WINDOW_WAIT_TIMEOUT)).and()
					.writeTimeout().properties("${ogham.sms.cloudhopper.session.write-timeout}").defaultValue(overrideIfNotSet(DEFAULT_WRITE_TIMEOUT)).and()
					.responseTimeout().properties("${ogham.sms.cloudhopper.session.response-timeout}").defaultValue(overrideIfNotSet(DEFAULT_RESPONSE_TIMEOUT)).and()
					.unbindTimeout().properties("${ogham.sms.cloudhopper.session.unbind-timeout}").defaultValue(overrideIfNotSet(DEFAULT_UNBIND_TIMEOUT)).and()
					.reuseSession()
						.enable().properties("${ogham.sms.cloudhopper.session.reuse-session.enable}").defaultValue(overrideIfNotSet(DEFAULT_REUSE_SESSION_ENABLED)).and()
						.lastInteractionExpiration().properties("${ogham.sms.cloudhopper.session.reuse-session.last-interaction-expiration-delay}").defaultValue(overrideIfNotSet(DEFAULT_LAST_INTERACTION_EXPIRATION_DELAY)).and()
						.responseTimeout().properties("${ogham.sms.cloudhopper.session.reuse-session.response-timeout}").defaultValue(overrideIfNotSet(DEFAULT_ENQUIRE_LINK_REUSE_RESPONSE_TIMEOUT)).and()
						.and()
					.keepAlive()
						.enable().properties("${ogham.sms.cloudhopper.session.keep-alive.enable}").defaultValue(overrideIfNotSet(DEFAULT_KEEP_ALIVE_ENABLED)).and()
						.interval().properties("${ogham.sms.cloudhopper.session.keep-alive.request-interval}").defaultValue(overrideIfNotSet(DEFAULT_ENQUIRE_LINK_INTERVAL)).and()
						.responseTimeout().properties("${ogham.sms.cloudhopper.session.keep-alive.response-timeout}").defaultValue(overrideIfNotSet(DEFAULT_ENQUIRE_LINK_RESPONSE_TIMEOUT)).and()
						.connectAtStartup().properties("${ogham.sms.cloudhopper.session.keep-alive.connect-at-startup}").defaultValue(overrideIfNotSet(DEFAULT_KEEP_ALIVE_CONNECT_AT_STARTUP)).and()
						.maxConsecutiveTimeouts().properties("${ogham.sms.cloudhopper.session.keep-alive.max-consecutive-timeouts}").defaultValue(overrideIfNotSet(DEFAULT_KEEP_ALIVE_MAX_CONSECUTIVE_TIMEOUTS)).and()
						.and()
					.connectRetry()
						.retryable(new DefaultConnectRetryablePredicate())
						.fixedDelay()
							.maxRetries().properties("${ogham.sms.cloudhopper.session.connect-retry.max-attempts}").defaultValue(overrideIfNotSet(DEFAULT_CONNECT_MAX_RETRIES)).and()
							.delay().properties("${ogham.sms.cloudhopper.session.connect-retry.delay-between-attempts}").defaultValue(overrideIfNotSet(DEFAULT_CONNECT_RETRY_DELAY)).and()
							.and()
						.exponentialDelay()
							.maxRetries().properties("${ogham.sms.cloudhopper.session.connect-retry.max-attempts}").defaultValue(overrideIfNotSet(DEFAULT_CONNECT_MAX_RETRIES)).and()
							.initialDelay().properties("${ogham.sms.cloudhopper.session.connect-retry.exponential-intial-delay}").and()
							.and()
						.perExecutionDelay()
							.maxRetries().properties("${ogham.sms.cloudhopper.session.connect-retry.max-attempts}").defaultValue(overrideIfNotSet(DEFAULT_CONNECT_MAX_RETRIES)).and()
							.delays().properties("${ogham.sms.cloudhopper.session.connect-retry.per-execution-delays}").and()
							.and()
						.fixedInterval()
							.maxRetries().properties("${ogham.sms.cloudhopper.session.connect-retry.max-attempts}").defaultValue(overrideIfNotSet(DEFAULT_CONNECT_MAX_RETRIES)).and()
							.interval().properties("${ogham.sms.cloudhopper.session.connect-retry.execution-interval}");
			// @formatter:on
		}
		
		private static boolean canUseCloudhopper() {
			return ClasspathUtils.exists("com.cloudhopper.smpp.SmppClient");
		}
	}

	private DefaultCloudhopperConfigurer() {
		super();
	}
}
