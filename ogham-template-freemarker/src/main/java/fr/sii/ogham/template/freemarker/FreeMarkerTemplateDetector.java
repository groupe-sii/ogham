package fr.sii.ogham.template.freemarker;

import static java.util.Arrays.asList;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.exception.template.EngineDetectionException;
import fr.sii.ogham.core.resource.path.ResolvedPath;
import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
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
	private static final Logger LOG = LoggerFactory.getLogger(FreeMarkerTemplateDetector.class);

	/**
	 * The template resolver used to find the template
	 */
	private final ResourceResolver resolver;

	/** Recognized FTL extensions. */
	private List<String> extensions;

	public FreeMarkerTemplateDetector(ResourceResolver resolver) {
		this(resolver, ".ftl", ".ftlh");
	}

	public FreeMarkerTemplateDetector(ResourceResolver resolver, String... extensions) {
		super();
		this.resolver = resolver;
		this.extensions = asList(extensions);
	}

	@Override
	public boolean canParse(ResourcePath templatePath, Context ctx) throws EngineDetectionException {
		LOG.debug("Checking if FreeMarker can handle the template {}", templatePath);

		ResolvedPath resolvedTemplatePath = resolver.resolve(templatePath);
		if (resolvedTemplatePath == null) {
			return false;
		}

		for (String extension : extensions) {
			if (resolvedTemplatePath.getResolvedPath().endsWith(extension) && exists(resolvedTemplatePath)) {
				LOG.debug("The template {} ends with {}. FreeMarker can be used", templatePath, extension);
				return true;
			}
		}

		LOG.debug("The template {} doesn't end with any of {}. FreeMarker can't be used", templatePath, extensions);
		return false;

	}

	@SuppressWarnings("squid:S1166")
	private boolean exists(ResolvedPath resolvedTemplatePath) {
		try {
			resolver.getResource(resolvedTemplatePath);
			return true;
		} catch(ResourceResolutionException e) {
			return false;
		}
	}

}
