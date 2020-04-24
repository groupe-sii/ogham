package fr.sii.ogham.core.exception.template;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.template.detector.TemplateEngineDetector;

/**
 * Ogham is able to handle several template engines. In order to use the right
 * template engine, Ogham has an automatic engine detection mechanism.
 * 
 * This exception is thrown when the auto-detection can't be performed because
 * either no {@link TemplateEngineDetector} is registered or none of the
 * registered {@link TemplateEngineDetector}s could handle the template.
 * 
 * @author Aurélien Baudet
 *
 */
public class NoEngineDetectionException extends EngineDetectionException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public NoEngineDetectionException(String message) {
		super(message);
	}

}
