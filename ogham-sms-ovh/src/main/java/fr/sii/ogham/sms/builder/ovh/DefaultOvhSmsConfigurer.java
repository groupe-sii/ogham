package fr.sii.ogham.sms.builder.ovh;

import static fr.sii.ogham.core.builder.configuration.MayOverride.overrideIfNotSet;
import static fr.sii.ogham.core.builder.configurer.ConfigurationPhase.AFTER_INIT;
import static fr.sii.ogham.sms.OvhSmsConstants.DEFAULT_OVHSMS_CONFIGURER_PRIORITY;

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * encoding (see {@link SmsCoding}): 1 for 7bit encoding, 2 for 16bit encoding
 * (Unicode). If you use Unicode, your SMS will have a maximum size of 70
 * characters instead of 160. If nothing specified, auto-detection is used. Set
 * this property if you want to force {@link SmsCoding} value.</li>
 * <li>It uses "ogham.sms.ovh.tag" to mark sent messages with a 20 maximum
 * character string</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public final class DefaultOvhSmsConfigurer {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultOvhSmsConfigurer.class);

	@ConfigurerFor(targetedBuilder = "standard", priority = DEFAULT_OVHSMS_CONFIGURER_PRIORITY, phase = AFTER_INIT)
	public static class EnvironmentPropagator implements MessagingConfigurer {
		@Override
		public void configure(MessagingBuilder msgBuilder) {
			OvhSmsBuilder builder = msgBuilder.sms().sender(OvhSmsBuilder.class);
			// use same environment as parent builder
			builder.environment(msgBuilder.environment());
		}
	}
	
	@ConfigurerFor(targetedBuilder = "standard", priority = DEFAULT_OVHSMS_CONFIGURER_PRIORITY)
	public static class OvhSmsConfigurer implements MessagingConfigurer {
		@Override
		public void configure(MessagingBuilder msgBuilder) {
			LOG.debug("[{}] apply configuration", this);
			OvhSmsBuilder builder = msgBuilder.sms().sender(OvhSmsBuilder.class);
			// @formatter:off
			builder
				.url().properties("${ogham.sms.ovh.url}").defaultValue(overrideIfNotSet(defaultUrl())).and()
				.account().properties("${ogham.sms.ovh.account}").and()
				.login().properties("${ogham.sms.ovh.login}").and()
				.password().properties("${ogham.sms.ovh.password}").and()
				.options()
					.noStop().properties("${ogham.sms.ovh.no-stop}").defaultValue(overrideIfNotSet(true)).and()
					.smsCoding().properties("${ogham.sms.ovh.sms-coding}").and()
					.tag().properties("${ogham.sms.ovh.tag}");
			// @formatter:on
		}
	
		private static URL defaultUrl() {
			try {
				return new URL("https://www.ovh.com/cgi-bin/sms/http2sms.cgi");
			} catch (MalformedURLException e) {
				// can never be thrown
				return null;
			}
		}
	}
	
	private DefaultOvhSmsConfigurer() {
		super();
	}
}
