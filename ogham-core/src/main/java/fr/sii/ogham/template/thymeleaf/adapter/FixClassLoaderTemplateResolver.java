package fr.sii.ogham.template.thymeleaf.adapter;

import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * Classpath URL can start with "/" but Thymeleaf implementation can't handle
 * the URLs starting with "/". This implementation override the resource name
 * computation to handle this case.
 * 
 * @author Aurélien Baudet
 *
 */
public class FixClassLoaderTemplateResolver extends ClassLoaderTemplateResolver {

	@Override
	protected String computeResourceName(TemplateProcessingParameters templateProcessingParameters) {
		String resourceName = super.computeResourceName(templateProcessingParameters);
		return resourceName.startsWith("/") ? resourceName.substring(1) : resourceName;
	}

}
