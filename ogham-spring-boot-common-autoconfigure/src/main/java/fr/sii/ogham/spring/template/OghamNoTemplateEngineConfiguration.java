package fr.sii.ogham.spring.template;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.sii.ogham.spring.common.SpringMessagingConfigurer;

@Configuration
@ConditionalOnMissingClass({"freemarker.template.Configuration", "org.thymeleaf.spring4.SpringTemplateEngine", "org.thymeleaf.spring5.SpringTemplateEngine"})
public class OghamNoTemplateEngineConfiguration {
	@Bean
	public List<SpringMessagingConfigurer> defaultMessagingBuilderConfigurer() {
		return Arrays.<SpringMessagingConfigurer>asList(new NoTemplateEngineConfigurer());
	}
}