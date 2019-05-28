package fr.sii.standalone.runtime.testing;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.service.MessagingService;

public class StandaloneApp {
	public MessagingService load() {
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = MessagingBuilder.standard()
				.environment()
					.and()
				.build();
		return service;
	}
}
