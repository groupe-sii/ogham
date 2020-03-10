package fr.sii.ogham.testing.assertion.email;

import static fr.sii.ogham.testing.assertion.util.EmailUtils.getBodyParts;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyList;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

import org.junit.Assert;
import org.junit.ComparisonFailure;

import fr.sii.ogham.testing.assertion.html.AssertHtml;
import fr.sii.ogham.testing.assertion.util.AssertionRegistry;
import fr.sii.ogham.testing.assertion.util.EmailUtils;
import fr.sii.ogham.testing.assertion.util.Executable;
import fr.sii.ogham.testing.assertion.util.FailAtEndRegistry;

/**
 * Utility class for checking if the received email content is as expected.
 * 
 * @author Aurélien Baudet
 *
 */
@SuppressWarnings("squid:S1192")
public final class AssertEmail {
	private static final Pattern HTML_PATTERN = Pattern.compile("<html", Pattern.CASE_INSENSITIVE);
	private static final Pattern NEW_LINES = Pattern.compile("\r|\n");

	/**
	 * Assert that the fields of the received email are equal to the expected
	 * values. The expected email contains several parts (several contents). The
	 * received email should also have the equivalent parts. It will check that:
	 * <ul>
	 * <li>The received headers are respected (see
	 * {@link #assertHeaders(ExpectedEmailHeader, Message)})</li>
	 * <li>The received message is a {@link Multipart} email</li>
	 * <li>The number of parts are equal</li>
	 * <li>Each received part body equals the expected one (order is important).
	 * See {@link #assertBody(String, String, boolean)}</li>
	 * <li>Each received part Mime Type equals the expected one (order is
	 * important).</li>
	 * </ul>
	 * <p>
	 * The checking of the body is done strictly (totally equal).
	 * </p>
	 * 
	 * @param expectedEmail
	 *            all the fields with their expected values
	 * @param actualEmail
	 *            the received email
	 * @throws MessagingException
	 *             when accessing the received email fails
	 * @throws IOException
	 *             when reading the content of the email fails
	 */
	public static void assertEquals(ExpectedMultiPartEmail expectedEmail, Message actualEmail) throws MessagingException, IOException {
		AssertionRegistry assertions = new FailAtEndRegistry();
		assertEquals(expectedEmail, actualEmail, true, assertions);
		assertions.execute();
	}

	/**
	 * <p>
	 * Shortcut to simplify unit testing with GreenMail. See
	 * {@link #assertEquals(ExpectedMultiPartEmail[], Message[])}.
	 * </p>
	 * Assert that the fields of the received email are equal to the expected
	 * values. The expected email contains several parts (several contents). The
	 * received email should also have the equivalent parts. It will check that:
	 * <ul>
	 * <li>The received headers are respected (see
	 * {@link #assertHeaders(ExpectedEmailHeader, Message)})</li>
	 * <li>The received message is a {@link Multipart} email</li>
	 * <li>The number of parts are equal</li>
	 * <li>Each received part body equals the expected one (order is important).
	 * See {@link #assertBody(String, String, boolean)}</li>
	 * <li>Each received part Mime Type equals the expected one (order is
	 * important).</li>
	 * </ul>
	 * <p>
	 * The checking of the body is done strictly (totally equal).
	 * </p>
	 * 
	 * @param expectedEmail
	 *            all the fields with their expected values
	 * @param actualEmails
	 *            the received email
	 * @throws MessagingException
	 *             when accessing the received email fails
	 * @throws IOException
	 *             when reading the content of the email fails
	 */
	public static void assertEquals(ExpectedMultiPartEmail expectedEmail, Message[] actualEmails) throws MessagingException, IOException {
		assertEquals(new ExpectedMultiPartEmail[] { expectedEmail }, actualEmails);
	}

	/**
	 * Assert that each received email content respects the expected one. It
	 * ensures that the number of received emails equals to the expected number.
	 * Then for each email it calls
	 * {@link #assertEquals(ExpectedMultiPartEmail, Message)} .
	 * 
	 * @param expectedEmails
	 *            the list of expected emails
	 * @param actualEmails
	 *            the received emails
	 * @throws MessagingException
	 *             when accessing the received email fails
	 * @throws IOException
	 *             when reading the content of the email fails
	 */
	public static void assertEquals(ExpectedMultiPartEmail[] expectedEmails, Message[] actualEmails) throws MessagingException, IOException {
		AssertionRegistry assertions = new FailAtEndRegistry();
		assertions.register(() -> Assert.assertEquals("should have received " + expectedEmails.length + " email", expectedEmails.length, actualEmails.length));
		for (int i = 0; i < expectedEmails.length; i++) {
			assertEquals(expectedEmails[i], i < actualEmails.length ? actualEmails[i] : null, true, assertions);
		}
		assertions.execute();
	}

