package fr.sii.notification.template.exception;

import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.template.resolver.TemplateResolver;

public class NoResolverAdapter extends NotificationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1737698194969085783L;

	private TemplateResolver resolver;
	
	public NoResolverAdapter(String message, TemplateResolver resolver, Throwable cause) {
		super(message, cause);
	}

	public NoResolverAdapter(String message, TemplateResolver resolver) {
		super(message);
	}

	public NoResolverAdapter(TemplateResolver resolver, Throwable cause) {
		super(cause);
	}

	public TemplateResolver getResolver() {
		return resolver;
	}
}
