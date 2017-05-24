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

	public VariantWithExt(Variant variant, String extension) {
		super();
		this.variant = variant;
		this.extension = extension;
	}

	public Variant getVariant() {
		return variant;
	}

	public String getExtension() {
		return extension;
	}
}