package fr.sii.ogham.core.exception.template;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.template.context.Context;

public class ParseException extends MessagingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final transient ResourcePath templatePath;
	
	private final transient Context context;

	public ParseException(String message, ResourcePath templatePath, Context context, Throwable cause) {
		super(message, cause);
		this.templatePath = templatePath;
		this.context = context;
	}

	public ParseException(String message, ResourcePath templatePath, Context context) {
		super(message);
		this.templatePath = templatePath;
		this.context = context;
	}

	public ParseException(ResourcePath templatePath, Context context, Throwable cause) {
		super(cause);
		this.templatePath = templatePath;
		this.context = context;
	}

	public ResourcePath getTemplatePath() {
		return templatePath;
	}

	public Context getContext() {
		return context;
	}
}
