package fr.sii.ogham.core.exception.configurer;

import java.util.Arrays;
import java.util.List;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

public class MissingImplementationException extends ConfigureException {
    private static final long serialVersionUID = SERIAL_VERSION_UID;

    private final List<String> requiredClasses;

    public MissingImplementationException(String message, String... requiredClasses) {
        this(message, Arrays.asList(requiredClasses));
    }

    public MissingImplementationException(String message, List<String> requiredClasses) {
        super(message);
        this.requiredClasses = requiredClasses;
    }
}
