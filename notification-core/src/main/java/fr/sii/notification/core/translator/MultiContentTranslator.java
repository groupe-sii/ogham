package fr.sii.notification.core.translator;

import fr.sii.notification.core.exception.handler.ContentTranslatorException;
import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.message.content.MultiContent;

public class MultiContentTranslator implements ContentTranslator {

	private ContentTranslator delegate;
	
	public MultiContentTranslator(ContentTranslator delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public Content translate(Content content) throws ContentTranslatorException {
		if(content instanceof MultiContent) {
			MultiContent result = new MultiContent();
			for(Content c : ((MultiContent) content).getContents()) {
				result.addContent(delegate.translate(c));
			}
			return result;
		} else {
			return content;
		}
	}

}
