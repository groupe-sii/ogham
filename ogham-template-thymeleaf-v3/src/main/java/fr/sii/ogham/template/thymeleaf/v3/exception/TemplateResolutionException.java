package fr.sii.ogham.template.thymeleaf.v3.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.template.exception.TemplateRuntimeException;

@SuppressWarnings({ "squid:MaximumInheritanceDepth" })	// Object, Throwable, Exception and RuntimeException are counted but this is stupid
public class TemplateResolutionException extends TemplateRuntimeException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final String templateName;
	private final transient ResourcePath templatePath;

	public TemplateResolutionException(String message, String templateName, ResourcePath templatePath, Throwable cause) {
		super(message, cause);
		this.templatePath = templatePath;
		this.templateName = templateName;
	}

	public TemplateResolutionException(String message, String templateName, ResourcePath templatePath) {
		super(message);
		this.templatePath = templatePath;
		this.templateName = templateName;
	}

	public ResourcePath getTemplatePath() {
		return templatePath;
	}

	public String getTemplateName() {
		return templateName;
	}

}
