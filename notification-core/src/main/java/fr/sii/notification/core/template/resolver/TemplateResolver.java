package fr.sii.notification.core.template.resolver;

import fr.sii.notification.core.exception.template.TemplateResolutionException;
import fr.sii.notification.core.template.Template;

/**
 * <p>
 * Interface for all Template Resolvers. Template resolvers are in charge of
 * finding template from its name (or path). A template can be stored at many
 * places. For example, a template can be stored either on the file system, into
 * the classpath, on a distant URL or into a database...
 * </p>
 * <p>
 * Each implementation is able to handle one resolution mechanism. Any new
 * implementation can be defined for future storage source.
 * </p>
 * 
 * @author Aurélien Baudet
 * @see Template
 */
public interface TemplateResolver {
	/**
	 * Find the template using the template name (or path).
	 * 
	 * @param templateName
	 *            the name of the template (or path to the template)
	 * @return the found template
	 * @throws TemplateResolutionException
	 *             when the template couldn't be found
	 */
	public Template getTemplate(String templateName) throws TemplateResolutionException;
}
