package fr.sii.notification.core.exception.resource;



public class NoResolverException extends ResourceResolutionException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2503512294444324909L;

	public NoResolverException(String message, String lookup, Throwable cause) {
		super(message, lookup, cause);
	}

	public NoResolverException(String message, String lookup) {
		super(message, lookup);
	}

	public NoResolverException(String lookup, Throwable cause) {
		super(lookup, cause);
	}
}
