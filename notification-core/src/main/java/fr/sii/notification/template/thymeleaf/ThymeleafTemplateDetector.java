package fr.sii.notification.template.thymeleaf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.notification.core.exception.template.EngineDetectionException;
import fr.sii.notification.core.template.Template;
import fr.sii.notification.core.template.context.Context;
import fr.sii.notification.core.template.detector.TemplateEngineDetector;

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

	@Override
	public boolean canParse(String templateName, Context ctx, Template template) throws EngineDetectionException {
		LOG.debug("Checking if Thymeleaf can handle the template {}", templateName);
		try (BufferedReader br = new BufferedReader(new InputStreamReader(template.getInputStream()))) {
			String line;
			boolean containsThymeleafNamespace;
			do {
				line = br.readLine();
				containsThymeleafNamespace = NAMESPACE_PATTERN.matcher(line).find();
			} while (line != null && !containsThymeleafNamespace);
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

}
