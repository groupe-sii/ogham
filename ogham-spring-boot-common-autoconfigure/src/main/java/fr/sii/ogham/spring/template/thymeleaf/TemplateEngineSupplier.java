package fr.sii.ogham.spring.template.thymeleaf;

import org.thymeleaf.TemplateEngine;

/**
 * Provides an instance of a {@link TemplateEngine} to be used in Spring
 * context.
 * 
 * @author Aurélien Baudet
 *
 */
public interface TemplateEngineSupplier {
	/**
	 * Create or retrieve an instance of a {@link TemplateEngine}.
	 * 
	 * @return the template engine instance
	 */
	TemplateEngine get();
}
