package fr.sii.notification.core.template.resolver;

import java.io.InputStream;

import fr.sii.notification.core.exception.template.TemplateResolutionException;
import fr.sii.notification.core.template.SimpleTemplate;
import fr.sii.notification.core.template.Template;

public class ClassPathTemplateResolver implements TemplateResolver {
	@Override
	public Template getTemplate(String templatePath) throws TemplateResolutionException {
		InputStream stream = getClass().getClassLoader().getResourceAsStream(templatePath.startsWith("/") ? templatePath.substring(1) : templatePath);
		if(stream==null) {
			throw new TemplateResolutionException("Template "+templatePath+" not found in the classpath", templatePath);
		}
		return new SimpleTemplate(stream);
	}

}
