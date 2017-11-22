package fr.sii.ogham.template.thymeleaf.common.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.template.exception.TemplateRuntimeException;

@SuppressWarnings({ "squid:MaximumInheritanceDepth" })	// Object, Throwable, Exception and RuntimeException are counted but this is stupid
public class TemplateResolutionException extends TemplateRuntimeException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final transient ResourcePath templatePath;

	public TemplateResolutionException(String message, ResourcePath templatePath, Throwable cause) {
		super(message, cause);
		this.templatePath = templatePath;
	}

	public TemplateResolutionException(String message, ResourcePath templatePath) {
		super(message);
		this.templatePath = templatePath;
	}

	public ResourcePath getTemplatePath() {
		return templatePath;
	}

}
