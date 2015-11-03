package fr.sii.ogham.sms.builder.cloudhopper;

import com.cloudhopper.commons.charset.CharsetUtil;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.util.ClasspathUtils;

@ConfigurerFor(targetedBuilder="standard", priority=800)
public class DefaultCloudhopperConfigurer implements MessagingConfigurer {

	@Override
	public void configure(MessagingBuilder msgBuilder) {
		if(canUseCloudhopper()) {
			CloudhopperBuilder builder = msgBuilder.sms().sender(CloudhopperBuilder.class);
			// use same environment as parent builder
			builder.environment(msgBuilder.environment());
			// @formatter:off
			builder
				.charset()
					// TODO: externalize in conf?
					.convert("UTF-8", CharsetUtil.NAME_GSM)
					.defaultCharset("${ogham.sms.cloudhopper.default-encoding}", "${ogham.sms.default-encoding}", "UTF-8")
					.and()
				.systemId("${ogham.sms.cloudhopper.system-id}", "${ogham.sms.smpp.system-id}")
				.password("${ogham.sms.cloudhopper.password}", "${ogham.sms.smpp.password}")
				.host("${ogham.sms.cloudhopper.host}", "${ogham.sms.smpp.host}")
				.port("${ogham.sms.cloudhopper.port}", "${ogham.sms.smpp.port}")
				.interfaceVersion("${ogham.sms.cloudhopper.interface-version}", "${ogham.sms.smpp.interface-version}", "3.4")
				.sessionName("${ogham.sms.cloudhopper.session-name}")
				.session()
					.bindTimeout("${ogham.sms.cloudhopper.bind-timeout}", "${ogham.sms.smpp.timeout.bind-timeout}", "5000")
					.connectTimeout("${ogham.sms.cloudhopper.connect-timeout}", "${ogham.sms.smpp.connect-timeout}", "10000")
					.requestExpiryTimeout("${ogham.sms.cloudhopper.request-expiry-timeout}", "${ogham.sms.smpp.request-expiry-timeout}", "-1")
					.windowMonitorInterval("${ogham.sms.cloudhopper.window-monitor-interval}", "${ogham.sms.smpp.window-monitor-interval}", "-1")
					.windowSize("${ogham.sms.cloudhopper.window-size}", "${ogham.sms.smpp.window-size}", "1")
					.windowWait("${ogham.sms.cloudhopper.window-wait-timeout}", "${ogham.sms.smpp.window-wait-timeout}", "60000")
					.writeTimeout("${ogham.sms.cloudhopper.write-timeout}", "${ogham.sms.smpp.write-timeout}", "0")
					.responseTimeout("${ogham.sms.cloudhopper.response-timeout}", "${ogham.sms.smpp.response-timeout}", "5000")
					.unbindTimeout("${ogham.sms.cloudhopper.unbind-timeout}", "${ogham.sms.smpp.unbind-timeout}", "5000")
					.connectRetry()
						.maxRetries("${ogham.sms.cloudhopper.connect-max-retry}", "${ogham.sms.smpp.connect-max-retry}", "10")
						.delay("${ogham.sms.cloudhopper.connect-retry-delay}", "${ogham.sms.smpp.connect-retry-delay}", "500");
			// @formatter:on
		}
	}

	private boolean canUseCloudhopper() {
		return ClasspathUtils.exists("com.cloudhopper.smpp.SmppClient");
	}

}
