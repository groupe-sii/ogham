package fr.sii.ogham.template.freemarker;

import java.io.IOException;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.template.ContextException;
import fr.sii.ogham.core.exception.template.ParseException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.ParsedContent;
import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.template.context.Context;
import fr.sii.ogham.core.template.context.LocaleContext;
import fr.sii.ogham.core.template.parser.TemplateParser;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Implementation for FreeMarker template engine.
 * 
 * @author Cyril Dejonghe
 *
 */
public class FreeMarkerParser implements TemplateParser {
	private static final Logger LOG = LoggerFactory.getLogger(FreeMarkerParser.class);

	private final Configuration configuration;
	private final ResourceResolver resolver;

	public FreeMarkerParser(Configuration configuration, ResourceResolver resolver) {
		super();
		this.configuration = configuration;
		this.resolver = resolver;
	}

	@Override
	public Content parse(ResourcePath templatePath, Context ctx) throws ParseException {
		LOG.debug("Parsing FreeMarker template {} with context {}...", templatePath, ctx);

		try {
			Template template = configuration.getTemplate(templatePath.getOriginalPath());
			if (ctx instanceof LocaleContext) {
				template.setLocale(((LocaleContext) ctx).getLocale());
			}
			StringWriter out = new StringWriter();
			template.process(ctx.getVariables(), out);

			LOG.debug("Template {} successfully parsed with context {}. Result:", templatePath, ctx);
			String templateString = out.toString();
			LOG.debug("{}", templateString);
			return new ParsedContent(resolver.resolve(templatePath), ctx, templateString);

		} catch (IOException | TemplateException e) {
			throw new ParseException("Failed to parse template with FreeMarker", templatePath, ctx, e);
		} catch (ContextException e) {
			throw new ParseException("Failed to parse template with FreeMarker due to conversion error", templatePath, ctx, e);

		}
	}

	@Override
	public String toString() {
		return "FremarkerParser";
	}

}
