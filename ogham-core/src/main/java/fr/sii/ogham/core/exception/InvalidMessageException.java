package fr.sii.ogham.core.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import java.util.HashSet;
import java.util.Set;

import fr.sii.ogham.core.message.Message;

public class InvalidMessageException extends MessageException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;
	
	private final Set<String> violations;
	
	public InvalidMessageException(String message, Message msg, Set<String> violations) {
		super(message, msg);
		this.violations = violations;
	}

	public InvalidMessageException(String message, Message msg, String violation) {
		this(message, msg, new HashSet<>());
		violations.add(violation);
	}

	public Set<String> getViolations() {
		return violations;
	}
}
