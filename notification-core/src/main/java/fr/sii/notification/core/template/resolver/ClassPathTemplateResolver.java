package fr.sii.notification.core.template.resolver;

import java.io.InputStream;

import fr.sii.notification.core.exception.template.TemplateResolutionException;
import fr.sii.notification.core.template.SimpleTemplate;
import fr.sii.notification.core.template.Template;

/**
 * Template resolver that searches for the template into the classpath. This
 * implementation is able to manage path starting or not with '/'. The template
 * resolution needs an absolute class path. The generated template information
 * will only contain a reference to the stream of the found resource. If the
 * path points nowhere, an {@link TemplateResolutionException} is thrown to
 * indicate that the template couldn't be found.
 * 
 * @author Aurélien Baudet
 * @see SimpleTemplate
 */
public class ClassPathTemplateResolver implements TemplateResolver {
	@Override
	public Template getTemplate(String templatePath) throws TemplateResolutionException {
		InputStream stream = getClass().getClassLoader().getResourceAsStream(templatePath.startsWith("/") ? templatePath.substring(1) : templatePath);
		if (stream == null) {
			throw new TemplateResolutionException("Template " + templatePath + " not found in the classpath", templatePath);
		}
		return new SimpleTemplate(stream);
	}

}
