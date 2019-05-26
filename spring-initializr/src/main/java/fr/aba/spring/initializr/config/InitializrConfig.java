package fr.aba.spring.initializr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataBuilder;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.metadata.InitializrProperties;
import io.spring.initializr.metadata.SimpleInitializrMetadataProvider;

@Configuration
public class InitializrConfig {
	@Bean
	public InitializrMetadataProvider initializrMetadataProvider(
	        InitializrProperties properties) {
	    InitializrMetadata metadata = InitializrMetadataBuilder
	            .fromInitializrProperties(properties).build();
	    return new SimpleInitializrMetadataProvider(metadata);
	}
}