	/**
	 * <p>
	 * Shortcut to simplify unit testing with GreenMail. See
	 * {@link #assertEquals(ExpectedEmail[], Message[])}.
	 * </p>
	 * Assert that the fields of the received email are equal to the expected
	 * values. The expected email contains only one part (only one content). It
	 * will check that:
	 * <ul>
	 * <li>The received headers are respected (see
	 * {@link #assertHeaders(ExpectedEmailHeader, Message)})</li>
	 * <li>The body equals the expected one (order is important). See
	 * {@link #assertBody(String, String, boolean)}</li>
	 * <li>The Mime Type equals the expected one (order is important).</li>
	 * </ul>
	 * <p>
	 * The checking of the body is done strictly (totally equal).
	 * </p>
	 * 
	 * 
	 * @param expectedEmail
	 *            all the fields with their expected values
	 * @param actualEmails
	 *            the received emails
	 * @throws MessagingException
	 *             when accessing the received email fails
	 * @throws IOException
	 *             when reading the email content fails
	 */
	public static void assertEquals(ExpectedEmail expectedEmail, Message[] actualEmails) throws MessagingException, IOException {
		assertEquals(new ExpectedEmail[] { expectedEmail }, actualEmails);
	}

	/**
	 * Assert that each received email content respects the expected one. It
	 * ensures that the number of received emails equals to the expected number.
	 * Then for each email it calls
	 * {@link #assertEquals(ExpectedEmail, Message)} .
	 * 
	 * @param expectedEmail
	 *            the expected email
	 * @param actualEmails
	 *            the received emails
	 * @throws MessagingException
	 *             when accessing the received email fails
	 * @throws IOException
	 *             when reading the email content fails
	 */
	public static void assertEquals(ExpectedEmail[] expectedEmail, Message[] actualEmails) throws MessagingException, IOException {
		AssertionRegistry assertions = new FailAtEndRegistry();
		assertions.register(() -> Assert.assertEquals("should have received " + expectedEmail.length + " email", expectedEmail.length, actualEmails.length));
		for (int i = 0; i < expectedEmail.length; i++) {
			assertEquals(expectedEmail[i], i < actualEmails.length ? actualEmails[i] : null, true, assertions);
		}
		assertions.execute();
	}

	/**
	 * Assert that the fields of the received email are equal to the expected
	 * values. The expected email contains only one part (only one content). It
	 * will check that:
	 * <ul>
	 * <li>The received headers are respected (see
	 * {@link #assertHeaders(ExpectedEmailHeader, Message)})</li>
	 * <li>The body equals the expected one (order is important). See
	 * {@link #assertBody(String, String, boolean)}</li>
	 * <li>The Mime Type equals the expected one (order is important).</li>
	 * </ul>
	 * <p>
	 * The checking of the body is done strictly (totally equal).
	 * </p>
	 * 
	 * @param expectedEmail
	 *            all the fields with their expected values
	 * @param actualEmail
	 *            the received email
	 * @throws MessagingException
	 *             when accessing the received email fails
	 * @throws IOException
	 *             when reading the email content fails
	 */
	public static void assertEquals(ExpectedEmail expectedEmail, Message actualEmail) throws MessagingException, IOException {
		AssertionRegistry assertions = new FailAtEndRegistry();
		assertEquals(expectedEmail, actualEmail, true, assertions);
		assertions.execute();
	}

	/**
	 * Assert that the fields of the received email are equal to the expected
	 * values. The expected email contains several parts (several contents). The
	 * received email should also have the equivalent parts. It will check that:
	 * <ul>
	 * <li>The received headers are respected (see
	 * {@link #assertHeaders(ExpectedEmailHeader, Message)})</li>
	 * <li>The received message is a {@link Multipart} email</li>
	 * <li>The number of parts are equal</li>
	 * <li>Each received part body equals the expected one (order is important).
	 * See {@link #assertBody(String, String, boolean)}</li>
	 * <li>Each received part Mime Type equals the expected one (order is
	 * important).</li>
	 * </ul>
	 * <p>
	 * The checking of the body ignores the new line characters.
	 * </p>
	 * 
	 * @param expectedEmail
	 *            all the fields with their expected values
	 * @param actualEmail
	 *            the received email
	 * @throws MessagingException
	 *             when accessing the received email fails
	 * @throws IOException
	 *             when reading the content of the email fails
	 */
	public static void assertSimilar(ExpectedMultiPartEmail expectedEmail, Message actualEmail) throws MessagingException, IOException {
		AssertionRegistry assertions = new FailAtEndRegistry();
		assertEquals(expectedEmail, actualEmail, false, assertions);
		assertions.execute();
	}

