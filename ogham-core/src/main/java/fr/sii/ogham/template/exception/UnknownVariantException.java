package fr.sii.ogham.template.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.message.content.Variant;
import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.template.context.Context;
import fr.sii.ogham.template.common.adapter.VariantResolver;

/**
 * Specialized exception that indicates that the message references a variant
 * that is not known by Ogham. It can happen in several cases:
 * <ul>
 * <li>Ogham is misconfigured so the {@link VariantResolver} associated to the
 * variant is not registered</li>
 * <li>An Ogham module is not present in the classpath so the
 * {@link VariantResolver} associated to the variant is not loaded</li>
 * <li>The variant and associated {@link VariantResolver} are defined in an
 * Ogham extension (not provided by Ogham) and Ogham is not aware (not
 * configured for example) of that {@link VariantResolver}</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
@SuppressWarnings({ "squid:MaximumInheritanceDepth" }) // Object, Throwable and
														// Exception are counted
														// but this is stupid
public class UnknownVariantException extends VariantResolutionException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public UnknownVariantException(String message, ResourcePath template, Context context, Variant variant) {
		super(message, template, context, variant);
	}
}
