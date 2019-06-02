package fr.sii.ogham.template.thymeleaf.v2.resolver;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.IResourceResolver;

public class StringResourceResolver implements IResourceResolver {

	private static final String NAME = "STRING";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public InputStream getResourceAsStream(TemplateProcessingParameters templateProcessingParameters, String resourceName) {
		return new ByteArrayInputStream(resourceName.getBytes());
	}

}
