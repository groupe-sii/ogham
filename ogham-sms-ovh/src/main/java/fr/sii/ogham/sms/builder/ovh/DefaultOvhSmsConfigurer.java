package fr.sii.ogham.sms.builder.ovh;

import static fr.sii.ogham.sms.OvhSmsConstants.DEFAULT_OVHSMS_CONFIGURER_PRIORITY;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.sms.sender.impl.ovh.SmsCoding;

/**
 * Default configurer that configures sending of SMS through OVH HTTP API .The
 * configurer is automatically applied every time a {@link MessagingBuilder}
 * instance is created through {@link MessagingBuilder#standard()}.
 * 
 * <p>
 * The configurer has a priority of 20000 in order to be applied after
 * templating configurers, email configurers and SMPP configurer.
 * </p>
 * 
 * This configurer is always applied but sender is only used if OVH URL,
 * account, username and password are defined.
 * 
 * <p>
 * This configurer inherits environment configuration (see
 * {@link EnvironmentBuilder} and
 * {@link OvhSmsBuilder#environment(EnvironmentBuilder)}).
 * </p>
 * 
 * <p>
 * This configurer applies the following configuration:
 * <ul>
 * <li>Configures OVH URL:
 * <ul>
 * <li>It uses the property "ogham.sms.ovh.url" if defined. By default URL is
 * "https://www.ovh.com/cgi-bin/sms/http2sms.cgi"</li>
 * </ul>
 * </li>
 * <li>Configures authentication:
 * <ul>
 * <li>It uses properties "ogham.sms.ovh.account", "ogham.sms.ovh.login" and
 * "ogham.sms.ovh.password" (these properties are mandatory to be able to send
 * SMS through OVH)</li>
 * </ul>
 * </li>
 * <li>Configures extra options:
 * <ul>
 * <li>It uses "ogham.sms.ovh.no-stop" property value to enable/disable "STOP"
 * indication at the end of the message (useful to disable for non-commercial
 * SMS). Default to true (disabled)</li>
 * <li>It uses "ogham.sms.ovh.sms-coding" property value to define message
 * encoding (see {@link SmsCoding}): 1 for 7bit encoding, 2 for 8bit encoding
 * (UTF-8). If you use UTF-8, your SMS will have a maximum size of 70 characters
 * instead of 160</li>
 * <li>It uses "ogham.sms.ovh.tag" to mark sent messages with a 20 maximum
 * character string</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
@ConfigurerFor(targetedBuilder = "standard", priority = DEFAULT_OVHSMS_CONFIGURER_PRIORITY)
public class DefaultOvhSmsConfigurer implements MessagingConfigurer {

	@Override
	public void configure(MessagingBuilder msgBuilder) {
		OvhSmsBuilder builder = msgBuilder.sms().sender(OvhSmsBuilder.class);
		// use same environment as parent builder
		builder.environment(msgBuilder.environment());
		// @formatter:off
		builder
			.url("${ogham.sms.ovh.url}", "https://www.ovh.com/cgi-bin/sms/http2sms.cgi")
			.account("${ogham.sms.ovh.account}")
			.login("${ogham.sms.ovh.login}")
			.password("${ogham.sms.ovh.password}")
			.options()
				.noStop("${ogham.sms.ovh.no-stop}", "true")
				.smsCoding("${ogham.sms.ovh.sms-coding}")
				.tag("${ogham.sms.ovh.tag}");
		// @formatter:on
	}

}
