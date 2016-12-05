package fr.sii.ogham.core.message.capability;

import fr.sii.ogham.core.message.content.Variant;

/**
 * Interface to mark a template that has variants.
 * 
 * @author Aurélien Baudet
 * 
 */
public interface HasVariant {
	/**
	 * Get the variant
	 * 
	 * @return the variant
	 */
	Variant getVariant();
}
