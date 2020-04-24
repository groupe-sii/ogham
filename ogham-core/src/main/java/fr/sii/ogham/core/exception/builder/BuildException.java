package fr.sii.ogham.core.exception.builder;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingRuntimeException;
import fr.sii.ogham.core.service.MessagingService;

/**
 * To configure and provide a fully usable {@link MessagingService}, Ogham
 * provides a {@link MessagingBuilder}. It helps the developer to configure
 * Ogham.
 * 
 * This exception is thrown when trying to instantiate the
 * {@link MessagingService} (by calling {@link MessagingBuilder#build()}) for
 * any reason.
 * 
 * @author Aurélien Baudet
 *
 */
public class BuildException extends MessagingRuntimeException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public BuildException(String message, Throwable cause) {
		super(message, cause);
	}

	public BuildException(String message) {
		super(message);
	}

	public BuildException(Throwable cause) {
		super(cause);
	}
}
