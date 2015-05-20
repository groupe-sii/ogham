package fr.sii.notification.core.template.detector;

import fr.sii.notification.core.exception.template.EngineDetectionException;
import fr.sii.notification.core.template.Template;
import fr.sii.notification.core.template.context.Context;

/**
 * Most basic detector that always provide the same detection value. None of the
 * information (template name, context or template content) is used. The value
 * is set at construction and never changes.
 * 
 * @author Aurélien Baudet
 *
 */
public class FixedEngineDetector implements TemplateEngineDetector {

	/**
	 * The value to return every time
	 * {@link #canParse(String, Context, Template)} is called
	 */
	private boolean canParse;

	/**
	 * Initialize the detector to always say yes to any template. The associated
	 * template engine will always handle all the templates.
	 */
	public FixedEngineDetector() {
		this(true);
	}

	/**
	 * Initialize the detector to always say yes or no to any template. If
	 * canParse is true, then the associated template engine will always be
	 * used. If canParse is false, then the associated template engine will
	 * never be used.
	 * 
	 * @param canParse
	 *            true to enable the associated template engine, false to
	 *            disable it
	 */
	public FixedEngineDetector(boolean canParse) {
		super();
		this.canParse = canParse;
	}

	@Override
	public boolean canParse(String templateName, Context ctx, Template template) throws EngineDetectionException {
		return canParse;
	}

}
