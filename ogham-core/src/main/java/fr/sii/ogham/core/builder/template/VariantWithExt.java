package fr.sii.ogham.core.builder.template;

import fr.sii.ogham.core.message.content.Variant;

/**
 * Simple container class that is shared by builders that implement
 * {@link VariantBuilder}.
 * 
 * @author Aurélien Baudet
 *
 */
public class VariantWithExt {
	private final Variant variant;
	private final String extension;

	/**
	 * Creates a pair of variant and extension.
	 * 
	 * @param variant
	 *            the variant
	 * @param extension
	 *            the assocaited extension
	 */
	public VariantWithExt(Variant variant, String extension) {
		super();
		this.variant = variant;
		this.extension = extension;
	}

	/**
	 * Get the variant
	 * 
	 * @return teh variant
	 */
	public Variant getVariant() {
		return variant;
	}

	/**
	 * Get the extension
	 * 
	 * @return the extension
	 */
	public String getExtension() {
		return extension;
	}
}