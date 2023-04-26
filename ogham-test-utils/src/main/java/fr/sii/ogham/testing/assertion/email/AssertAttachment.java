package fr.sii.ogham.testing.assertion.email;

import static fr.sii.ogham.testing.assertion.util.EmailUtils.getAttachment;
import static fr.sii.ogham.testing.assertion.util.EmailUtils.getContent;

import fr.sii.ogham.testing.assertion.exception.MessageReadingException;
import ogham.testing.jakarta.mail.BodyPart;
import ogham.testing.jakarta.mail.Message;
import ogham.testing.jakarta.mail.MessagingException;
import ogham.testing.jakarta.mail.Multipart;

import fr.sii.ogham.testing.assertion.util.AssertionRegistry;
import fr.sii.ogham.testing.assertion.util.Executable;
import fr.sii.ogham.testing.assertion.util.FailAtEndRegistry;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.util.function.Function;

/**
 * Utility class for checking the attachment of the received email is as
 * expected.
 * 
 * @author Aurélien Baudet
 *
 */
public final class AssertAttachment {
	/**
	 * Shortcut for use with GreenMail. See
	 * {@link #assertEquals(ExpectedAttachment, Message)}.
	 * 
	 * @param expected
	 *            the expected attachment values
	 * @param actual
	 *            the received email that must contain the attachment (the array
	 *            must have only one email)
	 * @throws MessageReadingException
	 *             when access to message has failed
	 */
	public static void assertEquals(ExpectedAttachment expected, Message[] actual) throws MessageReadingException {
		AssertionRegistry registry = new FailAtEndRegistry();
		registry.register(() -> Assertions.assertEquals(1, actual.length, "should have only one message"));
		assertEquals(expected, actual.length == 1 ? actual[0] : null, registry);
		registry.execute();
	}

	/**
	 * Checks if the received email contains the expected attachment. It also
	 * ensures that the values of the attachment are respected by checking:
	 * <ul>
	 * <li>The mimetype of the attachment</li>
	 * <li>The description of the attachment</li>
	 * <li>The disposition of the attachment</li>
	 * <li>The content of the attachment</li>
	 * </ul>
	 * 
	 * @param expected
	 *            the expected attachment values
	 * @param actual
	 *            the received email that must contain the attachment
	 * @throws MessageReadingException
	 *             when access to message has failed
	 */
	public static void assertEquals(ExpectedAttachment expected, Message actual) throws MessageReadingException {
		AssertionRegistry registry = new FailAtEndRegistry();
		assertEquals(expected, actual, registry);
		registry.execute();
	}

	/**
	 * It ensures that the values of the received attachment are respected by
	 * checking:
	 * <ul>
	 * <li>The mimetype of the attachment</li>
	 * <li>The description of the attachment</li>
	 * <li>The disposition of the attachment</li>
	 * <li>The content of the attachment</li>
	 * </ul>
	 * 
	 * @param expected
	 *            the expected attachment values
	 * @param attachment
	 *            the received attachment
	 * @throws AssertionError
	 *             when there are unexpected differences
	 * @throws MessageReadingException
	 *             when access to part has failed
	 */
	public static void assertEquals(ExpectedAttachment expected, BodyPart attachment) throws MessageReadingException {
		AssertionRegistry registry = new FailAtEndRegistry();
		assertEquals(expected, attachment, registry);
		registry.execute();
	}

	private static void assertEquals(ExpectedAttachment expected, Message actual, AssertionRegistry registry) throws MessageReadingException {
		try {
			Object content = actual == null ? null : actual.getContent();
			registry.register(() -> Assertions.assertTrue(content instanceof Multipart, "should be multipart message"));
			BodyPart part = getAttachmentOrNull((Multipart) content, expected.getName(), registry);
			assertEquals(expected, part, registry);
		} catch(IOException | MessagingException e) {
			throw new MessageReadingException("Failed to read content of the message", e);
		}
	}

	private static void assertEquals(ExpectedAttachment expected, BodyPart attachment, AssertionRegistry registry) throws MessageReadingException {
		// @formatter:off
		try {
			String prefix = "attachment named '" + expected.getName() + "'" + (attachment==null ? " (/!\\ not found)" : "");
			String contentType = attachment == null || attachment.getContentType()==null ? null : attachment.getContentType();
			registry.register(() -> Assertions.assertTrue(contentType!=null && expected.getMimetype().matcher(contentType).matches(),
					prefix + " mimetype should match '" + expected.getMimetype() + "' but was " + (contentType==null ? "null" : "'" + contentType + "'")));
			registry.register(() -> Assertions.assertEquals(expected.getDescription(),
					attachment == null ? null : attachment.getDescription(),
					prefix + " description should be '" + expected.getDescription() + "'"));
			registry.register(() -> Assertions.assertEquals(expected.getDisposition(),
					attachment == null ? null : attachment.getDisposition(),
					prefix + " disposition should be '" + expected.getDisposition() + "'"));
			registry.register(() -> Assertions.assertArrayEquals(expected.getContent(),
					attachment == null ? null : getContent(attachment),
					prefix + " has invalid content"));
		} catch(Exception e) {
			throw new MessageReadingException("Failed to make assertions on attachment", e);
		}
		// @formatter:on
	}

	@SuppressWarnings("squid:S2147") // false positive: merging exception
										// doesn't compile in that case or we
										// are force to throw Exception instead
										// of MessagingException
	private static BodyPart getAttachmentOrNull(Multipart multipart, final String filename, AssertionRegistry registry) throws MessageReadingException {
		try {
			return getAttachment(multipart, filename);
		} catch (MessagingException e) {
			registerAndWrap(registry, e, (ex) -> new MessageReadingException("Failed to get attachment "+filename, ex));
			return null;
		} catch (IllegalStateException e) {
			registry.register(failure(e));
			return null;
		}
	}

	private static <E extends Exception> Executable<E> failure(E exception) {
		return () -> {
			throw exception;
		};
	}

	private static <E extends Exception> void registerAndWrap(AssertionRegistry registry, E originalException, Function<Exception, MessageReadingException> wrapper) throws MessageReadingException {
		try {
			registry.register(failure(originalException));
		} catch (Exception e) {
			throw wrapper.apply(e);
		}
	}

	private AssertAttachment() {
		super();
	}
}
