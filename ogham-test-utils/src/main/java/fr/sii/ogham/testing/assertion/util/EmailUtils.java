package fr.sii.ogham.testing.assertion.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import ogham.testing.jakarta.mail.BodyPart;
import ogham.testing.jakarta.mail.Message;
import ogham.testing.jakarta.mail.MessagingException;
import ogham.testing.jakarta.mail.Multipart;
import ogham.testing.jakarta.mail.Part;
import ogham.testing.jakarta.mail.internet.MimeMessage;

import ogham.testing.org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.testing.assertion.filter.DefaultAttachmentPredicate;
import fr.sii.ogham.testing.assertion.filter.FileNamePredicate;

public final class EmailUtils {
	private static final Logger LOG = LoggerFactory.getLogger(EmailUtils.class);
	public static final String ATTACHMENT_DISPOSITION = "attachment";
	public static final String INLINE_DISPOSITION = "inline";
	
	/**
	 * Retrieve the body parts of the message (recursively):
	 * <ul>
	 * <li>The part of the message when it contains only one part</li>
	 * <li>The parts with text/* mimetype</li>
	 * </ul>
	 * 
	 * @param actualEmail
	 *            the message
	 * @return the body of the message
	 * @throws MessagingException
	 *             when message can't be read
	 */
	public static List<Part> getBodyParts(Part actualEmail) throws MessagingException {
		return getTextualParts(actualEmail);
	}

	/**
	 * Retrieve the main part of the message (recursively):
	 * <ul>
	 * <li>The part of the message when it contains only one part</li>
	 * <li>The part with text/* mimetype if only one part with one of that
	 * mimetype</li>
	 * <li>The last part with text or HTML mimetype if there are several text/*
	 * parts</li>
	 * </ul>
	 * 
	 * @param actualEmail
	 *            the message
	 * @return the body of the message
	 * @throws MessagingException
	 *             when message can't be read
	 */
	public static Part getBodyPart(Part actualEmail) throws MessagingException {
		return getBestAlternative(getBodyParts(actualEmail));
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
		return getBodyParts(actualEmail, EmailUtils::isTextualContent);
	}

