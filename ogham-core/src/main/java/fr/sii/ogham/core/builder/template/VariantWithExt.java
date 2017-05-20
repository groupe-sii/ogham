package fr.sii.ogham.core.builder.template;

import fr.sii.ogham.core.message.content.Variant;

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