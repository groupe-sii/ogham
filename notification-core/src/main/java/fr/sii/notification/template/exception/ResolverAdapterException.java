package fr.sii.notification.template.exception;

import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.template.resolver.TemplateResolver;

public class ResolverAdapterException extends NotificationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1737698194969085783L;

	private TemplateResolver resolver;
	
	public ResolverAdapterException(String message, TemplateResolver resolver, Throwable cause) {
		super(message, cause);
		this.resolver = resolver;
	}

	public ResolverAdapterException(String message, TemplateResolver resolver) {
		super(message);
		this.resolver = resolver;
	}

	public ResolverAdapterException(TemplateResolver resolver, Throwable cause) {
		super(cause);
		this.resolver = resolver;
	}

	public TemplateResolver getResolver() {
		return resolver;
	}
}
