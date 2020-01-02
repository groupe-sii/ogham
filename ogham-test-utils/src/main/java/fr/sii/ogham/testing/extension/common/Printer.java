package fr.sii.ogham.testing.extension.common;

/**
 * Simple abstraction useful to switch printer implementation.
 * 
 * @author Aurélien Baudet
 *
 */
public interface Printer {
	/**
	 * Write the header string with the associated marker.
	 * 
	 * @param marker
	 *            the marker
	 * @param header
	 *            the header
	 */
	void printHeader(String marker, String header);

	/**
	 * Write the footer in case of a succeeded test.
	 * 
	 * @param marker
	 *            the marker
	 * @param success
	 *            the footer
	 */
	void printSucess(String marker, String success);

	/**
	 * Write the footer in case of a failed test.
	 * 
	 * @param marker
	 *            the marker
	 * @param failure
	 *            the footer
	 * @param e
	 *            the exception
	 */
	void printFailure(String marker, String failure, Throwable e);
}
