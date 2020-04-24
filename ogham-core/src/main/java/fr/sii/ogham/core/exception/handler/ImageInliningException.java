package fr.sii.ogham.core.exception.handler;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.translator.content.ContentTranslator;
import fr.sii.ogham.html.inliner.ImageInliner;
import fr.sii.ogham.html.translator.InlineImageTranslator;

/**
 * Ogham uses a chain to transform original content into final content. For
 * example, the original content may contain references to images. Therefore,
 * there is a {@link ContentTranslator} that is in charge of loading images and
 * inlining them in the content. The inlining is delegated to a
 * {@link ImageInliner} and this inliner is included in the chain using a
 * {@link InlineImageTranslator}.
 * 
 * This exception wraps exceptions that are thrown while trying to inline the
 * images.
 * 
 * @author Aurélien Baudet
 *
 */
public class ImageInliningException extends ContentTranslatorException {
	/**
	 * 
	 */
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public ImageInliningException(String message, Throwable cause) {
		super(message, cause);
	}

	public ImageInliningException(String message) {
		super(message);
	}

	public ImageInliningException(Throwable cause) {
		super(cause);
	}

}
