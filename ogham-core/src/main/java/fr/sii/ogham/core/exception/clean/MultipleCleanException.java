package fr.sii.ogham.core.exception.clean;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;
import static java.util.Collections.unmodifiableList;

import java.util.List;

/**
 * Some resources may be opened while Ogham runs. That's why there is a cleanup
 * mechanism to free/close some resources. Clean-up may fail for any reason.
 * 
 * This is a wrapper exception used when performing a full cleanup. It is thrown
 * when at least one cleanup has raised an error. It keeps the list of cleanup
 * failures.
 * 
 * @author Aurélien Baudet
 *
 */
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
