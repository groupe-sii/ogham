package fr.sii.notification.core.exception.template;

import fr.sii.notification.core.exception.NotificationException;

public class TemplateResolutionException extends NotificationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2503512294444324909L;

	private String lookup;
	
	public TemplateResolutionException(String message, String lookup, Throwable cause) {
		super(message, cause);
	}

	public TemplateResolutionException(String message, String lookup) {
		super(message);
	}

	public TemplateResolutionException(String lookup, Throwable cause) {
		super(cause);
	}

	public String getLookup() {
		return lookup;
	}
}
