package fr.sii.ogham.email.exception.attachment.translator;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.exception.resource.ResourceResolutionException;

/**
 * Ogham uses a chain to transform original resource into final resource. For
 * example, the original resource may be a relative resource. Therefore, is an
 * element in the chain that may resolve the final resource relative to another
 * one.
 * 
 * This exception wraps a {@link ResourceResolutionException}.
 * 
 * @author Aurélien Baudet
 *
 */
public class ResourceTranslatorException extends MessagingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public ResourceTranslatorException(String message, Throwable cause) {
		super(message, cause);
	}

}
