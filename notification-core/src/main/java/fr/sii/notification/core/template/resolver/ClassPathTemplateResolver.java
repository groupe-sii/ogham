package fr.sii.notification.core.template.resolver;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static final Logger LOG = LoggerFactory.getLogger(ClassPathTemplateResolver.class);

	@Override
	public Template getTemplate(String templatePath) throws TemplateResolutionException {
		try {
			LOG.debug("Loading template {} from classpath...", templatePath);
			InputStream stream = getClass().getClassLoader().getResourceAsStream(templatePath.startsWith("/") ? templatePath.substring(1) : templatePath);
			if (stream == null) {
				throw new TemplateResolutionException("Template " + templatePath + " not found in the classpath", templatePath);
			}
			LOG.debug("Template {} available in the classpath...", templatePath);
			return new SimpleTemplate(stream);
		} catch (IOException e) {
			throw new TemplateResolutionException("The template "+templatePath+" is not readable", templatePath, e);
		}
	}

}
