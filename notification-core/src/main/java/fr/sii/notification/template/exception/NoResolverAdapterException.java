package fr.sii.notification.template.exception;

import fr.sii.notification.core.resource.resolver.ResourceResolver;

public class NoResolverAdapterException extends ResolverAdapterException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1737698194969085783L;

	public NoResolverAdapterException(String message, ResourceResolver resolver, Throwable cause) {
		super(message, resolver, cause);
	}

	public NoResolverAdapterException(String message, ResourceResolver resolver) {
		super(message, resolver);
	}

	public NoResolverAdapterException(ResourceResolver resolver, Throwable cause) {
		super(resolver, cause);
	}
}
