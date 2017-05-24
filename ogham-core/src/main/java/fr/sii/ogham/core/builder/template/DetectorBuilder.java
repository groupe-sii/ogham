package fr.sii.ogham.core.builder.template;

import fr.sii.ogham.core.template.detector.TemplateEngineDetector;

/**
 * Builder that configures detection of template engine.
 * 
 * <p>
 * The aim is to detect which template engine is able to parse a template.
 * </p>
 * 
 * The detection can be based on:
 * <ul>
 * <li>on the template name (may be the full template path)</li>
 * <li>on the template variable substitutions</li>
 * <li>on the template content</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 * @param <MYSELF>
 *            The type of this instance. This is needed to have the right return
 *            type for fluent chaining with inheritance
 */
public interface DetectorBuilder<MYSELF> {
	/**
	 * Registers a detector to associate with a template parser.
	 * 
	 * The detection can be based on:
	 * <ul>
	 * <li>on the template name (may be the full template path)</li>
	 * <li>on the template variable substitutions</li>
	 * <li>on the template content</li>
	 * </ul>
	 * 
	 * @param detector
	 *            the detector to register
	 * @return this instance for fluent chaining
	 */
	MYSELF detector(TemplateEngineDetector detector);

	/**
	 * Creates and configures the detector instance.
	 * 
	 * @return the detector instance
	 */
	TemplateEngineDetector buildDetector();
}
