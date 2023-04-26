package fr.sii.ogham.core.exception.configurer;

import fr.sii.ogham.core.exception.MessagingException;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

public class ConfigureException extends MessagingException {
    private static final long serialVersionUID = SERIAL_VERSION_UID;

    public ConfigureException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigureException(String message) {
        super(message);
    }

    public ConfigureException(Throwable cause) {
        super(cause);
    }
}
