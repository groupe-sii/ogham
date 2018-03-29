package fr.sii.ogham.template.freemarker;

import java.io.IOException;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.template.ContextException;
import fr.sii.ogham.core.exception.template.ParseException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.StringContent;
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

	private Configuration configuration;

	public FreeMarkerParser(Configuration configuration) {
		super();
		this.configuration = configuration;
	}

	@Override
	public Content parse(String templateName, Context ctx) throws ParseException {
		LOG.debug("Parsing FreeMarker template {} with context {}...", templateName, ctx);

		try {
			Template template = configuration.getTemplate(templateName);
			if (ctx instanceof LocaleContext) {
				template.setLocale(((LocaleContext) ctx).getLocale());
			}
			StringWriter out = new StringWriter();
			template.process(ctx.getVariables(), out);

			LOG.debug("Template {} successfully parsed with context {}. Result:", templateName, ctx);
			String templateString = out.toString();
			LOG.debug("{}", templateString);
			return new StringContent(templateString);

		} catch (IOException | TemplateException e) {
			throw new ParseException("Failed to parse template with FreeMarker", templateName, ctx, e);
		} catch (ContextException e) {
			throw new ParseException("Failed to parse template with FreeMarker due to conversion error", templateName, ctx, e);

		}
	}

	@Override
	public String toString() {
		return "FremarkerParser";
	}

}
