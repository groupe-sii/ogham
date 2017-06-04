package fr.sii.ogham.spring.sms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.sii.ogham.sms.sender.impl.CloudhopperSMPPSender;

@Configuration
@ConditionalOnClass({CloudhopperSMPPSender.class})
@EnableConfigurationProperties({OghamCloudhopperProperties.class, OghamSmppProperties.class})
public class OghamCloudhopperConfiguration {
	@Autowired(required=false) OghamSmppProperties smppProperties;
	@Autowired(required=false) OghamCloudhopperProperties cloudhopperProperties;

	@Bean
	@ConditionalOnMissingBean(SpringCloudhopperConfigurer.class)
	public SpringCloudhopperConfigurer springCloudhopperConfigurer() {
		return new SpringCloudhopperConfigurer(smppProperties, cloudhopperProperties);
	}
}
