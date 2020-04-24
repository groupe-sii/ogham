package fr.sii.ogham.template.thymeleaf.common.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.template.exception.TemplateRuntimeException;

/**
 * Specialized exception that is thrown by template engine integrations to
 * indicate that a template couldn't be resolved.
 * 
 * @author Aurélien Baudet
 *
 */
@SuppressWarnings({ "squid:MaximumInheritanceDepth" }) // Object, Throwable,
														// Exception and
														// RuntimeException are
														// counted but this is
														// stupid
public class TemplateResolutionException extends TemplateRuntimeException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final String templateName;
	private final transient ResourcePath templatePath;

	public TemplateResolutionException(String message, String templateName, ResourcePath templatePath, Throwable cause) {
		super(message, cause);
		this.templateName = templateName;
		this.templatePath = templatePath;
	}

	public TemplateResolutionException(String message, String templateName, ResourcePath templatePath) {
		super(message);
		this.templateName = templateName;
		this.templatePath = templatePath;
	}

	public ResourcePath getTemplatePath() {
		return templatePath;
	}

	public String getTemplateName() {
		return templateName;
	}

}
