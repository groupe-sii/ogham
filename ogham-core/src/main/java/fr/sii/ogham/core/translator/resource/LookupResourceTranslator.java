package fr.sii.ogham.core.translator.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.resource.LookupResource;
import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.email.exception.attachment.translator.ResourceTranslatorException;

/**
 * <p>
 * Translator that handles {@link LookupResource}. It associates a lookup with a
 * real resource implementation.
 * </p>
 * <p>
 * This translator uses a {@link ResourceResolver} to get the real
 * resource implementation.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public class LookupResourceTranslator implements AttachmentResourceTranslator {
	private static final Logger LOG = LoggerFactory.getLogger(LookupResourceTranslator.class);

	private final ResourceResolver resolver;

	public LookupResourceTranslator(ResourceResolver resolver) {
		super();
		this.resolver = resolver;
	}

	@Override
	public Resource translate(Resource resource) throws ResourceTranslatorException {
		if (resource instanceof LookupResource) {
			try {
				LookupResource lookupResource = (LookupResource) resource;
				LOG.debug("Resolving {} using {}", lookupResource.getPath(), resolver);
				return resolver.getResource(lookupResource.getPath());
			} catch (ResourceResolutionException e) {
				throw new ResourceTranslatorException("failed to translate lookup resource", e);
			}
		} else {
			LOG.trace("Not a LookupResource => skip it");
			return resource;
		}
	}

	@Override
	public String toString() {
		return "LookupResourceTranslator";
	}

}
