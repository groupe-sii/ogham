package fr.sii.ogham.test.classpath.core.exception;

public class PackagedAppNameException extends AdaptativeClasspathException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PackagedAppNameException(String message, Throwable cause) {
		super(message, cause);
	}

	public PackagedAppNameException(String message) {
		super(message);
	}

	public PackagedAppNameException(Throwable cause) {
		super(cause);
	}

}
