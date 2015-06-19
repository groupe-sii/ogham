package fr.sii.ogham.core.template.detector;

import java.util.List;

import fr.sii.ogham.core.exception.template.EngineDetectionException;
import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.core.template.context.Context;

/**
 * A decorator for template engine detection that relies on other template
 * engine detectors. This decorator implements a logical OR algorithm. It asks
 * each real detector if it can handle the template. If one can handle the
 * template, then it stops immediately and returns true. If none of the real
 * detectors can handle the template, then it returns false.
 * 
 * @author Aurélien Baudet
 *
 */
public class OrTemplateDetector extends CompositeTemplateDetector {

	/**
	 * Initialize the composite detector with none, one or several detector
	 * implementations.
	 * 
	 * @param detectors
	 *            the real template engine detector implementations
	 */
	public OrTemplateDetector(List<TemplateEngineDetector> detectors) {
		super(detectors);
	}

	/**
	 * Initialize the composite detector with the provided detector
	 * implementation list.
	 * 
	 * @param detectors
	 *            the real template engine detector implementations
	 */
	public OrTemplateDetector(TemplateEngineDetector... detectors) {
		super(detectors);
	}

	@Override
	public boolean canParse(String templateName, Context ctx, Resource template) throws EngineDetectionException {
		for (TemplateEngineDetector detector : getDetectors()) {
			if (detector.canParse(templateName, ctx, template)) {
				return true;
			}
		}
		return false;
	}
}
