package fr.sii.ogham.sms.builder.cloudhopper;

import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.util.ClasspathUtils;

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
 * <li>Configures SMPP portocol:
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
 * <li>Configures encoding:
 * <ul>
 * <li>It uses "ogham.sms.cloudhopper.default-app-charset" property value as
 * default charset if defined. Default charset is UTF-8</li>
 * <li>A conversion from "ogham.sms.cloudhopper.default-app-charset" to
 * "ogham.sms.cloudhopper.smpp-charset" properties if both are defined</li>
 * <li>A conversion from "UTF-8" to "GSM" charset by default</li>
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
@ConfigurerFor(targetedBuilder = "standard", priority = 40000)
public class DefaultCloudhopperConfigurer implements MessagingConfigurer {

	@Override
	public void configure(MessagingBuilder msgBuilder) {
		if (canUseCloudhopper()) {
			CloudhopperBuilder builder = msgBuilder.sms().sender(CloudhopperBuilder.class);
			// use same environment as parent builder
			builder.environment(msgBuilder.environment());
			// @formatter:off
			builder
				.charset()
					.convert("${ogham.sms.cloudhopper.default-app-charset}", "${ogham.sms.cloudhopper.smpp-charset}")
					.convert("UTF-8", NAME_GSM)
					.detector()
						.defaultCharset("${ogham.sms.cloudhopper.default-app-charset}", "UTF-8")
						.and()
					.and()
				.systemId("${ogham.sms.cloudhopper.system-id}", "${ogham.sms.smpp.system-id}")
				.password("${ogham.sms.cloudhopper.password}", "${ogham.sms.smpp.password}")
				.host("${ogham.sms.cloudhopper.host}", "${ogham.sms.smpp.host}")
				.port("${ogham.sms.cloudhopper.port}", "${ogham.sms.smpp.port}")
				.interfaceVersion("${ogham.sms.cloudhopper.interface-version}", "3.4")
				.session()
					.sessionName("${ogham.sms.cloudhopper.session-name}")
					.bindTimeout("${ogham.sms.cloudhopper.bind-timeout}", "5000")
					.connectTimeout("${ogham.sms.cloudhopper.connect-timeout}", "10000")
					.requestExpiryTimeout("${ogham.sms.cloudhopper.request-expiry-timeout}", "-1")
					.windowMonitorInterval("${ogham.sms.cloudhopper.window-monitor-interval}", "-1")
					.windowSize("${ogham.sms.cloudhopper.window-size}", "1")
					.windowWait("${ogham.sms.cloudhopper.window-wait-timeout}", "60000")
					.writeTimeout("${ogham.sms.cloudhopper.write-timeout}", "0")
					.responseTimeout("${ogham.sms.cloudhopper.response-timeout}", "5000")
					.unbindTimeout("${ogham.sms.cloudhopper.unbind-timeout}", "5000")
					.connectRetry()
						.fixedDelay()
							.maxRetries("${ogham.sms.cloudhopper.connect-max-retry}", "5")
							.delay("${ogham.sms.cloudhopper.connect-retry-delay}", "500");
			// @formatter:on
		}
	}

	private boolean canUseCloudhopper() {
		return ClasspathUtils.exists("com.cloudhopper.smpp.SmppClient");
	}

}
