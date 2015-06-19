package fr.sii.ogham.core.template.parser;

import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.exception.template.EngineDetectionException;
import fr.sii.ogham.core.exception.template.NoEngineDetectionException;
import fr.sii.ogham.core.exception.template.ParseException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.template.context.Context;
import fr.sii.ogham.core.template.detector.TemplateEngineDetector;

/**
 * Decorator that automatically detects the template engine parser to use. The
 * auto-detection is based on pairs of engine detector and associated template
 * engine parser.
 * 
 * The detection mechanism loop through the engine detectors until one indicates
 * that the associated engine can parse the template.
 * 
 * @author Aurélien Baudet
 *
 */
public class AutoDetectTemplateParser implements TemplateParser {
	private static final Logger LOG = LoggerFactory.getLogger(AutoDetectTemplateParser.class);
	
	/**
	 * The template resolver used to find the template
	 */
	private ResourceResolver resolver;

	/**
	 * The pairs of engine detector and template engine parser
	 */
	private Map<TemplateEngineDetector, TemplateParser> detectors;

	public AutoDetectTemplateParser(ResourceResolver resolver, Map<TemplateEngineDetector, TemplateParser> detectors) {
		super();
		this.resolver = resolver;
		this.detectors = detectors;
	}

	@Override
	public Content parse(String templateName, Context ctx) throws ParseException {
		try {
			LOG.info("Start template engine automatic detection for {}", templateName);
			Resource template = resolver.getResource(templateName);
			TemplateParser parser = null;
			for (Entry<TemplateEngineDetector, TemplateParser> entry : detectors.entrySet()) {
				if (entry.getKey().canParse(templateName, ctx, template)) {
					LOG.debug("Template engine {} is used for {}", parser, templateName);
					parser = entry.getValue();
					break;
				} else {
					LOG.debug("Template engine {} can't be used for {}", parser, templateName);
				}
			}
			if (parser == null) {
				throw new NoEngineDetectionException("Auto detection couldn't find any parser able to handle the template " + templateName);
			}
			LOG.info("Parse the template {} using template engine {}", templateName, parser);
			return parser.parse(templateName, ctx);
		} catch (ResourceResolutionException e) {
			throw new ParseException("Failed to automatically detect parser because the template couldn't be resolved", templateName, ctx, e);
		} catch (EngineDetectionException e) {
			throw new ParseException("Failed to automatically detect parser due to detection error", templateName, ctx, e);
		}
	}

}
