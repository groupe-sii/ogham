package fr.sii.ogham.template.exception;

import fr.sii.ogham.core.exception.template.ParseException;
import fr.sii.ogham.core.message.content.Variant;
import fr.sii.ogham.core.template.context.Context;

public class VariantResolutionException extends ParseException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6337902498224234453L;
	
	private final Variant variant;

	public VariantResolutionException(String message, String templateName, Context context, Variant variant) {
		super(message, templateName, context);
		this.variant = variant;
	}

	public Variant getVariant() {
		return variant;
	}
}
