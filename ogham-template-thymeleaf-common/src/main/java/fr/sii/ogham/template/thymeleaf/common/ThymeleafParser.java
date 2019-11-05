package fr.sii.ogham.template.thymeleaf.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.exceptions.TemplateEngineException;

import fr.sii.ogham.core.exception.template.ContextException;
import fr.sii.ogham.core.exception.template.ParseException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.ParsedContent;
import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.template.context.Context;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.template.exception.TemplateRuntimeException;

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

	/**
	 * The template resolver used to find the template
	 */
	private final ResourceResolver resolver;
	
	public ThymeleafParser(TemplateEngine engine, ResourceResolver resolver, ThymeleafContextConverter contextConverter) {
		super();
		this.engine = engine;
		this.resolver = resolver;
		this.contextConverter = contextConverter;
	}

	public ThymeleafParser(TemplateEngine engine, ResourceResolver resolver) {
		this(engine, resolver, new SimpleThymeleafContextConverter());
	}
	
	@Override
	public Content parse(ResourcePath templatePath, Context ctx) throws ParseException {
		try {
			LOG.debug("Parsing Thymeleaf template {} with context {}...", templatePath, ctx);
			String result = engine.process(templatePath.getOriginalPath(), contextConverter.convert(ctx));
			LOG.debug("Template {} successfully parsed with context {}. Result:", templatePath, ctx);
			LOG.debug(result);
			return new ParsedContent(resolver.resolve(templatePath), ctx, result);
		} catch (TemplateEngineException | TemplateRuntimeException e) {
			throw new ParseException("Failed to parse template with thymeleaf", templatePath, ctx, e);
		} catch (ContextException e) {
			throw new ParseException("Failed to parse template with thymeleaf due to conversion error", templatePath, ctx, e);
		}
	}

	@Override
	public String toString() {
		return "ThymeleafParser";
	}
	
}
