package fr.sii.ogham.template.freemarker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.template.EngineDetectionException;
import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.core.template.context.Context;
import fr.sii.ogham.core.template.detector.TemplateEngineDetector;

/**
 * Detector checking template name extension. If it ends with '.ftl' then the
 * detector returns true. Otherwise it returns false.
 * 
 * @author Cyril Dejonghe
 *
 */
public class FreeMarkerTemplateDetector implements TemplateEngineDetector {
	private static final String FREEMARKER_TEMPLATE_EXTENSION = ".ftl";
	private static final Logger LOG = LoggerFactory.getLogger(FreeMarkerTemplateDetector.class);

	@Override
	public boolean canParse(String templateName, Context ctx, Resource template) throws EngineDetectionException {
		LOG.debug("Checking if FreeMarker can handle the template {}", templateName);
		boolean endsWithFreeMarkerExtension = templateName.endsWith(FREEMARKER_TEMPLATE_EXTENSION);
		if (endsWithFreeMarkerExtension) {
			LOG.debug("The template {} ends with " + FREEMARKER_TEMPLATE_EXTENSION + ". FreeMarker can be used", templateName);
		} else {
			LOG.debug("The template {} doesn't end with " + FREEMARKER_TEMPLATE_EXTENSION + ". FreeMarker can't be used", templateName);
		}
		return endsWithFreeMarkerExtension;

	}

}
