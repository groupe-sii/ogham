package fr.sii.ogham.core.exception.handler;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.core.translator.content.ContentTranslator;
import fr.sii.ogham.core.translator.content.TemplateContentTranslator;

/**
 * Ogham uses a chain to transform original content into final content. For
 * example, the original content may be a path to a template. Therefore, there
 * is a {@link ContentTranslator} that is in charge of loading and parse the
 * template. The template parsing is delegated to a {@link TemplateParser} and
 * the template parser is included in the chain using a
 * {@link TemplateContentTranslator}.
 * 
 * This exception wraps exceptions that are thrown while trying to load the
 * template.
 * 
 * This exception is annotated with {@link Recoverable} to indicate that this
 * kind of exception may be thrown at some point because the resolver didn't
 * find the template but another resolver may find it.
 * 
 * @author Aurélien Baudet
 *
 */
@Recoverable
public class TemplateNotFoundException extends ContentTranslatorException {
	/**
	 * 
	 */
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public TemplateNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public TemplateNotFoundException(String message) {
		super(message);
	}

	public TemplateNotFoundException(Throwable cause) {
		super(cause);
	}

}
