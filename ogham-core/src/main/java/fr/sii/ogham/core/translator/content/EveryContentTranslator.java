package fr.sii.ogham.core.translator.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.handler.ContentTranslatorException;
import fr.sii.ogham.core.message.content.Content;

/**
 * Decorator that loop through all delegate translators to transform the content
 * of the message. Every translator will be called to update the content. Each
 * translator receive the content that may be updated by the previous
 * translator.
 * 
 * @author Aurélien Baudet
 *
 */
public class EveryContentTranslator implements ContentTranslator {
	private static final Logger LOG = LoggerFactory.getLogger(EveryContentTranslator.class);

	/**
	 * The list of translators used to update the message content
	 */
	private List<ContentTranslator> translators;

	/**
	 * Initialize the decorator with none, one or several translator
	 * implementations. The registration order may be important.
	 * 
	 * @param translators
	 *            the translators to register
	 */
	public EveryContentTranslator(ContentTranslator... translators) {
		this(new ArrayList<>(Arrays.asList(translators)));
	}

	/**
	 * Initialize the decorator with the provided translator implementations.
	 * The registration order may be important.
	 * 
	 * @param translators
	 *            the translators to register
	 */
	public EveryContentTranslator(List<ContentTranslator> translators) {
		super();
		this.translators = translators;
	}

	@Override
	public Content translate(Content content) throws ContentTranslatorException {
		Content result = content;
		for (ContentTranslator translator : translators) {
			LOG.debug("Applying translator {} on content {}", translator, content);
			result = translator.translate(result);
		}
		return result;
	}

	/**
	 * Register a new translator. The translator is added at the end.
	 * 
	 * @param translator
	 *            the translator to register
	 */
	public void addTranslator(ContentTranslator translator) {
		translators.add(translator);
	}

	/**
	 * Get the registered translators.
	 * 
	 * @return the list of translators
	 */
	public List<ContentTranslator> getTranslators() {
		return translators;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EveryContentTranslator [translators=").append(translators).append("]");
		return builder.toString();
	}
}
