package fr.sii.notification.email.attachment.translator;

import fr.sii.notification.email.attachment.Source;
import fr.sii.notification.email.exception.attachment.translator.SourceTranslatorException;

/**
 * Apply no transformation at all. Just provide the received source as the
 * result. This may be useful to avoid null values.
 * 
 * @author Aurélien Baudet
 *
 */
public class DefaultSourceTranslator implements AttachmentSourceTranslator {

	@Override
	public Source translate(Source source) throws SourceTranslatorException {
		return source;
	}

}
