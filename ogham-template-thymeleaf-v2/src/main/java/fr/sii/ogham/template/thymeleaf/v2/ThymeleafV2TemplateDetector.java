package fr.sii.ogham.template.thymeleaf.v2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.exception.template.EngineDetectionException;
import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.core.resource.path.ResourcePath;
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
public class ThymeleafV2TemplateDetector implements TemplateEngineDetector {
	private static final Logger LOG = LoggerFactory.getLogger(ThymeleafV2TemplateDetector.class);

	/**
	 * The pattern to search into the template
	 */
	private static final Pattern NAMESPACE_PATTERN = Pattern.compile("xmlns[^=]+=\\s*\"http://www.thymeleaf.org\"");

	/**
	 * The template resolver used to find the template
	 */
	private final ResourceResolver resolver;

	public ThymeleafV2TemplateDetector(ResourceResolver resolver) {
		super();
		this.resolver = resolver;
	}

	@Override
	public boolean canParse(ResourcePath template, Context ctx) throws EngineDetectionException {
		LOG.debug("Checking if Thymeleaf can handle the template {}", template);
		Resource resolvedTemplate = getTemplate(template);
		if (resolvedTemplate == null) {
			return false;
		}
		try (BufferedReader br = new BufferedReader(new InputStreamReader(resolvedTemplate.getInputStream()))) {
			boolean containsThymeleafNamespace = containsThymeleafNamespace(br);
			if (containsThymeleafNamespace) {
				LOG.debug("The template {} contains the namespace http://www.thymeleaf.org. Thymeleaf can be used", template);
			} else {
				LOG.debug("The template {} doesn't contain the namespace http://www.thymeleaf.org. Thymeleaf can't be used", template);
			}
			return containsThymeleafNamespace || isEmptyTemplate(resolvedTemplate);
		} catch (IOException e) {
			throw new EngineDetectionException("Failed to detect because template can't be read by thymeleaf", e);
		}
	}

	private static boolean containsThymeleafNamespace(BufferedReader br) throws IOException {
		String line;
		do {
			line = br.readLine();
			if (line != null && NAMESPACE_PATTERN.matcher(line).find()) {
				return true;
			}
		} while (line != null);
		return false;
	}


	private static boolean isEmptyTemplate(Resource template) throws IOException {
		try (InputStream stream = template.getInputStream()) {
			return stream.read() == -1;
		}
	}

	private Resource getTemplate(ResourcePath templateName) {
		try {
			return resolver.getResource(templateName);
		} catch (ResourceResolutionException e) {
			LOG.trace("Thymeleaf detector can't be applied because {} couldn't be resolved", templateName, e);
			return null;
		}
	}

}
