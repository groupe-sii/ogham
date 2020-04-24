package fr.sii.ogham.core.exception.clean;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.clean.Cleanable;

/**
 * Specialized exception that keeps a reference to the {@link Cleanable}
 * instance which {@link Cleanable#clean()} method has been called and failed.
 * 
 * This may be useful to manually trying to cleanup again.
 * 
 * @author Aurélien Baudet
 *
 */
public class CleanableException extends CleanException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final transient Cleanable cleanable;

	public CleanableException(String message, Throwable cause, Cleanable cleanable) {
		super(message, cause);
		this.cleanable = cleanable;
	}

	public CleanableException(String message, Cleanable cleanable) {
		super(message);
		this.cleanable = cleanable;
	}

	public CleanableException(Throwable cause, Cleanable cleanable) {
		super(cause);
		this.cleanable = cleanable;
	}

	public Cleanable getCleanable() {
		return cleanable;
	}

}
