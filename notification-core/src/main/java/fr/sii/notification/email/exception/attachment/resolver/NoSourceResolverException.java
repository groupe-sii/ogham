package fr.sii.notification.email.exception.attachment.resolver;


public class NoSourceResolverException extends SourceResolutionException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2503512294444324909L;

	public NoSourceResolverException(String message, String path, Throwable cause) {
		super(message, path, cause);
	}

	public NoSourceResolverException(String message, String path) {
		super(message, path);
	}

	public NoSourceResolverException(String path, Throwable cause) {
		super(path, cause);
	}
}
