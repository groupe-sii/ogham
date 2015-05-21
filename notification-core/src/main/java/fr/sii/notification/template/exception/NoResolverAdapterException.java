package fr.sii.notification.template.exception;

import fr.sii.notification.core.template.resolver.TemplateResolver;

public class NoResolverAdapterException extends ResolverAdapterException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1737698194969085783L;

	public NoResolverAdapterException(String message, TemplateResolver resolver, Throwable cause) {
		super(message, resolver, cause);
	}

	public NoResolverAdapterException(String message, TemplateResolver resolver) {
		super(message, resolver);
	}

	public NoResolverAdapterException(TemplateResolver resolver, Throwable cause) {
		super(resolver, cause);
	}
}
