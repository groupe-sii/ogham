package fr.sii.ogham.testing.assertion.email;

import static fr.sii.ogham.testing.assertion.util.EmailUtils.getAttachment;
import static fr.sii.ogham.testing.assertion.util.EmailUtils.getContent;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;

import org.junit.Assert;
import org.junit.internal.ArrayComparisonFailure;

import fr.sii.ogham.testing.assertion.util.AssertionRegistry;
import fr.sii.ogham.testing.assertion.util.Executable;
import fr.sii.ogham.testing.assertion.util.FailAtEndRegistry;

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
	 * @throws Exception
	 *             when access to message has failed
	 */
	public static void assertEquals(ExpectedAttachment expected, Message[] actual) throws Exception {
		AssertionRegistry registry = new FailAtEndRegistry();
		registry.register(() -> Assert.assertEquals("should have only one message", 1, actual.length));
		assertEquals(expected, actual[0], registry);
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
	 * @throws Exception
	 *             when access to message has failed
	 */
	public static void assertEquals(ExpectedAttachment expected, Message actual) throws Exception {
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
	 * @throws ArrayComparisonFailure
	 *             when there are unexpected differences
	 * @throws Exception
	 *             when access to part has failed
	 */
	public static void assertEquals(ExpectedAttachment expected, BodyPart attachment) throws Exception {
		AssertionRegistry registry = new FailAtEndRegistry();
		assertEquals(expected, attachment, registry);
		registry.execute();
	}

	private static void assertEquals(ExpectedAttachment expected, Message actual, AssertionRegistry registry) throws Exception {
		Object content = actual==null ? null : actual.getContent();
		registry.register(() -> Assert.assertTrue("should be multipart message", content instanceof Multipart));
		BodyPart part = getAttachmentOrNull((Multipart) content, expected.getName(), registry);
		assertEquals(expected, part, registry);
	}

	private static void assertEquals(ExpectedAttachment expected, BodyPart attachment, AssertionRegistry registry) throws Exception {
		// @formatter:off
		String prefix = "attachment named '" + expected.getName() + "'" + (attachment==null ? " (/!\\ not found)" : "");
		String contentType = attachment == null || attachment.getContentType()==null ? null : attachment.getContentType();
		registry.register(() -> Assert.assertTrue(prefix + " mimetype should match '" + expected.getMimetype() + "' but was " + (contentType==null ? "null" : "'" + contentType + "'"),
				contentType!=null && expected.getMimetype().matcher(contentType).matches()));
		registry.register(() -> Assert.assertEquals(prefix + " description should be '" + expected.getDescription() + "'", 
				expected.getDescription(),
				attachment == null ? null : attachment.getDescription()));
		registry.register(() -> Assert.assertEquals(prefix + " disposition should be '" + expected.getDisposition() + "'", 
				expected.getDisposition(),
				attachment == null ? null : attachment.getDisposition()));
		registry.register(() -> Assert.assertArrayEquals(prefix + " has invalid content", 
				expected.getContent(), 
				attachment == null ? null : getContent(attachment)));
		// @formatter:on
	}
	
	private static BodyPart getAttachmentOrNull(Multipart multipart, final String filename, AssertionRegistry registry) throws MessagingException {
		try {
			return getAttachment(multipart, filename);
		} catch(MessagingException e) {
			registry.register(failure(e));
			return null;
		} catch(IllegalStateException e) {
			registry.register(failure(e));
			return null;
		}
	}

	private static <E extends Exception> Executable<E> failure(E exception) {
		return new Executable<E>() {
			@Override
			public void run() throws E {
				throw exception;
			}
		};
	}

	private AssertAttachment() {
		super();
	}
}
