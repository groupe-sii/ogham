package fr.sii.ogham.sample.standard.template.thymeleaf;

import org.thymeleaf.dialect.IDialect;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.template.thymeleaf.buider.ThymeleafEmailBuilder;
import fr.sii.ogham.template.thymeleaf.buider.ThymeleafSmsBuilder;

public class AddDialect {
	public static void main(String[] args) {
		IDialect dialect = null;
		createService(dialect);
	}

	private static MessagingService createService(IDialect dialect) {
		// init the builder with default values
		MessagingBuilder builder = MessagingBuilder.standard();
		// register the dialect for both email and sms
		builder.email()
				.template(ThymeleafEmailBuilder.class)
					.engine()
						.addDialect(dialect);
		builder.sms()
				.template(ThymeleafSmsBuilder.class)
					.engine()
						.addDialect(dialect);
		// instantiate the service
		return builder.build();
	}
}
