package fr.sii.ogham.core.exception.clean;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import java.io.Closeable;
import java.io.IOException;

/**
 * Some resources may be opened while Ogham runs. That's why there is a cleanup
 * mechanism to free/close some resources. Automatic cleanup is triggered if
 * Ogham is created in a try-with-resource block (see {@link Closeable}).
 * Clean-up may fail for any reason.
 * 
 * Specialization of the general {@link IOException} that indicates that the
 * error happened while {@link Closeable#close()} has been called.
 * 
 * @author Aurélien Baudet
 *
 * @see Closeable
 */
public class CloseException extends IOException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public CloseException(String message, CleanException cause) {
		super(message, cause);
	}

}
