package fr.sii.ogham.core.template.parser;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.template.EngineDetectionException;
import fr.sii.ogham.core.exception.template.NoEngineDetectionException;
import fr.sii.ogham.core.exception.template.ParseException;
import fr.sii.ogham.core.message.content.Content;
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
	 * The pairs of engine detector and template engine parser
	 */
	private List<TemplateImplementation> implementations;

	public AutoDetectTemplateParser(List<TemplateImplementation> implementations) {
		super();
		this.implementations = implementations;
	}

	@Override
	public Content parse(String templateName, Context ctx) throws ParseException {
		try {
			LOG.info("Start template engine automatic detection for {}", templateName);
			TemplateParser parser = null;
			for (TemplateImplementation impl : implementations) {
				if (impl.getDetector().canParse(templateName, ctx)) {
					LOG.debug("Template engine {} is used for {}", parser, templateName);
					parser = impl.getParser();
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
		} catch (EngineDetectionException e) {
			throw new ParseException("Failed to automatically detect parser due to detection error", templateName, ctx, e);
		}
	}

	public static class TemplateImplementation {
		private final TemplateEngineDetector detector;
		private final TemplateParser parser;
		public TemplateImplementation(TemplateEngineDetector detector, TemplateParser parser) {
			super();
			this.detector = detector;
			this.parser = parser;
		}
		public TemplateEngineDetector getDetector() {
			return detector;
		}
		public TemplateParser getParser() {
			return parser;
		}
	}
}
