package fr.sii.notification.email.attachment.translator;

import fr.sii.notification.email.attachment.Source;
import fr.sii.notification.email.exception.attachment.translator.SourceTranslatorException;

/**
 * The aim of a source translator is to transform a source into a new one. It
 * may be useful for preparing the attachment of the message before sending it.
 * 
 * @author Aurélien Baudet
 *
 */
public interface AttachmentSourceTranslator {
	/**
	 * Transform the source into a new one.
	 * 
	 * @param source
	 *            the source to transform
	 * @return the transformed source
	 * @throws SourceTranslatorException
	 *             when the transformation has failed
	 */
	public Source translate(Source source) throws SourceTranslatorException;
}
