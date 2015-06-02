package fr.sii.notification.core.template.resolver;

import fr.sii.notification.core.exception.template.TemplateResolutionException;
import fr.sii.notification.core.template.SimpleTemplate;
import fr.sii.notification.core.template.Template;

/**
 * Template resolver that just wraps the template string into a {@link Template}
 * .
 * 
 * @author Aurélien Baudet
 *
 */
public class StringTemplateResolver implements TemplateResolver {
	@Override
	public Template getTemplate(String templatePath) throws TemplateResolutionException {
		return new SimpleTemplate(templatePath.getBytes());
	}

}
