package fr.sii.notification.core.translator;

import fr.sii.notification.core.exception.handler.ContentTranslatorException;
import fr.sii.notification.core.message.content.Content;

public class DefaultContentTranslator implements ContentTranslator {

	@Override
	public Content translate(Content content) throws ContentTranslatorException {
		return content;
	}

}
