package fr.sii.ogham.core.translator.resource;

import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.email.exception.attachment.translator.ResourceTranslatorException;

/**
 * Apply no transformation at all. Just provide the received resource as the
 * result. This may be useful to avoid null values.
 * 
 * @author Aurélien Baudet
 *
 */
public class DefaultResourceTranslator implements AttachmentResourceTranslator {

	@Override
	public Resource translate(Resource resource) throws ResourceTranslatorException {
		return resource;
	}

}
