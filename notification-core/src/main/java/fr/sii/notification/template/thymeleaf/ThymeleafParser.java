package fr.sii.notification.template.thymeleaf;

import org.thymeleaf.TemplateEngine;

import fr.sii.notification.core.exception.template.ContextException;
import fr.sii.notification.core.exception.template.ParseException;
import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.message.content.StringContent;
import fr.sii.notification.core.template.context.Context;
import fr.sii.notification.core.template.parser.TemplateParser;

/**
 * Implementation for Thymeleaf template engine.
 * 
 * @author Aurélien Baudet
 *
 */
public class ThymeleafParser implements TemplateParser {
	/**
	 * Thymeleaf engine
	 */
	private TemplateEngine engine;
	
	/**
	 * A resolver that provides real instances according to lookup prefixes
	 */
	private ThymeleafLookupMappingResolver lookupResolver;
	
	/**
	 * Converts general context into Thymeleaf specific context
	 */
	private ThymeleafContextConverter contextConverter;
	
	public ThymeleafParser(TemplateEngine engine, ThymeleafLookupMappingResolver lookupResolver, ThymeleafContextConverter contextConverter) {
		super();
		this.engine = engine;
		this.lookupResolver = lookupResolver;
		this.contextConverter = contextConverter;
	}

	public ThymeleafParser(TemplateEngine engine, ThymeleafLookupMappingResolver lookupResolver) {
		this(engine, lookupResolver, new ThymeleafContextConverter());
	}
	
	@Override
	public Content parse(String templateName, Context ctx) throws ParseException {
		try {
			// TODO: how to use multiple resolvers ?? Need to have one engine by resolver ??
			if(!engine.isInitialized()) {
				engine.setTemplateResolver(lookupResolver.getResolver(templateName));
			}
			String resolvedTemplateName = lookupResolver.getTemplateName(templateName);
			String result = engine.process(resolvedTemplateName, contextConverter.convert(ctx));
			return new StringContent(result);
		} catch (ContextException e) {
			throw new ParseException("failed to parse template with thymeleaf", templateName, ctx);
		}
	}

}
