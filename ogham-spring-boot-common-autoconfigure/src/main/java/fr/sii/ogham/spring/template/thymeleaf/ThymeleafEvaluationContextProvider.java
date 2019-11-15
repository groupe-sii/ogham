package fr.sii.ogham.spring.template.thymeleaf;

import org.springframework.expression.EvaluationContext;

import fr.sii.ogham.core.template.context.Context;

/**
 * Provides evaluation context to be used in SpEL expressions.
 * 
 * This is needed to be able to access Spring context (bean resolution, servlet
 * context, web context, ...).
 * 
 * @author Aurélien Baudet
 *
 */
public interface ThymeleafEvaluationContextProvider {
	/**
	 * Provides an instance of an evaluation context to be used in SpEL
	 * expressions.
	 * 
	 * The Ogham context may be used as source to fill the evaluation context.
	 * 
	 * @param context
	 *            the Ogham context that contains the variables and any useful
	 *            information for template parsing
	 * @return the evaluation context for Spring expressions
	 */
	EvaluationContext getEvaluationContext(Context context);
}
