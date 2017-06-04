package fr.sii.ogham.template.thymeleaf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.exception.template.EngineDetectionException;
import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.template.context.Context;
import fr.sii.ogham.core.template.detector.TemplateEngineDetector;

/**
 * Detector that reads the content of the template. If the template contains the
 * Thymeleaf namespace (http://www.thymeleaf.org) then the detector returns
 * true. Otherwise it returns false.
 * 
 * @author Aurélien Baudet
 *
 */
public class ThymeleafTemplateDetector implements TemplateEngineDetector {
	private static final Logger LOG = LoggerFactory.getLogger(ThymeleafTemplateDetector.class);

	/**
	 * The pattern to search into the template
	 */
	private static final Pattern NAMESPACE_PATTERN = Pattern.compile("xmlns[^=]+=\\s*\"http://www.thymeleaf.org\"");

	/**
	 * The template resolver used to find the template
	 */
	private final ResourceResolver resolver;
	
	public ThymeleafTemplateDetector(ResourceResolver resolver) {
		super();
		this.resolver = resolver;
	}

	@Override
	public boolean canParse(String templateName, Context ctx) throws EngineDetectionException {
		LOG.debug("Checking if Thymeleaf can handle the template {}", templateName);
		Resource resolvedTemplate = getTemplate(templateName);
		if(resolvedTemplate==null) {
			return false;
		}
		try (BufferedReader br = new BufferedReader(new InputStreamReader(resolvedTemplate.getInputStream()))) {
			boolean containsThymeleafNamespace = containsThymeleafNamespace(br);
			if(containsThymeleafNamespace) {
				LOG.debug("The template {} contains the namespace http://www.thymeleaf.org. Thymeleaf can be used", templateName);
			} else {
				LOG.debug("The template {} doesn't contain the namespace http://www.thymeleaf.org. Thymeleaf can't be used", templateName);
			}
			return containsThymeleafNamespace;
		} catch (IOException e) {
			throw new EngineDetectionException("Failed to detect if template can be read by thymeleaf", e);
		}
	}

	private boolean containsThymeleafNamespace(BufferedReader br) throws IOException {
		String line;
		boolean containsThymeleafNamespace = false;
		do {
			line = br.readLine();
			if(line != null) {
				containsThymeleafNamespace = NAMESPACE_PATTERN.matcher(line).find();
			}
		} while (line != null && !containsThymeleafNamespace);
		return containsThymeleafNamespace;
	}
	
	private Resource getTemplate(String templateName) throws EngineDetectionException {
		try {
			return resolver.getResource(templateName);
		} catch (ResourceResolutionException e) {
			throw new EngineDetectionException("Failed to automatically detect parser because the template couldn't be resolved", e);
		}
	}

}
