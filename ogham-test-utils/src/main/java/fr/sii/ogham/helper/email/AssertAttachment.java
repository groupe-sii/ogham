package fr.sii.ogham.helper.email;

import static fr.sii.ogham.helper.email.EmailUtils.getAttachment;
import static fr.sii.ogham.helper.email.EmailUtils.getContent;

import java.io.IOException;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;

import org.junit.Assert;
import org.junit.internal.ArrayComparisonFailure;

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
	 * @throws IOException
	 *             when email can't be read
	 * @throws MessagingException
	 *             when email values can't be accessed
	 */
	public static void assertEquals(ExpectedAttachment expected, Message[] actual) throws IOException, MessagingException {
		if (actual.length != 1) {
			throw new IllegalArgumentException("should have only one message but was " + actual.length);
		}
		assertEquals(expected, actual[0]);
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
	 * @throws IOException
	 *             when email can't be read
	 * @throws MessagingException
	 *             when email values can't be accessed
	 */
	public static void assertEquals(ExpectedAttachment expected, Message actual) throws IOException, MessagingException {
		Object content = actual.getContent();
		Assert.assertTrue("should be multipart message", content instanceof Multipart);
		BodyPart part = getAttachment((Multipart) content, expected.getName());
		assertEquals(expected, part);
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
	 * @throws IOException
	 *             when email can't be read
	 * @throws MessagingException
	 *             when email values can't be accessed
	 * @throws ArrayComparisonFailure
	 *             when there are unexpected differences
	 */
	public static void assertEquals(ExpectedAttachment expected, BodyPart attachment) throws MessagingException, ArrayComparisonFailure, IOException {
		Assert.assertTrue("attachment " + expected.getName() + " should have mimetype " + expected.getMimetype() + " but was " + attachment.getContentType(),
				expected.getMimetype().matcher(attachment.getContentType()).matches());
		Assert.assertEquals("attachment " + expected.getName() + " should have description " + expected.getDescription(), expected.getDescription(), attachment.getDescription());
		Assert.assertEquals("attachment " + expected.getName() + " should have disposition " + expected.getDisposition(), expected.getDisposition(), attachment.getDisposition());
		Assert.assertArrayEquals("attachment " + expected.getName() + " has invalid content", expected.getContent(), getContent(attachment));
	}

	
	private AssertAttachment() {
		super();
	}
}
