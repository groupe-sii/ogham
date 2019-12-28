package fr.sii.ogham.sms.builder.cloudhopper;

import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM;
import static fr.sii.ogham.core.builder.configuration.MayOverride.overrideIfNotSet;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_CLOUDHOPPER_CONFIGURER_PRIORITY;
import static fr.sii.ogham.sms.builder.cloudhopper.InterfaceVersion.VERSION_3_4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
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
 * {@link EnvironmentBuilder} and
 * {@link CloudhopperBuilder#environment(EnvironmentBuilder)}).
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
 * It uses one of "ogham.sms.cloudhopper.encoder.gsm-7bit-packed.priority" or
 * "ogham.sms.encoder.gsm-7bit-packed.priority" to set priority of GSM 7-bit
 * encoding.<br>
 * Default priority is set to 0 (disabled by default because most of providers
 * don't support it).</li>
 * <li>It encodes using GSM 8-bit data encoding if the message contains only
 * characters that can be encoded on one octet.<br>
 * It uses one of "ogham.sms.cloudhopper.encoder.gsm-8bit.priority" or
 * "ogham.sms.encoder.gsm-8bit.priority" to set priority of GSM 8-bit
 * encoding.<br>
 * Default priority is set to 99000.</li>
 * <li>It encodes using UCS-2 encoding if the message contains special
 * characters (Unicode characters) that can't be encoded on one octet. Each
 * character is encoded on two octets.<br>
 * It uses one of "ogham.sms.cloudhopper.encoder.ucs-2.priority" or
 * "ogham.sms.encoder.ucs-2.priority" to set priority of GSM 8-bit encoding.<br>
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
@ConfigurerFor(targetedBuilder = "standard", priority = DEFAULT_CLOUDHOPPER_CONFIGURER_PRIORITY)
public class DefaultCloudhopperConfigurer implements MessagingConfigurer {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultCloudhopperConfigurer.class);
	private static final int DEFAULT_SMPP_PORT = 2775;
	private static final int DEFAULT_GSM8_ENCODING_PRIORITY = 99000;
	private static final int DEFAULT_LATIN1_ENCODING_PRIORITY = 98000;
	private static final int DEFAULT_UCS2_ENCODING_PRIORITY = 90000;
	private static final long DEFAULT_BIND_TIMEOUT = 5000L;
	private static final long DEFAULT_CONNECT_TIMEOUT = 10000L;
	private static final long DEFAULT_REQUEST_EXPIRY_TIMEOUT = -1L;
	private static final long DEFAULT_WINDOW_MONITOR_INTERVAL = -1L;
	private static final int DEFAULT_WINDOW_SIZE = 1;
	private static final long DEFAULT_WINDOW_WAIT_TIMEOUT = 60000L;
	private static final long DEFAULT_WRITE_TIMEOUT = 0L;
	private static final long DEFAULT_RESPONSE_TIMEOUT = 5000L;
	private static final long DEFAULT_UNBIND_TIMEOUT = 5000L;
	private static final int DEFAULT_CONNECT_MAX_RETRIES = 5;
	private static final long DEFAULT_CONNECT_RETRY_DELAY = 500L;

	@Override
	public void configure(MessagingBuilder msgBuilder) {
		if (!canUseCloudhopper()) {
			LOG.debug("[{}] skip configuration", this);
			return;
		}
		LOG.debug("[{}] apply configuration", this);
		CloudhopperBuilder builder = msgBuilder.sms().sender(CloudhopperBuilder.class);
		// use same environment as parent builder
		builder.environment(msgBuilder.environment());
		// @formatter:off
		builder
			.userData()
				.useShortMessage().properties("${ogham.sms.cloudhopper.user-data.use-short-message}", "${ogham.sms.user-data.use-short-message}").defaultValue(overrideIfNotSet(true)).and()
				.useTlvMessagePayload().properties("${ogham.sms.cloudhopper.user-data.use-tlv-message-payload}", "${ogham.sms.user-data.use-tlv-message-payload}").defaultValue(overrideIfNotSet(false)).and()
				.and()
			.encoder()
				// packed algorithm disabled by default because it is not supported by most of providers
				.gsm7bitPacked().properties("${ogham.sms.cloudhopper.encoder.gsm-7bit-packed.priority}", "${ogham.sms.encoder.gsm-7bit-packed.priority}").and()
				.gsm8bit().properties("${ogham.sms.cloudhopper.encoder.gsm-8bit.priority}", "${ogham.sms.encoder.gsm-8bit.priority}").defaultValue(overrideIfNotSet(DEFAULT_GSM8_ENCODING_PRIORITY)).and()
				.latin1().properties("${ogham.sms.cloudhopper.encoder.latin1.priority}", "${ogham.sms.encoder.latin1.priority}").defaultValue(overrideIfNotSet(DEFAULT_LATIN1_ENCODING_PRIORITY)).and()
				.ucs2().properties("${ogham.sms.cloudhopper.encoder.ucs-2.priority}", "${ogham.sms.encoder.ucs-2.priority}").defaultValue(overrideIfNotSet(DEFAULT_UCS2_ENCODING_PRIORITY)).and()
				.autoGuess().properties("${ogham.sms.cloudhopper.encoder.auto-guess}", "${ogham.sms.encoder.auto-guess}").defaultValue(overrideIfNotSet(true)).and()
				.fallback().properties("${ogham.sms.cloudhopper.encoder.default-charset}").defaultValue(overrideIfNotSet(NAME_GSM)).and()
				.and()
			.splitter()
				.enable().properties("${ogham.sms.cloudhopper.split.enable}", "${ogham.sms.split.enable}").defaultValue(overrideIfNotSet(true)).and()
				.referenceNumber()
					.random()
					.and()
				.and()
			.dataCodingScheme()
				.auto().properties("${ogham.sms.cloudhopper.data-coding-scheme.auto.enable}", "${ogham.sms.data-coding-scheme.auto.enable}").defaultValue(overrideIfNotSet(true)).and()
				.value().properties("${ogham.sms.cloudhopper.data-coding-scheme.value}", "${ogham.sms.data-coding-scheme.value}").and()
				.and()
			.systemId().properties("${ogham.sms.cloudhopper.system-id}", "${ogham.sms.smpp.system-id}").and()
			.password().properties("${ogham.sms.cloudhopper.password}", "${ogham.sms.smpp.password}").and()
			.host().properties("${ogham.sms.cloudhopper.host}", "${ogham.sms.smpp.host}").and()
			.port().properties("${ogham.sms.cloudhopper.port}", "${ogham.sms.smpp.port}").defaultValue(overrideIfNotSet(DEFAULT_SMPP_PORT)).and()
			.bindType().properties("${ogham.sms.cloudhopper.bind-type}", "${ogham.sms.smpp.bind-type}").and()
			.systemType().properties("${ogham.sms.cloudhopper.system-type}", "${ogham.sms.smpp.system-type}").and()
			.interfaceVersion().properties("${ogham.sms.cloudhopper.interface-version}").defaultValue(overrideIfNotSet(VERSION_3_4)).and()
			.session()
				.sessionName().properties("${ogham.sms.cloudhopper.session-name}").and()
				.bindTimeout().properties("${ogham.sms.cloudhopper.bind-timeout}").defaultValue(overrideIfNotSet(DEFAULT_BIND_TIMEOUT)).and()
				.connectTimeout().properties("${ogham.sms.cloudhopper.connect-timeout}").defaultValue(overrideIfNotSet(DEFAULT_CONNECT_TIMEOUT)).and()
				.requestExpiryTimeout().properties("${ogham.sms.cloudhopper.request-expiry-timeout}").defaultValue(overrideIfNotSet(DEFAULT_REQUEST_EXPIRY_TIMEOUT)).and()
				.windowMonitorInterval().properties("${ogham.sms.cloudhopper.window-monitor-interval}").defaultValue(overrideIfNotSet(DEFAULT_WINDOW_MONITOR_INTERVAL)).and()
				.windowSize().properties("${ogham.sms.cloudhopper.window-size}").defaultValue(overrideIfNotSet(DEFAULT_WINDOW_SIZE)).and()
				.windowWait().properties("${ogham.sms.cloudhopper.window-wait-timeout}").defaultValue(overrideIfNotSet(DEFAULT_WINDOW_WAIT_TIMEOUT)).and()
				.writeTimeout().properties("${ogham.sms.cloudhopper.write-timeout}").defaultValue(overrideIfNotSet(DEFAULT_WRITE_TIMEOUT)).and()
				.responseTimeout().properties("${ogham.sms.cloudhopper.response-timeout}").defaultValue(overrideIfNotSet(DEFAULT_RESPONSE_TIMEOUT)).and()
				.unbindTimeout().properties("${ogham.sms.cloudhopper.unbind-timeout}").defaultValue(overrideIfNotSet(DEFAULT_UNBIND_TIMEOUT)).and()
				.connectRetry()
					.fixedDelay()
						.maxRetries().properties("${ogham.sms.cloudhopper.connect-max-retry}").defaultValue(overrideIfNotSet(DEFAULT_CONNECT_MAX_RETRIES)).and()
						.delay().properties("${ogham.sms.cloudhopper.connect-retry-delay}").defaultValue(overrideIfNotSet(DEFAULT_CONNECT_RETRY_DELAY));
		// @formatter:on
	}

	private static boolean canUseCloudhopper() {
		return ClasspathUtils.exists("com.cloudhopper.smpp.SmppClient");
	}

}
