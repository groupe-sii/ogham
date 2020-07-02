package fr.sii.ogham.test.classpath.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import fr.sii.ogham.test.classpath.core.FixedDelayRetryStrategy;
import fr.sii.ogham.test.classpath.core.ProjectInitializer;
import fr.sii.ogham.test.classpath.core.RetryProjectInitializer;
import fr.sii.ogham.test.classpath.core.RetryProperties;
import fr.sii.ogham.test.classpath.ogham.OghamProperties;
import fr.sii.ogham.test.classpath.runner.common.ParallelProjectsCreator;
import fr.sii.ogham.test.classpath.runner.common.ProjectsCreator;
import fr.sii.ogham.test.classpath.runner.common.RunnerProperties;
import fr.sii.ogham.test.classpath.runner.common.SequentialProjectsCreator;
import fr.sii.ogham.test.classpath.runner.springboot.HttpSpringStarterInitializer;
import fr.sii.ogham.test.classpath.runner.springboot.SpringBootDependency;
import fr.sii.ogham.test.classpath.runner.springboot.SpringBootProjectParams;
import fr.sii.ogham.test.classpath.runner.springboot.SpringBootSingleProjectCreator;
import fr.sii.ogham.test.classpath.runner.springboot.SpringStarterProperties;

@Configuration
public class SpringBootRunnerConfig {
	@Bean
	public ProjectInitializer<SpringBootProjectParams> springStarterProjectInitializer(RestTemplate restTemplate, SpringStarterProperties springStarterProperties, OghamProperties oghamProperties, final RetryProperties retryProperties) {
		return new RetryProjectInitializer<>(new HttpSpringStarterInitializer(restTemplate, springStarterProperties, oghamProperties), () -> new FixedDelayRetryStrategy(retryProperties.getMaxAttempts(), retryProperties.getDelay()));
	}
	
	@Bean
	public ProjectsCreator<SpringBootProjectParams, SpringBootDependency> springBootProjectsCreator(RunnerProperties props, SpringBootSingleProjectCreator projectCreator) {
		if(props.isParallel()) {
			return new ParallelProjectsCreator<>(projectCreator, props.getNumThreads());
		} else {
			return new SequentialProjectsCreator<>(projectCreator);
		}
	}
}
