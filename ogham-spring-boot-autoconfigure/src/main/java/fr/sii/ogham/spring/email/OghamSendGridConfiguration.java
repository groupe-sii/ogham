package fr.sii.ogham.spring.email;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OghamSendGridProperties.class)
public class OghamSendGridConfiguration {

}
