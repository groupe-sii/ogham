package fr.sii.ogham.spring.sms;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OghamOvhSmsProperties.class)
public class OghamOvhSmsConfiguration {

}
