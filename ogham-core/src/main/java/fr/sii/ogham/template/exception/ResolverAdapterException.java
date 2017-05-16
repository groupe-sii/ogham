package fr.sii.ogham.template.exception;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;

public class ResolverAdapterException extends MessagingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1737698194969085783L;

	private final transient ResourceResolver resolver;
	
	public ResolverAdapterException(String message, ResourceResolver resolver, Throwable cause) {
		super(message, cause);
		this.resolver = resolver;
	}

	public ResolverAdapterException(String message, ResourceResolver resolver) {
		super(message);
		this.resolver = resolver;
	}

	public ResolverAdapterException(ResourceResolver resolver, Throwable cause) {
		super(cause);
		this.resolver = resolver;
	}

	public ResourceResolver getResolver() {
		return resolver;
	}
}
