package fr.sii.ogham.spring.sms;

import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_CLOUDHOPPER_CONFIGURER_PRIORITY;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.sii.ogham.sms.builder.smsglobal.SmsglobalServiceProviderConfigurer;
import fr.sii.ogham.sms.builder.smsglobal.SmsglobalServiceProviderConfigurer.SmsglobalConfigurer;
import fr.sii.ogham.sms.sender.impl.CloudhopperSMPPSender;
import fr.sii.ogham.spring.common.StandaloneWrapperConfigurer;

@Configuration
@ConditionalOnClass({ CloudhopperSMPPSender.class, SmsglobalServiceProviderConfigurer.class })
@ConditionalOnProperty(name = { "ogham.sms.smpp.host", "ogham.sms.cloudhopper.host" }, havingValue = "smsglobal.com")
public class OghamSmsglobalServiceProviderConfiguration {
	@Bean
	@ConditionalOnMissingBean(StandaloneWrapperConfigurer.class)
	public StandaloneWrapperConfigurer springSmsglobalServiceProviderConfigurer() {
		return new StandaloneWrapperConfigurer(new SmsglobalConfigurer(), DEFAULT_CLOUDHOPPER_CONFIGURER_PRIORITY + 1001);
	}
}
