package fr.sii.notification.core.template.resolver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import fr.sii.notification.core.exception.template.TemplateResolutionException;
import fr.sii.notification.core.template.SimpleTemplate;
import fr.sii.notification.core.template.Template;

/**
 * Template resolver that searches for the template on the file system. The
 * template resolution can handle relative path but it depends on the runtime
 * environment. It is better to provide an absolute path. The generated template
 * information will only contain a reference to the stream of the found
 * resource. If file pointed out by the path doesn't exist, then an
 * {@link TemplateResolutionException} is thrown to indicate that the template
 * couldn't be found.
 * 
 * @author Aurélien Baudet
 *
 */
public class FileTemplateResolver implements TemplateResolver {
	@Override
	public Template getTemplate(String templatePath) throws TemplateResolutionException {
		try {
			return new SimpleTemplate(new FileInputStream(templatePath));
		} catch (FileNotFoundException e) {
			throw new TemplateResolutionException("Template " + templatePath + " not found on file system", templatePath, e);
		}
	}

}
