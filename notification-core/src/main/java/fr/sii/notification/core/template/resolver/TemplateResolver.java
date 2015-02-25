package fr.sii.notification.core.template.resolver;

import fr.sii.notification.core.exception.TemplateResolutionException;
import fr.sii.notification.core.template.Template;

public interface TemplateResolver {
	public Template getTemplate(String templateName) throws TemplateResolutionException;
}
