package fr.sii.notification.core.translator;

import fr.sii.notification.core.exception.handler.ContentTranslatorException;
import fr.sii.notification.core.exception.template.ParseException;
import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.message.content.TemplateContent;
import fr.sii.notification.core.template.parser.TemplateParser;

/**
 * <p>
 * Translator that handles {@link TemplateContent}. It parses the template and
 * provide an evaluated content.
 * </p>
 * <p>
 * The template parsing is delegated to a {@link TemplateParser}.
 * </p>
 * <p>
 * If the content is not a {@link TemplateContent}, then the content is returned
 * as-is
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public class TemplateContentTranslator implements ContentTranslator {
	/**
	 * The parser to use for finding, loading and evaluating the template
	 */
	private TemplateParser parser;

	public TemplateContentTranslator(TemplateParser parser) {
		super();
		this.parser = parser;
	}

	@Override
	public Content translate(Content content) throws ContentTranslatorException {
		if (content instanceof TemplateContent) {
			try {
				TemplateContent template = (TemplateContent) content;
				return parser.parse(template.getPath(), template.getContext());
			} catch (ParseException e) {
				throw new ContentTranslatorException("failed to translate templated content", e);
			}
		} else {
			return content;
		}
	}

}
