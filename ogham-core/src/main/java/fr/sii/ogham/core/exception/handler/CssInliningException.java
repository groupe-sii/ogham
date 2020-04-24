package fr.sii.ogham.core.exception.handler;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.translator.content.ContentTranslator;
import fr.sii.ogham.html.inliner.CssInliner;
import fr.sii.ogham.html.translator.InlineCssTranslator;

/**
 * Ogham uses a chain to transform original content into final content. For
 * example, the original content may contain references to CSS styles.
 * Therefore, there is a {@link ContentTranslator} that is in charge of loading
 * the stylesheets and inlining them in the content. The inlining is delegated
 * to a {@link CssInliner} and this inliner is included in the chain using a
 * {@link InlineCssTranslator}.
 * 
 * This exception wraps exceptions that are thrown while trying to inline the
 * styles.
 * 
 * @author Aurélien Baudet
 *
 */
public class CssInliningException extends ContentTranslatorException {
	/**
	 * 
	 */
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public CssInliningException(String message, Throwable cause) {
		super(message, cause);
	}

	public CssInliningException(String message) {
		super(message);
	}

	public CssInliningException(Throwable cause) {
		super(cause);
	}

}
