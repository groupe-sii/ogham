package fr.sii.ogham.core.exception.template;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

public class NoEngineDetectionException extends EngineDetectionException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public NoEngineDetectionException(String message) {
		super(message);
	}

}
