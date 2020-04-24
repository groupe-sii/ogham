package fr.sii.ogham.core.exception.clean;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;

/**
 * Some resources may be opened while Ogham runs. That's why there is a cleanup
 * mechanism to free/close some resources. Clean-up may fail for any reason.
 * 
 * This is a general exception that wraps the failing cause. It also has
 * subclasses to add additional information.
 * 
 * @author Aurélien Baudet
 *
 */
public class CleanException extends MessagingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public CleanException(String message, Throwable cause) {
		super(message, cause);
	}

	public CleanException(String message) {
		super(message);
	}

	public CleanException(Throwable cause) {
		super(cause);
	}

}
