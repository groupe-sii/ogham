package fr.sii.ogham.test.classpath.runner.common;

import fr.sii.ogham.test.classpath.core.exception.AdaptativeClasspathException;

public class SingleProjectCreationException extends AdaptativeClasspathException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SingleProjectCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	public SingleProjectCreationException(String message) {
		super(message);
	}

	public SingleProjectCreationException(Throwable cause) {
		super(cause);
	}

}