	/**
	 * <p>
	 * Shortcut to simplify unit testing with GreenMail. See
	 * {@link #assertSimilar(ExpectedMultiPartEmail[], Message[])}.
	 * </p>
	 * Assert that the fields of the received email are equal to the expected
	 * values. The expected email contains several parts (several contents). The
	 * received email should also have the equivalent parts. It will check that:
	 * <ul>
	 * <li>The received headers are respected (see
	 * {@link #assertHeaders(ExpectedEmailHeader, Message)})</li>
	 * <li>The received message is a {@link Multipart} email</li>
	 * <li>The number of parts are equal</li>
	 * <li>Each received part body equals the expected one (order is important).
	 * See {@link #assertBody(String, String, boolean)}</li>
	 * <li>Each received part Mime Type equals the expected one (order is
	 * important).</li>
	 * </ul>
	 * <p>
	 * The checking of the body ignores the new line characters.
	 * </p>
	 * 
	 * @param expectedEmail
	 *            all the fields with their expected values
	 * @param actualEmails
	 *            the received email
	 * @throws MessagingException
	 *             when accessing the received email fails
	 * @throws IOException
	 *             when reading the content of the email fails
	 */
	public static void assertSimilar(ExpectedMultiPartEmail expectedEmail, Message[] actualEmails) throws MessagingException, IOException {
		assertSimilar(new ExpectedMultiPartEmail[] { expectedEmail }, actualEmails);
	}

	/**
	 * Assert that each received email content respects the expected one. It
	 * ensures that the number of received emails equals to the expected number.
	 * Then for each email it calls
	 * {@link #assertSimilar(ExpectedMultiPartEmail, Message)} .
	 * 
	 * @param expectedEmails
	 *            the list of expected emails
	 * @param actualEmails
	 *            the received emails
	 * @throws MessagingException
	 *             when accessing the received email fails
	 * @throws IOException
	 *             when reading the content of the email fails
	 */
	public static void assertSimilar(ExpectedMultiPartEmail[] expectedEmails, Message[] actualEmails) throws MessagingException, IOException {
		AssertionRegistry assertions = new FailAtEndRegistry();
		assertions.register(() -> Assert.assertEquals("should have received " + expectedEmails.length + " email", expectedEmails.length, actualEmails.length));
		for (int i = 0; i < expectedEmails.length; i++) {
			assertEquals(expectedEmails[i], i < actualEmails.length ? actualEmails[i] : null, false, assertions);
		}
		assertions.execute();
	}

	/**
	 * <p>
	 * Shortcut to simplify unit testing with GreenMail. See
	 * {@link #assertSimilar(ExpectedEmail[], Message[])}.
	 * </p>
	 * Assert that the fields of the received email are equal to the expected
	 * values. The expected email contains only one part (only one content). It
	 * will check that:
	 * <ul>
	 * <li>The received headers are respected (see
	 * {@link #assertHeaders(ExpectedEmailHeader, Message)})</li>
	 * <li>The body equals the expected one (order is important). See
	 * {@link #assertBody(String, String, boolean)}</li>
	 * <li>The Mime Type equals the expected one (order is important).</li>
	 * </ul>
	 * <p>
	 * The checking of the body ignores the new line characters.
	 * </p>
	 * 
	 * 
	 * @param expectedEmail
	 *            all the fields with their expected values
	 * @param actualEmails
	 *            the received emails
	 * @throws MessagingException
	 *             when accessing the received email fails
	 * @throws IOException
	 *             when reading the email content fails
	 */
	public static void assertSimilar(ExpectedEmail expectedEmail, Message[] actualEmails) throws MessagingException, IOException {
		assertSimilar(new ExpectedEmail[] { expectedEmail }, actualEmails);
	}

