package fr.sii.ogham.core.template.detector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.exception.template.EngineDetectionException;
import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.core.resource.SimpleResource;
import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.template.context.Context;

/**
 * A template engine detector that indicates it can parse the template if the
 * resource represented byt the template name corresponds to a
 * {@link SimpleResource}.
 * 
 * This is useful when the template name is not a path but directly the template
 * content as string.
 * 
 * @author Aurélien Baudet
 *
 */
public class SimpleResourceEngineDetector implements TemplateEngineDetector {
	private static final Logger LOG = LoggerFactory.getLogger(SimpleResourceEngineDetector.class);

	private final ResourceResolver resolver;
	private final TemplateEngineDetector delegate;

	/**
	 * The resolver is used to check if the resolved template from provided name
	 * is a {@link SimpleResource}. The delegate is used to make the real
	 * detection if it is a {@link SimpleResource}.
	 * 
	 * @param resolver
	 *            the resolver to check if resolved template from provided name
	 *            is a {@link SimpleResource} or not
	 * @param delegate
	 *            the delegate to call if it is a {@link SimpleResource}
	 */
	public SimpleResourceEngineDetector(ResourceResolver resolver, TemplateEngineDetector delegate) {
		super();
		this.resolver = resolver;
		this.delegate = delegate;
	}

	@Override
	public boolean canParse(ResourcePath template, Context ctx) throws EngineDetectionException {
		Resource resource = getResource(template);
		// no resource matches requested template
		// => can't parse (we need a SimpleResource)
		if (resource == null) {
			return false;
		}

		// resource found but not a SimpleResource
		// => can't parse
		if (!(resource instanceof SimpleResource)) {
			return false;
		}

		// it is a SimpleResource
		// => may be able to parse, it depends on delegate
		return delegate.canParse(template, ctx);
	}

	private Resource getResource(ResourcePath template) {
		try {
			return resolver.getResource(template);
		} catch (ResourceResolutionException e) {
			LOG.trace("resource resolution couldn't resolve template " + template + " while trying detect template engine", e);
			return null;
		}
	}

}
