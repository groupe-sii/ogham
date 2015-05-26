package fr.sii.notification.email.attachment.translator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.notification.email.attachment.LookupSource;
import fr.sii.notification.email.attachment.Source;
import fr.sii.notification.email.attachment.resolver.LookupSourceResolver;
import fr.sii.notification.email.exception.attachment.resolver.SourceResolutionException;
import fr.sii.notification.email.exception.attachment.translator.SourceTranslatorException;

/**
 * <p>
 * Translator that handles {@link LookupSource}. It associates a lookup with a
 * real source implementation.
 * </p>
 * <p>
 * This translator uses a {@link LookupSourceResolver} to get the real source
 * implementation.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public class LookupSourceTranslator implements AttachmentSourceTranslator {
	private static final Logger LOG = LoggerFactory.getLogger(LookupSourceTranslator.class);

	/**
	 * The parser to use for finding, loading and evaluating the template
	 */
	private LookupSourceResolver resolver;

	public LookupSourceTranslator(LookupSourceResolver resolver) {
		super();
		this.resolver = resolver;
	}

	@Override
	public Source translate(Source source) throws SourceTranslatorException {
		if (source instanceof LookupSource) {
			try {
				LookupSource lookupSource = (LookupSource) source;
				LOG.debug("Resolving {} using {}", lookupSource.getPath(), resolver);
				return resolver.resolve(lookupSource.getPath());
			} catch (SourceResolutionException e) {
				throw new SourceTranslatorException("failed to translate lookup source", e);
			}
		} else {
			LOG.trace("Not a LookupSource => skip it");
			return source;
		}
	}

	@Override
	public String toString() {
		return "LookupSourceTranslator";
	}

}
