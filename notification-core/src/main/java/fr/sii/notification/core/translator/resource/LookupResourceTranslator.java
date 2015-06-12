package fr.sii.notification.core.translator.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.notification.core.exception.resource.ResourceResolutionException;
import fr.sii.notification.core.resource.LookupResource;
import fr.sii.notification.core.resource.Resource;
import fr.sii.notification.core.resource.resolver.LookupMappingResolver;
import fr.sii.notification.email.exception.attachment.translator.ResourceTranslatorException;

/**
 * <p>
 * Translator that handles {@link LookupResource}. It associates a lookup with a
 * real resource implementation.
 * </p>
 * <p>
 * This translator uses a {@link LookupMappingResolver} to get the real resource
 * implementation.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public class LookupResourceTranslator implements AttachmentResourceTranslator {
	private static final Logger LOG = LoggerFactory.getLogger(LookupResourceTranslator.class);

	/**
	 * The parser to use for finding, loading and evaluating the template
	 */
	private LookupMappingResolver resolver;

	public LookupResourceTranslator(LookupMappingResolver resolver) {
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
