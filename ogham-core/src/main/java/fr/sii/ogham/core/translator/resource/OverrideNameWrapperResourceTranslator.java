package fr.sii.ogham.core.translator.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.resource.OverrideNameWrapper;
import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.email.exception.attachment.translator.ResourceTranslatorException;

/**
 * <p>
 * Translator that handles {@link OverrideNameWrapper}. It delegates to another
 * {@link AttachmentResourceTranslator} to get handle the wrapped
 * {@link Resource}. The handled {@link Resource} is then re-wrapped into a
 * {@link OverrideNameWrapper}.
 * 
 * @author Aurélien Baudet
 *
 */
public class OverrideNameWrapperResourceTranslator implements AttachmentResourceTranslator {
	private static final Logger LOG = LoggerFactory.getLogger(OverrideNameWrapperResourceTranslator.class);

	private final AttachmentResourceTranslator delegate;

	public OverrideNameWrapperResourceTranslator(AttachmentResourceTranslator delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public Resource translate(Resource resource) throws ResourceTranslatorException {
		if (resource instanceof OverrideNameWrapper) {
			OverrideNameWrapper namedResource = (OverrideNameWrapper) resource;
			Resource translated = delegate.translate(namedResource.getDelegate());
			return new OverrideNameWrapper(translated, namedResource.getName());
		} else {
			LOG.trace("Not a OverrideNameWrapper => skip it");
			return resource;
		}
	}

	@Override
	public String toString() {
		return "OverrideNameWrapperResourceTranslator";
	}

}
