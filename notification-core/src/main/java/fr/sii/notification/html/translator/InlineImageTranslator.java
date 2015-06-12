package fr.sii.notification.html.translator;

import fr.sii.notification.core.exception.handler.ContentTranslatorException;
import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.translator.content.ContentTranslator;
import fr.sii.notification.html.inliner.ImageInliner;

public class InlineImageTranslator implements ContentTranslator {

	private ImageInliner inliner;
	
	@Override
	public Content translate(Content content) throws ContentTranslatorException {
//		return new InlinedContent(inliner.inline(content), inliner.);
		return null;
	}

}
