package fr.sii.notification.core.translator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.notification.core.exception.handler.ContentTranslatorException;
import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.message.content.MultiContent;

/**
 * <p>
 * Decorator that is able to handle {@link MultiContent}. A {@link MultiContent}
 * can provide several contents to put into the final message. For example,
 * emails can send both HTML and text content.
 * </p>
 * <p>
 * This implementation calls the delegate content translator to apply
 * translation on each sub content of the {@link MultiContent}. It can be useful
 * to apply content translations on every sub content like it should be applied
 * for normal content.
 * </p>
 * <p>
 * The same translator is applied for all sub contents.
 * </p>
 * <p>
 * If the content is not a {@link MultiContent}, then the content is returned
 * as-is.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public class MultiContentTranslator implements ContentTranslator {
	private static final Logger LOG = LoggerFactory.getLogger(MultiContentTranslator.class);

	/**
	 * The content translator to apply on each sub content
	 */
	private ContentTranslator delegate;

	public MultiContentTranslator(ContentTranslator delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public Content translate(Content content) throws ContentTranslatorException {
		if (content instanceof MultiContent) {
			MultiContent result = new MultiContent();
			for (Content c : ((MultiContent) content).getContents()) {
				LOG.debug("Translate the sub content {} using {}", c, delegate);
				result.addContent(delegate.translate(c));
			}
			return result;
		} else {
			LOG.trace("Not a MultiContent => skip it");
			return content;
		}
	}

	@Override
	public String toString() {
		return "MultiContentTranslator";
	}

}
