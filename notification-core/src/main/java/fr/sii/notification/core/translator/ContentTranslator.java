package fr.sii.notification.core.translator;

import fr.sii.notification.core.exception.handler.ContentTranslatorException;
import fr.sii.notification.core.message.content.Content;

public interface ContentTranslator {
	public Content translate(Content content) throws ContentTranslatorException;
}
