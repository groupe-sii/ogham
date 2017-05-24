package fr.sii.ogham.template.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.template.ParseException;
import fr.sii.ogham.core.message.content.Variant;
import fr.sii.ogham.core.template.context.Context;

public class VariantResolutionException extends ParseException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;
	
	private final transient Variant variant;

	public VariantResolutionException(String message, String templateName, Context context, Variant variant) {
		super(message, templateName, context);
		this.variant = variant;
	}

	public Variant getVariant() {
		return variant;
	}
}
