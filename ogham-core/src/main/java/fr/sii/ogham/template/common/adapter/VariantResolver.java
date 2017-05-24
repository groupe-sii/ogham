package fr.sii.ogham.template.common.adapter;

import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.template.exception.VariantResolutionException;

/**
 * Converts the general variant concept into the real path to the template file.
 * 
 * @author Aurélien Baudet
 *
 */
public interface VariantResolver {
	/**
	 * Get the real path to the template path
	 * 
	 * @param template
	 *            the template name or path
	 * @return the resolved path or null if variant is unknown
	 * @throws VariantResolutionException
	 *             when template has variant but the variant is not known
	 */
	String getRealPath(TemplateContent template) throws VariantResolutionException;

	/**
	 * Check if the variant exists
	 * 
	 * @param template
	 *            the template name or path
	 * @return true if the resolved path exists
	 */
	boolean variantExists(TemplateContent template);
}
