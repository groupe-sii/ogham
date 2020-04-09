package fr.sii.ogham.core.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;
import static java.util.Collections.unmodifiableList;

import java.util.List;
import java.util.stream.Collectors;

import fr.sii.ogham.core.util.ExceptionUtils;

public class MultipleCauseExceptionWrapper extends Exception {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final List<Exception> causes;

	public MultipleCauseExceptionWrapper(String message, List<Exception> causes) {
		super(message);
		this.causes = unmodifiableList(causes);
	}

	public MultipleCauseExceptionWrapper(List<Exception> causes) {
		super(toCauseString(causes));
		this.causes = unmodifiableList(causes);
	}

	public List<Exception> getCauses() {
		return causes;
	}
	
	private static String toCauseString(List<Exception> causes) {
		return causes.stream()
				.map(ExceptionUtils::toString)
				.collect(Collectors.joining("\n- ", "List of original failures:\n- ", "\n"));
	}
}
