package fr.sii.ogham.core.exception.mimetype;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

public class MimeTypeParseException extends MimeTypeException {
    private static final long serialVersionUID = SERIAL_VERSION_UID;

    private final String mimetype;

    public MimeTypeParseException(String message, Throwable cause, String mimetype) {
        super(message, cause);
        this.mimetype = mimetype;
    }

    public MimeTypeParseException(String message, String mimetype) {
        super(message);
        this.mimetype = mimetype;
    }

    public MimeTypeParseException(Throwable cause, String mimetype) {
        super(cause);
        this.mimetype = mimetype;
    }
}
