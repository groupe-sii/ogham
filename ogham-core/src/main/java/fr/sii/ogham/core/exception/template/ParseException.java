package fr.sii.ogham.core.exception.template;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.template.context.Context;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.template.exception.VariantResolutionException;

/**
 * General exception to indicate that the {@link TemplateParser} couldn't parse
 * the template.
 * 
 * It may happen in several cases:
 * <ul>
 * <li>The template doesn't exist</li>
 * <li>The template is not readable</li>
 * <li>The template exists but it is malformed</li>
 * <li>The template is well-formed but the data (context) doesn't provide the
 * right variables</li>
 * <li>...</li>
 * </ul>
 * 
 * This exception wraps the original cause. It also has several subclasses to
 * indicate specific reasons for some failures.
 * 
 * @author Aurélien Baudet
 *
 * @see VariantResolutionException
 */
public class ParseException extends MessagingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final transient ResourcePath templatePath;
	private final transient Context context;

	public ParseException(String message, ResourcePath templatePath, Context context, Throwable cause) {
		super(message, cause);
		this.templatePath = templatePath;
		this.context = context;
	}

	public ParseException(String message, ResourcePath templatePath, Context context) {
		super(message);
		this.templatePath = templatePath;
		this.context = context;
	}

	public ParseException(ResourcePath templatePath, Context context, Throwable cause) {
		super(cause);
		this.templatePath = templatePath;
		this.context = context;
	}

	public ResourcePath getTemplatePath() {
		return templatePath;
	}

	public Context getContext() {
		return context;
	}
}
