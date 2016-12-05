package fr.sii.ogham.template.exception;

import fr.sii.ogham.core.message.content.Variant;
import fr.sii.ogham.core.template.context.Context;

public class UnknownVariantException extends VariantResolutionException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6337902498224234453L;
	
	public UnknownVariantException(String message, String templateName, Context context, Variant variant) {
		super(message, templateName, context, variant);
	}
}
