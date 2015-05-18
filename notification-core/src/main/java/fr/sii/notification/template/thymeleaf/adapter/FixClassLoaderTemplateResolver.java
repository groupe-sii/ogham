package fr.sii.notification.template.thymeleaf.adapter;

import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class FixClassLoaderTemplateResolver extends ClassLoaderTemplateResolver {

	@Override
	protected String computeResourceName(TemplateProcessingParameters templateProcessingParameters) {
		String resourceName = super.computeResourceName(templateProcessingParameters);
		return resourceName.startsWith("/") ? resourceName.substring(1) : resourceName;
	}

}
