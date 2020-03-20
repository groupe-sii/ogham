package fr.sii.ogham.sms.sender.impl.cloudhopper.session;

/**
 * Analyze an error to determine if the error can be recovered or if the error
 * is fatal and requires a new session.
 * 
 * @author Aurélien Baudet
 * @see DefaultErrorAnalyzer
 */
public interface ErrorAnalyzer {
	/**
	 * Indicates whether the raised error is severe therefore needs a new
	 * session.
	 * 
	 * @param failure
	 *            the error to analyze
	 * @return true if a new session is needed
	 */
	boolean requiresNewConnection(Throwable failure);
}
