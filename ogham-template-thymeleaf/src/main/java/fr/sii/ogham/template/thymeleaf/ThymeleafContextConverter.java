package fr.sii.ogham.template.thymeleaf;

import fr.sii.ogham.core.exception.template.ContextException;
import fr.sii.ogham.core.template.context.Context;

/**
 * Convert a {@link Context} abstraction used for all template engines into a
 * {@link org.thymeleaf.context.Context} specific to Thymeleaf.
 * 
 * @author Aurélien Baudet
 *
 */
public interface ThymeleafContextConverter {

	/**
	 * Convert abstraction used for all template engines into a Thymeleaf
	 * context.
	 * 
	 * @param context
	 *            the context abstraction
	 * @return the Thymeleaf context
	 * @throws ContextException
	 *             when conversion couldn't be applied
	 */
	public abstract org.thymeleaf.context.Context convert(Context context) throws ContextException;

}