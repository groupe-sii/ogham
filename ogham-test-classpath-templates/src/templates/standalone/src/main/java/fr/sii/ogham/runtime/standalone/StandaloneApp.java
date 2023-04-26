package fr.sii.ogham.runtime.standalone;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.service.MessagingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class StandaloneApp {
	private static final Logger LOG = LoggerFactory.getLogger(StandaloneApp.class);
	
	public MessagingService init() {
		return init(new Properties());
	}
	
	public MessagingService init(Properties properties) {
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = MessagingBuilder.standard()
				.environment()
					.properties(properties)
					.and()
				.build();
		return service;
	}
}
