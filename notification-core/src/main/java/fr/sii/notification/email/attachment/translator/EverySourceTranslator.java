package fr.sii.notification.email.attachment.translator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.notification.email.attachment.Source;
import fr.sii.notification.email.exception.attachment.translator.SourceTranslatorException;

/**
 * Decorator that loop through all delegate translators to transform the
 * attachment source of the message. Every translator will be called to update
 * the source. Each translator receive the source that may be updated by the
 * previous translator.
 * 
 * @author Aurélien Baudet
 *
 */
public class EverySourceTranslator implements AttachmentSourceTranslator {
	private static final Logger LOG = LoggerFactory.getLogger(EverySourceTranslator.class);

	/**
	 * The list of translators used to update the message content
	 */
	private List<AttachmentSourceTranslator> translators;

	/**
	 * Initialize the decorator with none, one or several translator
	 * implementations. The registration order may be important.
	 * 
	 * @param translators
	 *            the translators to register
	 */
	public EverySourceTranslator(AttachmentSourceTranslator... translators) {
		this(new ArrayList<>(Arrays.asList(translators)));
	}

	/**
	 * Initialize the decorator with the provided translator implementations.
	 * The registration order may be important.
	 * 
	 * @param translators
	 *            the translators to register
	 */
	public EverySourceTranslator(List<AttachmentSourceTranslator> translators) {
		super();
		this.translators = translators;
	}

	@Override
	public Source translate(Source source) throws SourceTranslatorException {
		Source result = source;
		for (AttachmentSourceTranslator translator : translators) {
			LOG.debug("Applying translator {} on content {}", translator, source);
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
	public void addTranslator(AttachmentSourceTranslator translator) {
		translators.add(translator);
	}

	/**
	 * Get the registered translators.
	 * 
	 * @return the list of translators
	 */
	public List<AttachmentSourceTranslator> getTranslators() {
		return translators;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EverySourceTranslator [translators=").append(translators).append("]");
		return builder.toString();
	}
}
