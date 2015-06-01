package fr.sii.notification.html.translator;

import fr.sii.notification.core.exception.handler.ContentTranslatorException;
import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.message.content.StringContent;
import fr.sii.notification.core.translator.ContentTranslator;
import fr.sii.notification.html.inliner.CssInliner;

public class InlineCssTranslator implements ContentTranslator {
	private CssInliner cssInliner;
	
	public InlineCssTranslator(CssInliner cssInliner) {
		super();
		this.cssInliner = cssInliner;
	}

	@Override
	public Content translate(Content content) throws ContentTranslatorException {
		// TODO: load external css files
//		return new StringContent(cssInliner.inline(content.toString()));
		return null;
	}

}
