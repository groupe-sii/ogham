package fr.sii.ogham.sms.builder.smsglobal;

import static fr.sii.ogham.core.util.BuilderUtils.evaluate;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_CLOUDHOPPER_CONFIGURER_PRIORITY;
import static fr.sii.ogham.sms.builder.cloudhopper.InterfaceVersion.VERSION_3_4;
import static java.util.Arrays.asList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.sms.builder.cloudhopper.CloudhopperBuilder;
import fr.sii.ogham.sms.builder.cloudhopper.DefaultCloudhopperConfigurer;
import fr.sii.ogham.sms.splitter.GsmMessageSplitter;

/**
 * Default configurer for Cloudhoppder that is automatically applied every time
 * a {@link MessagingBuilder} instance is created through
 * {@link MessagingBuilder#standard()}.
 * 
 * <p>
 * The configurer has a priority of 40001 in order to be applied before
 * {@link DefaultCloudhopperConfigurer}.
 * </p>
 * 
 * This configurer is applied only if:
 * <ul>
 * <li>{@code com.cloudhopper.smpp.SmppClient} is present in the classpath</li>
 * <li>and the property
 * "ogham.sms.automatic-service-provider-configuration.enable" is true (default
 * value)</li>
 * <li>and the property "ogham.sms.cloudhopper.host" or "ogham.sms.smpp.host"
 * equals "smsglobal.com"</li>
 * </ul>
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
 * <li>Configures SMSGlobal service info:
 * <ul>
 * <li>Set "ogham.sms.cloudhopper.port" property value to 1175</li>
 * <li>Set "ogham.sms.cloudhopper.interface-version" property value to
 * "3.4"</li>
 * </ul>
 * </li>
 * <li>Configures encoding:
 * <ul>
 * <li>Set "ogham.sms.cloudhopper.encoder.gsm-7bit.priority" property to 0 to
 * disable GSM 7-bit encoding (not supported by SMSGlobal)</li>
 * <li>Let default value for "ogham.sms.cloudhopper.encoder.gsm-8bit.priority"
 * to enable GSM 8-bit data encoding if the message contains only characters
 * that can be encoded on one octet.</li>
 * <li>Let default value for "ogham.sms.cloudhopper.encoder.ucs-2.priority" to
 * enable UCS-2 encoding if the message contains special characters that can't
 * be encoded on one octet. Each character is encoded on two octets.</li>
 * </ul>
 * </li>
 * <li>TODO: Configures message splitting (force TLV?):
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
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
@ConfigurerFor(targetedBuilder = "standard", priority = DEFAULT_CLOUDHOPPER_CONFIGURER_PRIORITY + 1)
public class SmsglobalServiceProviderConfigurer implements MessagingConfigurer {
	private static final Logger LOG = LoggerFactory.getLogger(SmsglobalServiceProviderConfigurer.class);
	private static final int SMSGLOBAL_PORT = 1775;

	@Override
	public void configure(MessagingBuilder msgBuilder) {
		if (!usingSmsGlobal(msgBuilder.environment().build())) {
			LOG.debug("[{}] skip service provider configuration", this);
			return;
		}
		LOG.debug("[{}] apply service provider configuration", this);
		CloudhopperBuilder builder = msgBuilder.sms().sender(CloudhopperBuilder.class);
		// use same environment as parent builder
		builder.environment(msgBuilder.environment());
		// @formatter:off
		builder
			.userData()
				// both supported but to benefit from 160 characters messages, we have to use Tlv message_payload because GSM 7-bit is not supported
				.useShortMessage().defaultValue(false).and()
				.useTlvMessagePayload().defaultValue(true).and()
				.and()
			.encoder()
				.gsm7bitPacked().defaultValue(0).and()	// not supported by SmsGlobal
				.and()
			.dataCodingScheme()
				.custom(new SmsGlobalDataCodingProvider())
				.and()
			.splitter()
				.enable().defaultValue(false).and()		// do not split when using Tlv message_payload
				.and()
			.port().defaultValue(SMSGLOBAL_PORT).and()
			.interfaceVersion().defaultValue(VERSION_3_4);
		// @formatter:on
	}

	private static boolean usingSmsGlobal(PropertyResolver propertyResolver) {
		String host = evaluate(asList("${ogham.sms.cloudhopper.host}", "${ogham.sms.smpp.host}"), propertyResolver, String.class);
		return "smsglobal.com".equals(host);
	}
}
