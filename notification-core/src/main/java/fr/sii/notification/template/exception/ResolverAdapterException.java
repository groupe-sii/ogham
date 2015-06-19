package fr.sii.notification.template.exception;

import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.resource.resolver.ResourceResolver;

public class ResolverAdapterException extends NotificationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1737698194969085783L;

	private ResourceResolver resolver;
	
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
