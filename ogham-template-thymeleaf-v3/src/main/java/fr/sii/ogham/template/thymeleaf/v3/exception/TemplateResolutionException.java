package fr.sii.ogham.template.thymeleaf.v3.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.resource.ResourcePath;
import fr.sii.ogham.template.exception.TemplateRuntimeException;

public class TemplateResolutionException extends TemplateRuntimeException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final String templateName;
	private final transient ResourcePath resolvedPath;

	public TemplateResolutionException(String message, Throwable cause, String templateName, ResourcePath resolvedPath) {
		super(message, cause);
		this.templateName = templateName;
		this.resolvedPath = resolvedPath;
	}

	public TemplateResolutionException(String message, String templateName, ResourcePath resolvedPath) {
		super(message);
		this.templateName = templateName;
		this.resolvedPath = resolvedPath;
	}

	public String getTemplateName() {
		return templateName;
	}

	public ResourcePath getResolvedPath() {
		return resolvedPath;
	}

}
