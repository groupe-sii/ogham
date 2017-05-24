package fr.sii.ogham.template.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.message.content.Variant;
import fr.sii.ogham.core.template.context.Context;

public class UnknownVariantException extends VariantResolutionException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;
	
	public UnknownVariantException(String message, String templateName, Context context, Variant variant) {
		super(message, templateName, context, variant);
	}
}
