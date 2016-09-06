package fr.sii.ogham.template.exception;

/**
 * 
 * @author Cyril Dejonghe
 *
 */
public class ResolverAdapterNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ResolverAdapterNotFoundException() {
		super();
	}

	public ResolverAdapterNotFoundException(String message) {
		super(message);
	}

	public ResolverAdapterNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
