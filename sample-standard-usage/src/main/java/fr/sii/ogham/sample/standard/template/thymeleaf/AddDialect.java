package fr.sii.ogham.sample.standard.template.thymeleaf;

import org.thymeleaf.dialect.IDialect;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.service.MessagingService;

public class AddDialect {
	public static void main(String[] args) {
		IDialect dialect = null;
		createService(dialect);
	}

	private static MessagingService createService(IDialect dialect) {
		// init the builder with default values
		MessagingBuilder builder = new MessagingBuilder().useAllDefaults();
		// register the dialect
		builder.getEmailBuilder().getTemplateBuilder().getThymeleafParser().getEngine().addDialect(dialect);
		builder.getSmsBuilder().getTemplateBuilder().getThymeleafParser().getEngine().addDialect(dialect);
		// instantiate the service
		return builder.build();
	}
}
