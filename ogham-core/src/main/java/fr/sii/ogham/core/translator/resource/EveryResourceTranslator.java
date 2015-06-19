package fr.sii.ogham.core.translator.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.email.exception.attachment.translator.ResourceTranslatorException;

/**
 * Decorator that loop through all delegate translators to transform the
 * attachment resource of the message. Every translator will be called to update
 * the resource. Each translator receive the resource that may be updated by the
 * previous translator.
 * 
 * @author Aurélien Baudet
 *
 */
public class EveryResourceTranslator implements AttachmentResourceTranslator {
	private static final Logger LOG = LoggerFactory.getLogger(EveryResourceTranslator.class);

	/**
	 * The list of translators used to update the message content
	 */
	private List<AttachmentResourceTranslator> translators;

	/**
	 * Initialize the decorator with none, one or several translator
	 * implementations. The registration order may be important.
	 * 
	 * @param translators
	 *            the translators to register
	 */
	public EveryResourceTranslator(AttachmentResourceTranslator... translators) {
		this(new ArrayList<>(Arrays.asList(translators)));
	}

	/**
	 * Initialize the decorator with the provided translator implementations.
	 * The registration order may be important.
	 * 
	 * @param translators
	 *            the translators to register
	 */
	public EveryResourceTranslator(List<AttachmentResourceTranslator> translators) {
		super();
		this.translators = translators;
	}

	@Override
	public Resource translate(Resource resource) throws ResourceTranslatorException {
		Resource result = resource;
		for (AttachmentResourceTranslator translator : translators) {
			LOG.debug("Applying translator {} on resource {}", translator, resource);
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
	public void addTranslator(AttachmentResourceTranslator translator) {
		translators.add(translator);
	}

	/**
	 * Get the registered translators.
	 * 
	 * @return the list of translators
	 */
	public List<AttachmentResourceTranslator> getTranslators() {
		return translators;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EveryResourceTranslator [translators=").append(translators).append("]");
		return builder.toString();
	}
}
