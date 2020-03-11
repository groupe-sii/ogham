package fr.sii.ogham.core.exception.clean;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.clean.Cleanable;

public class CleanableException extends CleanException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final Cleanable cleanable;

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
