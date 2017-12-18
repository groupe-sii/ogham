package fr.sii.ogham.test.classpath.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import fr.sii.ogham.test.classpath.core.FixedDelayRetryStrategy;
import fr.sii.ogham.test.classpath.core.ProjectInitializer;
import fr.sii.ogham.test.classpath.core.RetryProjectInitializer;
import fr.sii.ogham.test.classpath.core.RetryProperties;
import fr.sii.ogham.test.classpath.core.RetryStrategy;
import fr.sii.ogham.test.classpath.core.RetryStrategySupplier;
import fr.sii.ogham.test.classpath.ogham.OghamProperties;
import fr.sii.ogham.test.classpath.springboot.HttpSpringStarterInitializer;
import fr.sii.ogham.test.classpath.springboot.SpringStarterProperties;

@Configuration
public class ProjectInitializerConfig {
	@Bean
	public ProjectInitializer projectInitializer(RestTemplate restTemplate, SpringStarterProperties springStarterProperties, OghamProperties oghamProperties, final RetryProperties retryProperties) {
		return new RetryProjectInitializer(new HttpSpringStarterInitializer(restTemplate, springStarterProperties, oghamProperties), new RetryStrategySupplier() {
			@Override
			public RetryStrategy get() {
				return new FixedDelayRetryStrategy(retryProperties.getMaxAttempts(), retryProperties.getDelay());
			}
		});
	}
}
