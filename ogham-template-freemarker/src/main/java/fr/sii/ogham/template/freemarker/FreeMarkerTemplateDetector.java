package fr.sii.ogham.template.freemarker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.template.EngineDetectionException;
import fr.sii.ogham.core.template.context.Context;
import fr.sii.ogham.core.template.detector.TemplateEngineDetector;

/**
 * Detector checking template name extension. If it ends with '.ftl' then the detector returns true. Otherwise it returns false.
 * 
 * @author Cyril Dejonghe
 *
 */
public class FreeMarkerTemplateDetector implements TemplateEngineDetector {
	private static final Logger LOG = LoggerFactory.getLogger(FreeMarkerTemplateDetector.class);

	private static final String FREEMARKER_TEMPLATE_EXTENSION = ".ftl";

	/** Recognized FTL extensions. */
	private String[] extensions;

	public FreeMarkerTemplateDetector() {
		this(FREEMARKER_TEMPLATE_EXTENSION);
	}

	public FreeMarkerTemplateDetector(String... extensions) {
		super();
		this.extensions = extensions;
	}

	@Override
	public boolean canParse(String templateName, Context ctx) throws EngineDetectionException {
		LOG.debug("Checking if FreeMarker can handle the template {}", templateName);

		boolean result = false;

		for (String extension : extensions) {
			result = templateName.endsWith(extension);
			if (result) {
				LOG.debug("The template {} ends with {}. FreeMarker can be used", templateName, extension);
			}
		}

		if (!result) {
			LOG.debug("The template {} doesn't end with any of {}. FreeMarker can't be used", templateName, extensions);
		}
		return result;

	}

}
