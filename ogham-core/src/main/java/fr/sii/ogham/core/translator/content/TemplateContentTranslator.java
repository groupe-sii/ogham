package fr.sii.ogham.core.translator.content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.handler.ContentTranslatorException;
import fr.sii.ogham.core.exception.template.ParseException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.template.parser.TemplateParser;

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
	private static final Logger LOG = LoggerFactory.getLogger(TemplateContentTranslator.class);

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
				LOG.info("Parse template {} using context {}", template.getPath(), template.getContext());
				LOG.debug("Parse template content {} using {}", template, parser);
				return parser.parse(template.getPath(), template.getContext());
			} catch (ParseException e) {
				throw new ContentTranslatorException("failed to translate templated content", e);
			}
		} else {
			LOG.trace("Not a TemplateContent => skip it");
			return content;
		}
	}

	@Override
	public String toString() {
		return "TemplateContentTranslator";
	}
	
}
