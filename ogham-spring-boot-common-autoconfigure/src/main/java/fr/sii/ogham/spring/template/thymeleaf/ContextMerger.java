package fr.sii.ogham.spring.template.thymeleaf;

import java.util.Map;

import org.thymeleaf.context.IContext;

/**
 * Add additional variables to an existing context.
 * 
 * @author Aurélien Baudet
 */
public interface ContextMerger {
	/**
	 * Adds additional variables to base context.
	 * 
	 * @param base
	 *            the context that contains the original variables and Thymeleaf
	 *            context data. It may be updated in place.
	 * @param variables
	 *            the additional variables to apply to the original context
	 * @return the resulting context (can be the same instance as base)
	 */
	IContext mergeVariables(IContext base, Map<String, Object> variables);
}
