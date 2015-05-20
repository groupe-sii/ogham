package fr.sii.notification.core.template.detector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A decorator for template engine detection that relies on other template
 * engine detectors. It implements the template design pattern to simplify
 * algorithms with interaction of multiple detectors.
 * 
 * @author Aurélien Baudet
 *
 */
public abstract class CompositeTemplateDetector implements TemplateEngineDetector {

	/**
	 * The list of real detectors
	 */
	protected List<TemplateEngineDetector> detectors;

	/**
	 * Initialize the composite detector with none, one or several detector
	 * implementations.
	 * 
	 * @param detectors
	 *            the real template engine detector implementations
	 */
	public CompositeTemplateDetector(TemplateEngineDetector... detectors) {
		this(new ArrayList<>(Arrays.asList(detectors)));
	}

	/**
	 * Initialize the composite detector with the provided detector
	 * implementation list.
	 * 
	 * @param detectors
	 *            the real template engine detector implementations
	 */
	public CompositeTemplateDetector(List<TemplateEngineDetector> detectors) {
		super();
		this.detectors = detectors;
	}

	/**
	 * Register a new template engine detector. The detector is added at the end
	 * and will be used only if none of the previous detectors could handle the
	 * template.
	 * 
	 * @param detector
	 *            the template engine detector to register
	 */
	public void addDetector(TemplateEngineDetector detector) {
		detectors.add(detector);
	}

}