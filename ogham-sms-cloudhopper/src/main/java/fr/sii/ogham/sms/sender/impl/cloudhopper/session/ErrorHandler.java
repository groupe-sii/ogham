package fr.sii.ogham.sms.sender.impl.cloudhopper.session;

/**
 * Interface for error handling.
 * 
 * <p>
 * Error handling may take several forms:
 * 
 * <ul>
 * <li>Just log the error</li>
 * <li>Trigger some action</li>
 * <li>Skip the error</li>
 * <li>...</li>
 * </ul>
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public interface ErrorHandler {
	/**
	 * Handle an error. The handling may differ according the the failure.
	 * 
	 * @param failure
	 *            the error to handle
	 */
	void handleFailure(Throwable failure);
}
