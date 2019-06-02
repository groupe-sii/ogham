package fr.sii.ogham.helper.email;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

import org.apache.commons.io.IOUtils;


public class EmailUtils {
	private static final Pattern TEXT_OR_HTML_MIMETYPES = Pattern.compile("^((text/)|(application/x?html)).*", Pattern.CASE_INSENSITIVE);
	public static final String ATTACHMENT_DISPOSITION = "attachment";
	public static final String INLINE_DISPOSITION = "inline";

	/**
	 * Retrieve the main part of the message (recursively):
	 * <ul>
	 * <li>The part of the message when it contains only one part</li>
	 * <li>The part with text or HTML mimetype if only one part with one of that
	 * mimetype</li>
	 * <li>The second part with text or HTML mimetype if there are two text or
	 * HTML parts</li>
	 * </ul>
	 * 
	 * @param actualEmail
	 *            the message
	 * @return the body of the message
	 * @throws MessagingException
	 *             when message can't be read
	 */
	public static Part getBodyPart(Part actualEmail) throws MessagingException {
		List<Part> bodyParts = getTextualParts(actualEmail);
		if (bodyParts.isEmpty()) {
			return null;
		}
		// if only one part matching => not Mltipart with alternative => take
		// the first one
		// if several matching parts => alternative + main content => the second
		// is the main
		return bodyParts.size() == 1 ? bodyParts.get(0) : bodyParts.get(1);
	}

	/**
	 * Retrieve the alternative part of the message (recursively). The
	 * alternative is useful when sending HTML email that may be unreadable on
	 * some email clients. For example, a smartphone will display the 2 or 3
	 * first lines as a summary. Many smartphones will take the HTML message
	 * as-is and will display HTML tags instead of content of email. Alternative
	 * is used to provide a textual visualization of the message that will be
	 * readable by any system.
	 * 
	 * <p>
	 * The alternative of the message is either:
	 * <ul>
	 * <li>null if there is only one part</li>
	 * <li>null if there is only one text or HTML part</li>
	 * <li>the first part if there are more than one text or HTML part</li>
	 * </ul>
	 * 
	 * @param actualEmail
	 *            the message
	 * @return the alternative part of the message if exists, null otherwise
	 * @throws MessagingException
	 *             when message can't be read
	 */
	public static Part getAlternativePart(Part actualEmail) throws MessagingException {
		List<Part> bodyParts = getTextualParts(actualEmail);
		if (bodyParts.size() < 2) {
			return null;
		}
		return bodyParts.get(0);
	}

	/**
	 * Get the whole list of "textual" parts (text or HTML mimetypes).
	 * 
	 * @param actualEmail
	 *            the message
	 * @return the list of "textual" parts
	 * @throws MessagingException
	 *             when message can't be read
	 */
	public static List<Part> getTextualParts(Part actualEmail) throws MessagingException {
		List<Part> founds = new ArrayList<>();
		getBodyParts(actualEmail, founds);
		return founds;
	}

	/**
	 * Get the content as byte array of a particular part.
	 * 
	 * @param part
	 *            the part
	 * @return the content
	 * @throws IOException
	 *             when part can't be read
	 * @throws MessagingException
	 *             when message can"t be read
	 */
	public static byte[] getContent(Part part) throws IOException, MessagingException {
		InputStream stream = part.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(stream, baos);
		return baos.toByteArray();
	}

	/**
	 * Get a particular attachment (found using exact filename field).
	 * 
	 * @param multipart
	 *            the email that contains several parts
	 * @param filename
	 *            the name of the attachment to find
	 * @return the found attachment or null
	 * @throws MessagingException
	 *             when message can't be read
	 */
	public static BodyPart getAttachment(Multipart multipart, final String filename) throws MessagingException {
		return getAttachment(multipart, new FileNamePredicate(filename));
	}

	/**
	 * Get a particular attachment (found using provided predicate). If several
	 * attachments match the predicate, only the first one is retrieved.
	 * 
	 * @param multipart
	 *            the email that contains several parts
	 * @param filter
	 *            the predicate used to find the attachment
	 * @return the found attachment or null
	 * @throws MessagingException
	 *             when message can't be read
	 */
	public static BodyPart getAttachment(Multipart multipart, Predicate<BodyPart> filter) throws MessagingException {
		List<BodyPart> attachments = getAttachments(multipart, filter);
		return attachments.isEmpty() ? null : attachments.get(0);
	}

	/**
	 * Get a list of attachments that match the provided predicate.
	 * 
	 * @param multipart
	 *            the email that contains several parts
	 * @param filter
	 *            the predicate used to find the attachments
	 * @return the found attachments or empty list
	 * @throws MessagingException
	 *             when message can't be read
	 */
	public static List<BodyPart> getAttachments(Multipart multipart, Predicate<BodyPart> filter) throws MessagingException {
		List<BodyPart> found = new ArrayList<>();
		for (int i = 0; i < multipart.getCount(); i++) {
			BodyPart bodyPart = multipart.getBodyPart(i);
			if (filter.test(bodyPart)) {
				found.add(bodyPart);
			}
		}
		return found;
	}

	/**
	 * Get the whole list of attachments.
	 * 
	 * @param multipart
	 *            the email that contains several parts
	 * @return the found attachments or empty list
	 * @throws MessagingException
	 *             when message can't be read
	 */
	public static List<BodyPart> getAttachments(Multipart multipart) throws MessagingException {
		List<BodyPart> attachments = new ArrayList<>();
		for (int i = 0; i < multipart.getCount(); i++) {
			BodyPart bodyPart = multipart.getBodyPart(i);
			if (!isContent(bodyPart) && !isMultipart(bodyPart)) {
				attachments.add(bodyPart);
			}
		}
		return attachments;
	}

	private static void getBodyParts(Part actualEmail, List<Part> founds) throws MessagingException {
		try {
			Object content = actualEmail.getContent();
			if (content instanceof Multipart) {
				Multipart mp = (Multipart) content;
				for (int i = 0; i < mp.getCount(); i++) {
					BodyPart part = mp.getBodyPart(i);
					if (isMultipart(part)) {
						getBodyParts(part, founds);
					} else if (isContent(part)) {
						founds.add(part);
					}
				}
			}
		} catch (IOException e) {
			throw new MessagingException("Failed to access content of the mail", e);
		}
	}

	private static boolean isMultipart(BodyPart part) throws MessagingException {
		return part.getContentType().startsWith("multipart/");
	}

	private static boolean isContent(BodyPart part) throws MessagingException {
		return TEXT_OR_HTML_MIMETYPES.matcher(part.getContentType()).matches();
	}

	private EmailUtils() {
		super();
	}

}
