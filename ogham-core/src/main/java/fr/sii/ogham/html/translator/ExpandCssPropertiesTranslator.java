package fr.sii.ogham.html.translator;

import fr.sii.ogham.core.exception.handler.ContentTranslatorException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.translator.content.ContentTranslator;

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
		// TODO: implement translator
		return null;
	}

}
