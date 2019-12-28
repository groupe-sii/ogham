package fr.sii.ogham.core.translator.content;

import fr.sii.ogham.core.exception.handler.ContentTranslatorException;
import fr.sii.ogham.core.message.content.Content;

/**
 * The aim of a content translator is to transform a content into a new one. It
 * may be useful for preparing the content of the message before sending it.
 * 
 * @author Aurélien Baudet
 *
 */
public interface ContentTranslator {
	/**
	 * Transform the content into a new one.
	 * 
	 * @param content
	 *            the content to transform
	 * @return the transformed content
	 * @throws ContentTranslatorException
	 *             when the transformation has failed
	 */
	Content translate(Content content) throws ContentTranslatorException;
}
