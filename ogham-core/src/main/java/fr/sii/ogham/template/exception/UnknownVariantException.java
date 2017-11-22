package fr.sii.ogham.template.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.message.content.Variant;
import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.template.context.Context;

@SuppressWarnings({ "squid:MaximumInheritanceDepth" })	// Object, Throwable and Exception are counted but this is stupid
public class UnknownVariantException extends VariantResolutionException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;
	
	public UnknownVariantException(String message, ResourcePath template, Context context, Variant variant) {
		super(message, template, context, variant);
	}
}
