package fr.sii.ogham.sms.builder.smsglobal;

import static fr.sii.ogham.core.util.BuilderUtils.evaluate;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_CLOUDHOPPER_CONFIGURER_PRIORITY;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_GSM8_ENCODING_PRIORITY;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_UCS2_ENCODING_PRIORITY;
import static fr.sii.ogham.sms.builder.cloudhopper.InterfaceVersion.VERSION_3_4;
import static java.util.Arrays.asList;

import fr.sii.ogham.core.exception.configurer.AutomaticServiceProviderConfigurationSkippedException;
import fr.sii.ogham.core.exception.configurer.ConfigureException;
import fr.sii.ogham.core.exception.configurer.MissingImplementationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.util.ClasspathUtils;
import fr.sii.ogham.sms.builder.cloudhopper.CloudhopperBuilder;
import fr.sii.ogham.sms.builder.cloudhopper.DefaultCloudhopperConfigurer;

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
 * equals "smpp.smsglobal.com"</li>
 * </ul>
 * 
 * <p>
 * This configurer inherits environment configuration (see
 * {@link BuildContext}).
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
 * <li>Set "ogham.sms.cloudhopper.encoder.gsm7bit-packed.priority" property to 0
 * to disable GSM 7-bit encoding (not supported by SMSGlobal)</li>
 * <li>Let default value for "ogham.sms.cloudhopper.encoder.gsm8bit.priority" to
 * enable GSM 8-bit data encoding if the message contains only characters that
 * can be encoded on one octet.</li>
 * <li>Let default value for "ogham.sms.cloudhopper.encoder.ucs2.priority" to
 * enable UCS-2 encoding if the message contains special characters that can't
 * be encoded on one octet. Each character is encoded on two octets.</li>
 * </ul>
 * </li>
 * <li>Configures message splitting:
 * <ul>
 * <li>Split is not done by Ogham but directly by Smsglobal instead. The message
 * is transmitted using "message_payload" TLV parameter.</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public final class SmsglobalServiceProviderConfigurer {
	private static final Logger LOG = LoggerFactory.getLogger(SmsglobalServiceProviderConfigurer.class);
	private static final String SMSGLOBAL_HOST = "smpp.smsglobal.com";
	private static final int SMSGLOBAL_PORT = 1775;

	@ConfigurerFor(targetedBuilder = "standard", priority = DEFAULT_CLOUDHOPPER_CONFIGURER_PRIORITY + 1)
	public static class SmsglobalConfigurer implements MessagingConfigurer {

		@Override
		public void configure(MessagingBuilder msgBuilder) throws ConfigureException {
			checkCanUseCloudhopper();
			checkCanApplySmsGlobalConfiguration(msgBuilder.environment().build());

			CloudhopperBuilder builder = msgBuilder.sms().sender(CloudhopperBuilder.class);
			// @formatter:off
			builder
				.userData()
					// both supported but to benefit from 160 characters messages, we have to use Tlv message_payload because GSM 7-bit is not supported
					.useShortMessage().defaultValue(false).and()
					.useTlvMessagePayload().defaultValue(true).and()
					.and()
				.encoder()
					.gsm7bitPacked().defaultValue(0).and()	// not supported by SmsGlobal
					.latin1().defaultValue(0).and()			// not supported by SmsGlobal
					.gsm8bit().defaultValue(DEFAULT_GSM8_ENCODING_PRIORITY).and()
					.ucs2().defaultValue(DEFAULT_UCS2_ENCODING_PRIORITY).and()
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

		private static void checkCanApplySmsGlobalConfiguration(PropertyResolver propertyResolver) throws ConfigureException {
			Boolean skip = evaluate(asList("${ogham.sms.smsglobal.service-provider.auto-conf.skip}"), propertyResolver, Boolean.class);
			if (skip != null && skip) {
				throw new AutomaticServiceProviderConfigurationSkippedException("SmsGlobal service auto-configuration manually skipped");
			}
			Boolean force = evaluate("${ogham.sms.smsglobal.service-provider.auto-conf.force}", propertyResolver, Boolean.class);
			if (force != null && force) {
				return;
			}
			String host = evaluate(asList("${ogham.sms.cloudhopper.host}", "${ogham.sms.smpp.host}"), propertyResolver, String.class);
			if (!SMSGLOBAL_HOST.equals(host)) {
				throw new AutomaticServiceProviderConfigurationSkippedException("SmsGlobal service auto-configuration skipped because configured SMPP host doesn't target Sms Global");
			}
		}


		private static void checkCanUseCloudhopper() throws ConfigureException {
			if (!isCloudhopperPresent()) {
				throw new MissingImplementationException("Can't send SMS through SmsGlobal because Cloudhopper implementation is not present in the classpath", "com.cloudhopper.smpp.SmppClient");
			}
		}

		private static boolean isCloudhopperPresent() {
			return ClasspathUtils.exists("com.cloudhopper.smpp.SmppClient");
		}
	}

	private SmsglobalServiceProviderConfigurer() {
		super();
	}
}
