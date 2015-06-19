package fr.sii.ogham.core.template.parser;

import fr.sii.ogham.core.exception.template.ParseException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.template.context.Context;

/**
 * Interface that defines the general contract for template parsing. Template
 * parsing consists to load a template from its name (or path) and then replace
 * all variables that are defined in the template by the values provided by the
 * context.
 * 
 * The parser may rely on a {@link ResourceResolver} for finding and loading
 * template.
 * 
 * @author Aurélien Baudet
 * @see ResourceResolver More information about template resolution
 */
public interface TemplateParser {
	/**
	 * Load the template from its name (or path). Read it and replace variables
	 * by the values defined in the context.
	 * 
	 * @param templateName
	 *            the name of the template to load (or the path)
	 * @param ctx
	 *            the context that contains the variable values
	 * @return the content generated from the template
	 * @throws ParseException
	 *             when the template couldn't be parsed either if the template
	 *             couldn't be read or if the template could't be processed
	 */
	public Content parse(String templateName, Context ctx) throws ParseException;
}
