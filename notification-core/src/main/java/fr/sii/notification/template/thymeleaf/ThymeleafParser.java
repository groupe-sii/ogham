package fr.sii.notification.template.thymeleaf;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.notification.core.exception.ContextException;
import fr.sii.notification.core.exception.ParseException;
import fr.sii.notification.core.exception.TemplateResolutionException;
import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.message.content.StringContent;
import fr.sii.notification.core.template.context.Context;
import fr.sii.notification.core.template.parser.TemplateParser;

public class ThymeleafParser implements TemplateParser {
	private TemplateEngine engine;
	private ThymeleafLookupMappingResolver<ITemplateResolver> lookupResolver;
	
	public ThymeleafParser(TemplateEngine engine, ThymeleafLookupMappingResolver<ITemplateResolver> lookupResolver) {
		super();
		this.engine = engine;
		this.lookupResolver = lookupResolver;
	}

	@Override
	public Content parse(String templateName, Context ctx) throws ParseException {
		try {
			engine.setTemplateResolver(lookupResolver.getResolver(templateName));
			return new StringContent(engine.process(lookupResolver.getTemplateName(templateName), new ThymeleafContextConverter().convert(ctx)));
		} catch (ContextException | TemplateResolutionException e) {
			throw new ParseException("failed to parse template with thymeleaf", templateName, ctx);
		}
	}

}