	/**
	 * Assert that each received email content respects the expected one. It
	 * ensures that the number of received emails equals to the expected number.
	 * Then for each email it calls
	 * {@link #assertSimilar(ExpectedEmail, Message)} .
	 * 
	 * @param expectedEmail
	 *            the expected email
	 * @param actualEmails
	 *            the received emails
	 * @throws MessagingException
	 *             when accessing the received email fails
	 * @throws IOException
	 *             when reading the email content fails
	 */
	public static void assertSimilar(ExpectedEmail[] expectedEmail, Message[] actualEmails) throws MessagingException, IOException {
		AssertionRegistry assertions = new FailAtEndRegistry();
		assertions.register(() -> Assert.assertEquals("should have received " + expectedEmail.length + " email", expectedEmail.length, actualEmails.length));
		for (int i = 0; i < expectedEmail.length; i++) {
			assertEquals(expectedEmail[i], i < actualEmails.length ? actualEmails[i] : null, false, assertions);
		}
		assertions.execute();
	}

	/**
	 * Assert that the fields of the received email are equal to the expected
	 * values. The expected email contains only one part (only one content). It
	 * will check that:
	 * <ul>
	 * <li>The received headers are respected (see
	 * {@link #assertHeaders(ExpectedEmailHeader, Message)})</li>
	 * <li>The body equals the expected one (order is important). See
	 * {@link #assertBody(String, String, boolean)}</li>
	 * <li>The Mime Type equals the expected one (order is important).</li>
	 * </ul>
	 * <p>
	 * The checking of the body ignores the new line characters.
	 * </p>
	 * 
	 * @param expectedEmail
	 *            all the fields with their expected values
	 * @param actualEmail
	 *            the received email
	 * @throws MessagingException
	 *             when accessing the received email fails
	 * @throws IOException
	 *             when reading the email content fails
	 */
	public static void assertSimilar(ExpectedEmail expectedEmail, Message actualEmail) throws MessagingException, IOException {
		AssertionRegistry assertions = new FailAtEndRegistry();
		assertEquals(expectedEmail, actualEmail, false, assertions);
		assertions.execute();
	}

	/**
	 * Checks if the received body equals the expected body. It handles HTML
	 * content and pure text content.
	 * <p>
	 * For text, the check can be done either strictly (totally equal) or not
	 * (ignore new lines).
	 * </p>
	 * <p>
	 * For HTML, the string content is parsed and DOM trees are compared. The
	 * comparison can be done either strictly (DOM trees are totally equals,
	 * attributes are in the same order) or not (attributes can be in any
	 * order).
	 * </p>
	 * 
	 * @param expectedBody
	 *            the expected content as string
	 * @param actualBody
	 *            the received content as string
	 * @param strict
	 *            true for strict checking (totally equals) or false to ignore
	 *            new line characters
	 */
	public static void assertBody(String expectedBody, String actualBody, boolean strict) {
		AssertionRegistry assertions = new FailAtEndRegistry();
		assertBody("body", expectedBody, actualBody, strict, assertions);
		assertions.execute();
	}

	/**
	 * Checks if the received headers corresponds to the expected header values.
	 * It checks if:
	 * <ul>
	 * <li>The received subject equals the expected subject</li>
	 * <li>The email sender address equals the expected sender address</li>
	 * <li>The total number of recipients equals the total number of expected
	 * recipients</li>
	 * <li>The number of "to" recipients equals the number of expected "to"
	 * recipients</li>
	 * <li>The number of "cc" recipients equals the number of expected "cc"
	 * recipients</li>
	 * <li>The number of "bcc" recipients equals the number of expected "bcc"
	 * recipients</li>
	 * <li>Each "to" recipient equals the expected "to" recipient value</li>
	 * <li>Each "cc" recipient equals the expected "cc" recipient value</li>
	 * <li>Each "bcc" recipient equals the expected "bcc" recipient value</li>
	 * </ul>
	 * 
	 * @param expectedEmail
	 *            the expected header values
	 * @param actualEmail
	 *            the received header values
	 * @throws MessagingException
	 *             when accessing the received email fails
	 */
	public static void assertHeaders(ExpectedEmailHeader expectedEmail, Message actualEmail) throws MessagingException {
		AssertionRegistry assertions = new FailAtEndRegistry();
		assertHeaders(expectedEmail, actualEmail, assertions);
		assertions.execute();
	}

