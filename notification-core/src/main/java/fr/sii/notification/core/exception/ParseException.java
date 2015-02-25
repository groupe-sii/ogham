package fr.sii.notification.core.exception;

import fr.sii.notification.core.template.context.Context;

public class ParseException extends NotificationException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6431070357840561026L;

	private String templateName;
	
	private Context context;

	public ParseException(String message, String templateName, Context context, Throwable cause) {
		super(message, cause);
		this.templateName = templateName;
		this.context = context;
	}

	public ParseException(String message, String templateName, Context context) {
		super(message);
		this.templateName = templateName;
		this.context = context;
	}

	public ParseException(String templateName, Context context, Throwable cause) {
		super(cause);
		this.templateName = templateName;
		this.context = context;
	}

	public String getTemplateName() {
		return templateName;
	}

	public Context getContext() {
		return context;
	}
}
