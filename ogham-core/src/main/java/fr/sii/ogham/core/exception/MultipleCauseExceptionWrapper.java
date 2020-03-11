package fr.sii.ogham.core.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;
import static java.util.Collections.unmodifiableList;

import java.util.List;

public class MultipleCauseExceptionWrapper extends Exception {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final List<Exception> causes;

	public MultipleCauseExceptionWrapper(String message, List<Exception> causes) {
		super(message);
		this.causes = unmodifiableList(causes);
	}

	public MultipleCauseExceptionWrapper(List<Exception> causes) {
		super();
		this.causes = unmodifiableList(causes);
	}

	public List<Exception> getCauses() {
		return causes;
	}
}
