package fr.sii.notification.helper.email;


/**
 * Class used in tests for ensuring that the email is respected. It provides the
 * following information:
 * <ul>
 * <li>The expected subject</li>
 * <li>The expected bodies (an email may have several contents)</li>
 * <li>The expected sender address</li>
 * <li>The expected recipients (to, cc, bcc)</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 * @see ExpectedEmailHeader
 */
public class ExpectedMultiPartEmail extends ExpectedEmailHeader {
	/**
	 * List of expected contents
	 */
	private ExpectedContent[] expectedContents;

	/**
	 * Initialize the expected email with provided values.
	 * 
	 * @param subject
	 *            the expected subject of the email
	 * @param bodies
	 *            the list of expected bodies of the email with their respective
	 *            expected Mime Type
	 * @param from
	 *            the expected email sender address
	 * @param to
	 *            the expected recipients
	 */
	public ExpectedMultiPartEmail(String subject, ExpectedContent[] bodies, String from, String... to) {
		super(subject, from, to);
		this.expectedContents = bodies;
	}

	public ExpectedContent[] getExpectedContents() {
		return expectedContents;
	}
}