	/**
	 * Checks if the received headers corresponds to the expected header values.
	 * It checks if:
	 * <ul>
	 * <li>The number of recipients of the provided type equals the number of
	 * expected recipients of the provided type</li>
	 * <li>Each recipient of the provided type equals the expected recipient
	 * value of the provided type</li>
	 * </ul>
	 * 
	 * @param expectedRecipients
	 *            the list of recipient string values
	 * @param actualEmail
	 *            the received header values
	 * @param recipientType
	 *            the type of the recipient to compare
	 * @throws MessagingException
	 *             when accessing the received email fails
	 */
	public static void assertRecipients(List<String> expectedRecipients, Message actualEmail, RecipientType recipientType) throws MessagingException {
		AssertionRegistry assertions = new FailAtEndRegistry();
		assertRecipients(expectedRecipients, actualEmail, recipientType, assertions);
		assertions.execute();
	}

	private static void assertEquals(ExpectedEmail expectedEmail, Message actualEmail, boolean strict, AssertionRegistry assertions) throws MessagingException, IOException {
		assertHeaders(expectedEmail, actualEmail, assertions);
		assertions.register(() -> Assert.assertNotNull("Expected at least one body part but none found", getBodyOrNull(actualEmail, assertions)));
		assertBody("body", expectedEmail.getExpectedContent().getBody(), getBodyContentOrNull(actualEmail, assertions), strict, assertions);
		assertMimetype(expectedEmail.getExpectedContent(), getBodyMimetypeOrNull(actualEmail, assertions), assertions);
	}

	private static void assertEquals(ExpectedMultiPartEmail expectedEmail, Message actualEmail, boolean strict, AssertionRegistry assertions) throws MessagingException, IOException {
		assertHeaders(expectedEmail, actualEmail, assertions);
		Object content = actualEmail == null ? null : actualEmail.getContent();
		assertions.register(() -> Assert.assertTrue("should be multipart message", content instanceof Multipart));
		List<Part> bodyParts = actualEmail == null ? emptyList() : getBodyParts(actualEmail);
		assertions.register(() -> Assert.assertEquals("should have " + expectedEmail.getExpectedContents().size() + " parts", expectedEmail.getExpectedContents().size(), bodyParts.size()));
		for (int i = 0; i < expectedEmail.getExpectedContents().size(); i++) {
			Part part = i < bodyParts.size() ? bodyParts.get(i) : null;
			assertBody("body[" + i + "]", expectedEmail.getExpectedContents().get(i).getBody(), part == null || part.getContent() == null ? null : part.getContent().toString(), strict, assertions);
			assertMimetype(expectedEmail.getExpectedContents().get(i), part == null ? null : part.getContentType(), assertions);
		}
	}

	private static void assertMimetype(ExpectedContent expectedContent, String contentType, AssertionRegistry assertions) {
		assertions.register(() -> Assert.assertTrue("mimetype should match " + expectedContent.getMimetype() + " instead of " + contentType,
				contentType != null && expectedContent.getMimetype().matcher(contentType).matches()));
	}

	private static void assertBody(String name, String expectedBody, String actualBody, boolean strict, AssertionRegistry assertions) {
		if (isHtml(expectedBody)) {
			if (strict) {
				assertions.register(() -> AssertHtml.assertEquals(expectedBody, actualBody));
			} else {
				assertions.register(() -> AssertHtml.assertSimilar(expectedBody, actualBody));
			}
		} else {
			assertions.register(() -> compareText(name, expectedBody, actualBody, strict));
		}
	}

	private static void compareText(String name, String expectedBody, String actualBody, boolean strict) throws ComparisonFailure {
		if (expectedBody == null && actualBody != null) {
			throw new ComparisonFailure(name + " should be null", expectedBody, actualBody);
		}
		if (expectedBody == null) {
			return;
		}
		if (strict ? !expectedBody.equals(actualBody) : !sanitize(expectedBody).equals(sanitize(actualBody))) {
			throw new ComparisonFailure(name + " should be '" + expectedBody + "'", expectedBody, actualBody);
		}
	}

