package fr.sii.ogham.template.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.template.ParseException;
import fr.sii.ogham.core.message.content.Variant;
import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.template.context.Context;

/**
 * When a {@link ResourcePath} references a template, the path may be partial to
 * be able to load a template that has variants. A variant may be a specific
 * format (HTML, text, ...) or could also be for a language or anything else.
 * 
 * This exception is thrown when resolving variant for a template is not found
 * or can't be read/accessed or any other reason.
 * 
 * This exception is general and has subclasses to identify specific reasons.
 * 
 * 
 * @author Aurélien Baudet
 * 
 * @see TemplateVariantNotFoundException
 * @see UnknownVariantException
 */
@SuppressWarnings({ "squid:MaximumInheritanceDepth" }) // Object, Throwable and
														// Exception are counted
														// but this is stupid
public class VariantResolutionException extends ParseException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final transient Variant variant;

	public VariantResolutionException(String message, ResourcePath template, Context context, Variant variant) {
		super(message, template, context);
		this.variant = variant;
	}

	public Variant getVariant() {
		return variant;
	}
}
