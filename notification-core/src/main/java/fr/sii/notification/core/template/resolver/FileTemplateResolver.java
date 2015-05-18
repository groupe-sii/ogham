package fr.sii.notification.core.template.resolver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import fr.sii.notification.core.exception.template.TemplateResolutionException;
import fr.sii.notification.core.template.SimpleTemplate;
import fr.sii.notification.core.template.Template;

public class FileTemplateResolver implements TemplateResolver {
	@Override
	public Template getTemplate(String templatePath) throws TemplateResolutionException {
		try {
			return new SimpleTemplate(new FileInputStream(templatePath));
		} catch (FileNotFoundException e) {
			throw new TemplateResolutionException("Template "+templatePath+" not found on file system", templatePath, e);
		}
	}

}
