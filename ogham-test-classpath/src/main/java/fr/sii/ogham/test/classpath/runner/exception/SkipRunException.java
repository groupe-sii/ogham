package fr.sii.ogham.test.classpath.runner.exception;

import fr.sii.ogham.test.classpath.core.exception.AdaptativeClasspathException;

public class SkipRunException extends AdaptativeClasspathException {
    public SkipRunException(String message) {
        super(message);
    }
}
