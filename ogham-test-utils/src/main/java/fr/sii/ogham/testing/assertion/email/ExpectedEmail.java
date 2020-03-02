package fr.sii.ogham.testing.assertion.email;


/**
 * Class used in tests for ensuring that the email is respected. It provides the
 * following information:
 * <ul>
 * <li>The expected subject</li>
 * <li>The expected body</li>
 * <li>The expected sender address</li>
 * <li>The expected recipients (to, cc, bcc)</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 * @see ExpectedEmailHeader
 */
public class ExpectedEmail extends ExpectedEmailHeader {
	/**
	 * The expected body content
	 */
	private ExpectedContent expectedContent;

	/**
	 * Initialize the expected email with string values. The body is used as-is
	 * and is expected to provide a "text/plain" Mime Type.
	 * 
	 * @param subject
	 *            the expected subject of the email
	 * @param body
	 *            the expected body of the email in plain text
	 * @param from
	 *            the expected email sender address
	 * @param to
	 *            the expected recipients
	 */
	public ExpectedEmail(String subject, String body, String from, String... to) {
		this(subject, new ExpectedContent(body, "text/plain.*"), from, to);
	}

	/**
	 * Initialize the expected email with provided values.
	 * 
	 * @param subject
	 *            the expected subject of the email
	 * @param expectedContent
	 *            the expected body of the email with the expected Mime Type
	 * @param from
	 *            the expected email sender address
	 * @param to
	 *            the expected recipients
	 */
	public ExpectedEmail(String subject, ExpectedContent expectedContent, String from, String... to) {
		super(subject, from, to);
		this.expectedContent = expectedContent;
	}

	public ExpectedContent getExpectedContent() {
		return expectedContent;
	}
}