	private static void assertHeaders(ExpectedEmailHeader expectedEmail, Message actualEmail, AssertionRegistry assertions) throws MessagingException {
		Address[] from = actualEmail == null || actualEmail.getFrom() == null ? null : actualEmail.getFrom();
		assertions.register(() -> Assert.assertEquals("subject should be '" + expectedEmail.getSubject() + "'", expectedEmail.getSubject(), actualEmail == null ? null : actualEmail.getSubject()));
		assertions.register(() -> Assert.assertEquals("should have only one from", (Integer) 1, from == null ? null : from.length));
		assertions.register(() -> Assert.assertEquals("from should be '" + expectedEmail.getFrom() + "'", expectedEmail.getFrom(), from == null ? null : from[0].toString()));
		int recipients = expectedEmail.getTo().size() + expectedEmail.getBcc().size() + expectedEmail.getCc().size();
		assertions.register(() -> Assert.assertEquals("should be received by " + recipients + " recipients", (Integer) recipients,
				actualEmail == null || actualEmail.getAllRecipients() == null ? null : actualEmail.getAllRecipients().length));
		assertRecipients(expectedEmail.getTo(), actualEmail, RecipientType.TO, assertions);
		assertRecipients(expectedEmail.getCc(), actualEmail, RecipientType.CC, assertions);
		assertRecipients(expectedEmail.getBcc(), actualEmail, RecipientType.BCC, assertions);
	}

	private static void assertRecipients(List<String> expectedRecipients, Message actualEmail, RecipientType recipientType, AssertionRegistry assertions) throws MessagingException {
		Address[] actualRecipients = actualEmail == null ? null : actualEmail.getRecipients(recipientType);
		if (expectedRecipients.isEmpty()) {
			assertions.register(() -> Assert.assertTrue("should be received by no recipients (of type RecipientType." + recipientType + ")", actualRecipients == null || actualRecipients.length == 0));
		} else {
			assertions.register(() -> Assert.assertEquals("should be received by " + expectedRecipients.size() + " recipients (of type RecipientType." + recipientType + ")",
					(Integer) expectedRecipients.size(), actualRecipients == null ? null : actualRecipients.length));
			for (int i = 0; i < expectedRecipients.size(); i++) {
				final int idx = i;
				assertions.register(() -> Assert.assertEquals("recipient " + recipientType + "[" + idx + "] should be '" + expectedRecipients.get(idx) + "'", expectedRecipients.get(idx),
						actualRecipients != null && idx < actualRecipients.length ? actualRecipients[idx].toString() : null));
			}
		}
	}

	/**
	 * Remove new lines from the string.
	 * 
	 * @param str
	 *            the string to sanitize
	 * @return the sanitized string
	 */
	private static String sanitize(String str) {
		if (str == null) {
			return null;
		}
		return NEW_LINES.matcher(str).replaceAll("");
	}

	@SuppressWarnings("squid:S2147") // false positive: merging exception
										// doesn't compile in that case or we
										// are force to throw Exception instead
										// of MessagingException
	private static Part getBodyOrNull(Part actualEmail, AssertionRegistry registry) throws MessagingException {
		try {
			if (actualEmail == null) {
				return null;
			}
			return EmailUtils.getBodyPart(actualEmail);
		} catch (MessagingException e) {
			registry.register(failure(e));
			return null;
		} catch (IllegalStateException e) {
			registry.register(failure(e));
			return null;
		}
	}

	@SuppressWarnings("squid:S2147") // false positive: merging exception
										// doesn't compile in that case or we
										// are force to throw Exception instead
										// of MessagingException
	private static String getBodyContentOrNull(Part actualEmail, AssertionRegistry registry) throws MessagingException, IOException {
		try {
			Part bodyPart = getBodyOrNull(actualEmail, registry);
			if (bodyPart == null) {
				return null;
			}
			return EmailUtils.getContent(bodyPart, UTF_8);
		} catch (MessagingException e) {
			registry.register(failure(e));
			return null;
		} catch (IllegalStateException e) {
			registry.register(failure(e));
			return null;
		} catch (IOException e) {
			registry.register(failure(e));
			return null;
		}
	}

	private static <E extends Exception> Executable<E> failure(E exception) {
		return () -> {
			throw exception;
		};
	}

	@SuppressWarnings("squid:S2147") // false positive: merging exception
										// doesn't compile in that case or we
										// are force to throw Exception instead
										// of MessagingException
	private static String getBodyMimetypeOrNull(Part actualEmail, AssertionRegistry registry) throws MessagingException {
		try {
			Part bodyPart = getBodyOrNull(actualEmail, registry);
			if (bodyPart == null) {
				return null;
			}
			return bodyPart.getContentType();
		} catch (MessagingException e) {
			registry.register(failure(e));
			return null;
		} catch (IllegalStateException e) {
			registry.register(failure(e));
			return null;
		}
	}

	private static boolean isHtml(String expectedBody) {
		return HTML_PATTERN.matcher(expectedBody).find();
	}

	private AssertEmail() {
		super();
	}
}
