package fr.sii.ogham.core.template.detector;

import fr.sii.ogham.core.exception.template.EngineDetectionException;
import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.template.context.Context;

/**
 * Defines the contract for template engine detection. The implementations
 * indicate if the template can be handle by the associated template engine
 * parser. The detection can be based on:
 * <ul>
 * <li>on the template name</li>
 * <li>on the template variable substitutions</li>
 * <li>on the template content</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public interface TemplateEngineDetector {
	/**
	 * Indicates if the template can be parsed or not.
	 * 
	 * @param template
	 *            the path to the template
	 * @param ctx
	 *            the variable substitutions
	 * @return true if the engine can parse the template, false otherwise
	 * @throws EngineDetectionException
	 *             when something went wrong during detection
	 */
	public boolean canParse(ResourcePath template, Context ctx) throws EngineDetectionException;
}