	/**
	 * Get the content as byte array of a particular part.
	 * 
	 * <p>
	 * If the part is null or the content stream is null, then
	 * null is returned.
	 * 
	 * @param part
	 *            the part
	 * @return the content as byte array or null
	 * @throws IOException
	 *             when part can't be read
	 * @throws MessagingException
	 *             when message can't be read
	 */
	@SuppressWarnings("squid:S1168")	// return null on purpose (to be able to distinguish empty content from no content at all in tests)
	public static byte[] getContent(Part part) throws IOException, MessagingException {
		if (part == null) {
			return null;
		}
		InputStream stream = part.getInputStream();
		if (stream == null) {
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(stream, baos);
		return baos.toByteArray();
	}

	/**
	 * Get the content as {@link String} of a particular part.
	 * 
	 * <p>
	 * <strong>NOTE: This method handles a special case due to how Java Mail
	 * sends textual content (adds CRLF)</strong> If the content is a textual
	 * content ("text/*" mimetype) and the part has no parent and it ends with
	 * CRLF, remove the last CRLF.
	 * 
	 * <strong>If your original text had the CRLF, this method can't know that
	 * it was already part of the original text because Java Mail only adds CRLF
	 * if there not already has CRLF at the end of the text.</strong>
	 * 
	 * <p>
	 * If the part is null or the content stream is null, then
	 * null is returned.
	 * 
	 * @param part
	 *            the part
	 * @param charset
	 *            the charset used to decode the part content
	 * @return the part content
	 * @throws MessagingException
	 *             when the part can't be accessed
	 * @throws IOException
	 *             when the part content can't be read
	 */
	public static String getContent(Part part, Charset charset) throws IOException, MessagingException {
		byte[] bytes = getContent(part);
		if (bytes == null) {
			return null;
		}
		String content = IOUtils.toString(bytes, charset.name());
		if (isTextualContent(part) && !hasParent(part) && content.endsWith("\r\n")) {
			return content.substring(0, content.length() - 2);
		}
		return content;
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
	public static BodyPart getAttachment(Multipart multipart, Predicate<Part> filter) throws MessagingException {
		List<BodyPart> attachments = getAttachments(multipart, filter);
		return attachments.isEmpty() ? null : attachments.get(0);
	}

	/**
	 * Get a list of direct attachments that match the provided predicate.
	 * 
	 * @param multipart
	 *            the email that contains several parts
	 * @param filter
	 *            the predicate used to find the attachments
	 * @return the found attachments or empty list
	 * @throws MessagingException
	 *             when message can't be read
	 */
	public static List<BodyPart> getAttachments(Multipart multipart, Predicate<Part> filter) throws MessagingException {
		if (multipart == null) {
			throw new IllegalStateException("The multipart can't be null");
		}
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
	 * Get the whole list of attachments (recursively).
	 * 
	 * @param message
	 *            the email that contains several parts
	 * @return the found attachments or empty list
	 * @throws MessagingException
	 *             when message can't be read
	 */
	public static List<BodyPart> getAttachments(Part message) throws MessagingException {
		return getAttachments(message, p -> true);
	}

	/**
	 * Get the whole list of attachments.
	 * 
	 * <p>
	 * <strong>WARNING:</strong> As there is no way to ensure that a part is an
	 * attachment, every part is testing against the filter (event multipart
	 * containers). So the filter that you provide is combined with
	 * {@link DefaultAttachmentPredicate}. This way, only the list of parts that
	 * may be potential attachments (downloadable or embeddable) are provided to
	 * your filter.
	 * 
	 * @param message
	 *            the email that contains several parts
	 * @param filter
	 *            filter the parts to keep only some attachments. If filter
	 *            returns true then the part is added to the list.
	 * @param <T>
	 *            type of part
	 * @return the found attachments or empty list
	 * @throws MessagingException
	 *             when message can't be read
	 */
	public static <T extends Part> List<T> getAttachments(Part message, Predicate<Part> filter) throws MessagingException {
		List<T> attachments = new ArrayList<>();
		findBodyParts(message, new DefaultAttachmentPredicate().and(filter), attachments);
		return attachments;
	}

	/**
	 * Indicates if a part is a multipart (its Content-Type starts with
	 * "multipart/").
	 * 
	 * @param part
	 *            the part to check
	 * @return true if a multipart
	 */
	public static boolean isMultipart(Part part) {
		try {
			return part.isMimeType("multipart/*");
		} catch (MessagingException e) {
			throw new MessagingRuntimeException("Failed to retrieve Content-Type of part", e);
		}
	}

	/**
	 * Indicates if the part contains text (either plain text or html). It is
	 * checked using Content-Type (either "text/*", "application/html" or
	 * "application/xhtml").
	 * 
	 * @param part
	 *            the part to check
	 * @return true if it is text
	 */
	public static boolean isTextualContent(Part part) {
		try {
			return part.isMimeType("text/*") || part.isMimeType("application/html") || part.isMimeType("application/xhtml");
		} catch (MessagingException e) {
			throw new MessagingRuntimeException("Failed to retrieve Content-Type of part", e);
		}
	}

	/**
	 * Get the whole structure of the email. This is mainly used for debugging
	 * purpose.
	 * 
	 * @param mimeMessage
	 *            the email
	 * @return the structure of the email
	 * @throws IOException
	 *             when email can't be read
	 * @throws MessagingException
	 *             when email can't be read
	 */
	public static String getStructure(MimeMessage mimeMessage) throws IOException, MessagingException {
		StringBuilder structure = new StringBuilder();
		findParts(mimeMessage, structure, "");
		return structure.toString();
	}

	/**
	 * Get the partial structure of the email from the provided container. This
	 * is mainly used for debugging purpose.
	 * 
	 * @param multipart
	 *            the container
	 * @return the structure of the email
	 * @throws IOException
	 *             when email can't be read
	 * @throws MessagingException
	 *             when email can't be read
	 */
	public static String getStructure(Multipart multipart) throws IOException, MessagingException {
		StringBuilder structure = new StringBuilder();
		findParts(multipart, structure, "");
		return structure.toString();
	}

	/**
	 * Get the partial structure of the email from the provided part. This is
	 * mainly used for debugging purpose.
	 * 
	 * @param part
	 *            the part
	 * @return the structure of the email
	 * @throws IOException
	 *             when email can't be read
	 * @throws MessagingException
	 *             when email can't be read
	 */
	public static String getStructure(BodyPart part) throws IOException, MessagingException {
		StringBuilder structure = new StringBuilder();
		findParts(part, structure, "");
		return structure.toString();
	}

	private static <T extends Part> List<T> getBodyParts(Part actualEmail, Predicate<Part> filter) throws MessagingException {
		List<T> founds = new ArrayList<>();
		findBodyParts(actualEmail, filter, founds);
		return founds;
	}

	private static <T extends Part> void findBodyParts(Part actualEmail, Predicate<Part> filter, List<T> founds) throws MessagingException {
		LOG.trace("---------------------------");
		findBodyParts(actualEmail, filter, founds, "");
	}

	private static <T extends Part> void findBodyParts(Part part, Predicate<Part> filter, List<T> founds, String indent) throws MessagingException {
		try {
			addPart(filter, founds, indent, part);
			Object content = part.getContent();
			if (content instanceof Multipart) {
				Multipart mp = (Multipart) content;
				LOG.trace("{}find {}", indent, mp.getContentType());
				for (int i = 0; i < mp.getCount(); i++) {
					findBodyParts(mp.getBodyPart(i), filter, founds, indent + "   ");
				}
			}
		} catch (IOException e) {
			throw new MessagingException("Failed to access content of the mail", e);
		}
	}

	@SuppressWarnings("unchecked")
	private static <T extends Part> void addPart(Predicate<Part> filter, List<T> founds, String indent, Part part) throws MessagingException {
		if (filter.test(part)) {
			LOG.trace("{}{}add {}", indent, "   ", part.getContentType());
			founds.add((T) part);
		}
	}

	private static void findParts(Part part, StringBuilder structure, String indent) throws IOException, MessagingException {
		addPart(part, structure, indent);
		Object content = part.getContent();
		if (content instanceof Multipart) {
			findParts((Multipart) content, structure, indent);
		}
	}

	private static void findParts(Multipart mp, StringBuilder structure, String indent) throws MessagingException, IOException {
		for (int i = 0; i < mp.getCount(); i++) {
			BodyPart subpart = mp.getBodyPart(i);
			findParts(subpart, structure, indent + "  ");
		}
	}

	private static void addPart(Part part, StringBuilder structure, String indent) throws MessagingException {
		structure.append(indent).append("[").append(part.getDataHandler().getContentType().split(";")[0]).append("]\n");
	}

	/**
	 * According to
	 * <a href="https://www.w3.org/Protocols/rfc1341/7_2_Multipart.html">rfc1341
	 * §7.2.3 The Multipart/alternative subtype</a>, the best alternative is the
	 * last that can be displayed.
	 * 
	 * @param bodyParts
	 *            the possible body parts
	 * @return
	 */
	private static Part getBestAlternative(List<Part> bodyParts) {
		if (bodyParts.isEmpty()) {
			return null;
		}
		return bodyParts.get(bodyParts.size() - 1);
	}

	private static boolean hasParent(Part part) {
		if (part instanceof Message) {
			return false;
		}
		if (part instanceof BodyPart) {
			return ((BodyPart) part).getParent() != null;
		}
		return false;
	}

	private EmailUtils() {
		super();
	}

	private static class MessagingRuntimeException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public MessagingRuntimeException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
