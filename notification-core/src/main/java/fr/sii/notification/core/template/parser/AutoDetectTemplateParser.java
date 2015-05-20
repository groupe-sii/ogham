package fr.sii.notification.core.template.parser;

import java.util.Map;
import java.util.Map.Entry;

import fr.sii.notification.core.exception.template.EngineDetectionException;
import fr.sii.notification.core.exception.template.NoEngineDetectionException;
import fr.sii.notification.core.exception.template.ParseException;
import fr.sii.notification.core.exception.template.TemplateResolutionException;
import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.template.Template;
import fr.sii.notification.core.template.context.Context;
import fr.sii.notification.core.template.detector.TemplateEngineDetector;
import fr.sii.notification.core.template.resolver.TemplateResolver;

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
	/**
	 * The template resolver used to find the template
	 */
	private TemplateResolver resolver;

	/**
	 * The pairs of engine detector and template engine parser
	 */
	private Map<TemplateEngineDetector, TemplateParser> detectors;

	public AutoDetectTemplateParser(TemplateResolver resolver, Map<TemplateEngineDetector, TemplateParser> detectors) {
		super();
		this.resolver = resolver;
		this.detectors = detectors;
	}

	@Override
	public Content parse(String templateName, Context ctx) throws ParseException {
		try {
			Template template = resolver.getTemplate(templateName);
			TemplateParser parser = null;
			for (Entry<TemplateEngineDetector, TemplateParser> entry : detectors.entrySet()) {
				if (entry.getKey().canParse(templateName, ctx, template)) {
					parser = entry.getValue();
					break;
				}
			}
			if (parser == null) {
				throw new NoEngineDetectionException("Auto detection couldn't find any parser able to handle the template " + templateName);
			}
			return parser.parse(templateName, ctx);
		} catch (TemplateResolutionException e) {
			throw new ParseException("Failed to automatically detect parser because the template couldn't be resolved", templateName, ctx, e);
		} catch (EngineDetectionException e) {
			throw new ParseException("Failed to automatically detect parser due to detection error", templateName, ctx, e);
		}
	}

}
