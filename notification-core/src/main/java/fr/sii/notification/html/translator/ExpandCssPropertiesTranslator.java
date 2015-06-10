package fr.sii.notification.html.translator;

import fr.sii.notification.core.exception.handler.ContentTranslatorException;
import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.translator.ContentTranslator;

/**
 * Automatically transform CSS shortcuts to the expanded version. For example,
 * it will convert:
 * 
 * <pre>
 * padding: 2px 5px;
 * </pre>
 * 
 * into
 * 
 * <pre>
 * padding: 2px 5px 2px 5px;
 * </pre>
 * 
 * @author Aurélien Baudet
 *
 */
public class ExpandCssPropertiesTranslator implements ContentTranslator {

	@Override
	public Content translate(Content content) throws ContentTranslatorException {
		// TODO Auto-generated method stub
		return null;
	}

}
