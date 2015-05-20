package fr.sii.notification.core.exception.template;


public class NoTemplateResolverException extends TemplateResolutionException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2503512294444324909L;

	public NoTemplateResolverException(String message, String lookup, Throwable cause) {
		super(message, lookup, cause);
	}

	public NoTemplateResolverException(String message, String lookup) {
		super(message, lookup);
	}

	public NoTemplateResolverException(String lookup, Throwable cause) {
		super(lookup, cause);
	}
}
