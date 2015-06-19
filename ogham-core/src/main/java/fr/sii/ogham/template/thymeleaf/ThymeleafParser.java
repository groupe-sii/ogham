package fr.sii.ogham.template.thymeleaf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.exceptions.TemplateEngineException;

import fr.sii.ogham.core.exception.template.ContextException;
import fr.sii.ogham.core.exception.template.ParseException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.template.context.Context;
import fr.sii.ogham.core.template.parser.TemplateParser;

/**
 * Implementation for Thymeleaf template engine.
 * 
 * @author Aurélien Baudet
 *
 */
public class ThymeleafParser implements TemplateParser {
	private static final Logger LOG = LoggerFactory.getLogger(ThymeleafParser.class);

	/**
	 * Thymeleaf engine
	 */
	private TemplateEngine engine;
	
	/**
	 * Converts general context into Thymeleaf specific context
	 */
	private ThymeleafContextConverter contextConverter;
	
	public ThymeleafParser(TemplateEngine engine, ThymeleafLookupMappingResolver lookupResolver, ThymeleafContextConverter contextConverter) {
		super();
		this.engine = engine;
		this.contextConverter = contextConverter;
		engine.setTemplateResolver(lookupResolver);
	}

	public ThymeleafParser(TemplateEngine engine, ThymeleafLookupMappingResolver lookupResolver) {
		this(engine, lookupResolver, new ThymeleafContextConverter());
	}
	
	@Override
	public Content parse(String templateName, Context ctx) throws ParseException {
		try {
			LOG.debug("Parsing Thymeleaf template {} with context {}...", templateName, ctx);
			String result = engine.process(templateName, contextConverter.convert(ctx));
			LOG.debug("Template {} successfully parsed with context {}. Result:", templateName);
			LOG.debug(result);
			return new StringContent(result);
		} catch (TemplateEngineException e) {
			throw new ParseException("Failed to parse template with thymeleaf", templateName, ctx, e);
		} catch (ContextException e) {
			throw new ParseException("Failed to parse template with thymeleaf due to conversion error", templateName, ctx, e);
		}
	}

	@Override
	public String toString() {
		return "ThymeleafParser";
	}
	
}
