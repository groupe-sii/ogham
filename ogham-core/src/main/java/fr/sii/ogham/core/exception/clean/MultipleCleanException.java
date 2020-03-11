package fr.sii.ogham.core.exception.clean;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;
import static java.util.Collections.unmodifiableList;

import java.util.List;

public class MultipleCleanException extends CleanException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final List<CleanException> causes;
	
	public MultipleCleanException(String message, List<CleanException> causes) {
		super(message);
		this.causes = unmodifiableList(causes);
	}

	public List<CleanException> getCauses() {
		return causes;
	}
	
}
