package fr.sii.ogham.spring.autoconfigure;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.sii.ogham.spring.config.NoTemplateEngineConfigurer;
import fr.sii.ogham.spring.config.SpringMessagingConfigurer;

@Configuration
@ConditionalOnMissingClass({"freemarker.template.Configuration", "org.thymeleaf.spring4.SpringTemplateEngine"})
public class OghamNoTemplateEngineConfiguration {
	@Bean
	public List<SpringMessagingConfigurer> defaultMessagingBuilderConfigurer() {
		return Arrays.<SpringMessagingConfigurer>asList(new NoTemplateEngineConfigurer());
	}
}