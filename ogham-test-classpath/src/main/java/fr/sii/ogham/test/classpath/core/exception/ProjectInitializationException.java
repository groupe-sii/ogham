package fr.sii.ogham.test.classpath.core.exception;

public class ProjectInitializationException extends AdaptativeClasspathException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProjectInitializationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProjectInitializationException(String message) {
		super(message);
	}

	public ProjectInitializationException(Throwable cause) {
		super(cause);
	}

}
