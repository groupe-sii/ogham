package fr.sii.ogham.email.sender.impl;

import static fr.sii.ogham.core.util.LogUtils.logString;
import static fr.sii.ogham.email.JavaMailConstants.DEFAULT_JAVAMAIL_IMPLEMENTATION_PRIORITY;
import static fr.sii.ogham.email.attachment.ContentDisposition.ATTACHMENT;
import static fr.sii.ogham.email.attachment.ContentDisposition.INLINE;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.priority.Priority;
import fr.sii.ogham.core.env.PropertiesBridge;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.sender.AbstractSpecializedSender;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.exception.javamail.AttachmentResourceHandlerException;
import fr.sii.ogham.email.exception.javamail.ContentHandlerException;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.message.EmailAddress;
import fr.sii.ogham.email.message.Recipient;
import fr.sii.ogham.email.sender.impl.javamail.JavaMailAttachmentHandler;
import fr.sii.ogham.email.sender.impl.javamail.JavaMailContentHandler;
import fr.sii.ogham.email.sender.impl.javamail.JavaMailInterceptor;

/**
 * Java mail API implementation.
 * 
 * @author Aurélien Baudet
 * @see JavaMailContentHandler
 */
@Priority(properties = "${ogham.email.implementation-priority.javamail}", defaultValue = DEFAULT_JAVAMAIL_IMPLEMENTATION_PRIORITY)
public class JavaMailSender extends AbstractSpecializedSender<Email> {
	private static final Logger LOG = LoggerFactory.getLogger(JavaMailSender.class);

	/**
	 * Properties that is used to initialize the session
	 */
	private final Properties properties;

	/**
	 * The content handler used to add message content
	 */
	private final JavaMailContentHandler contentHandler;

	/**
	 * The handler used to attach files to the email
	 */
	private final JavaMailAttachmentHandler attachmentHandler;

	/**
	 * Extra operations to apply on the message
	 */
	private final JavaMailInterceptor interceptor;

	/**
	 * Authentication mechanism
	 */
	private final Authenticator authenticator;

	public JavaMailSender(PropertyResolver propertyResolver, JavaMailContentHandler contentHandler, JavaMailAttachmentHandler attachmentHandler, Authenticator authenticator) {
		this(new PropertiesBridge(propertyResolver), contentHandler, attachmentHandler, authenticator);
	}

	public JavaMailSender(PropertyResolver propertyResolver, JavaMailContentHandler contentHandler, JavaMailAttachmentHandler attachmentHandler, Authenticator authenticator,
			JavaMailInterceptor interceptor) {
		this(new PropertiesBridge(propertyResolver), contentHandler, attachmentHandler, authenticator, interceptor);
	}

	public JavaMailSender(Properties properties, JavaMailContentHandler contentHandler, JavaMailAttachmentHandler attachmentHandler, Authenticator authenticator) {
		this(properties, contentHandler, attachmentHandler, authenticator, null);
	}

	public JavaMailSender(Properties properties, JavaMailContentHandler contentHandler, JavaMailAttachmentHandler attachmentHandler, Authenticator authenticator, JavaMailInterceptor interceptor) {
		super();
		this.properties = properties;
		this.contentHandler = contentHandler;
		this.attachmentHandler = attachmentHandler;
		this.authenticator = authenticator;
		this.interceptor = interceptor;
	}

	@Override
	public void send(Email email) throws MessageException {
		try {
			LOG.debug("Initialize Java mail session with authenticator {} and properties {}", authenticator, properties);
			LOG.debug("Create the mime message for email {}", logString(email));
			MimeMessage mimeMsg = createMimeMessage();
			// set the sender address
			setFrom(email, mimeMsg);
			// set recipients (to, cc, bcc)
			setRecipients(email, mimeMsg);
			// set subject and content
			mimeMsg.setSubject(email.getSubject());
			setMimeContent(email, mimeMsg);
			// default behavior is done => message is ready but let possibility
			// to add extra operations to do on the message
			if (interceptor != null) {
				LOG.debug("Executing extra operations for email {}", logString(email));
				interceptor.intercept(mimeMsg, email);
			}
			// message is ready => send it
			LOG.info("Sending email using Java Mail API through server {}:{}...", properties.getProperty("mail.smtp.host", properties.getProperty("mail.host")),
					properties.getProperty("mail.smtp.port", properties.getProperty("mail.port")));
			Transport.send(mimeMsg);
		} catch (MessagingException | ContentHandlerException | AttachmentResourceHandlerException | IOException e) {
			throw new MessageException("failed to send message using Java Mail API", email, e);
		}
	}

	/**
	 * Initialize the session and create the mime message.
	 * 
	 * @return the mime message
	 */
	private MimeMessage createMimeMessage() {
		// prepare the message
		Session session = Session.getInstance(properties, authenticator);
		return new MimeMessage(session);
	}

	/**
	 * Set the sender address on the mime message.
	 * 
	 * @param email
	 *            the source email
	 * @param mimeMsg
	 *            the mime message to fill
	 * @throws MessagingException
	 *             when the email address is not valid
	 * @throws AddressException
	 *             when the email address is not valid
	 * @throws UnsupportedEncodingException
	 *             when the email address is not valid
	 */
	private static void setFrom(Email email, MimeMessage mimeMsg) throws MessagingException, UnsupportedEncodingException {
		if (email.getFrom() == null) {
			throw new IllegalArgumentException("The sender address has not been set");
		}
		mimeMsg.setFrom(toInternetAddress(email.getFrom()));
	}

	/**
	 * Set the recipients addresses on the mime message.
	 * 
	 * @param email
	 *            the source email
	 * @param mimeMsg
	 *            the mime message to fill
	 * @throws MessagingException
	 *             when the email address is not valid
	 * @throws AddressException
	 *             when the email address is not valid
	 * @throws UnsupportedEncodingException
	 *             when the email address is not valid
	 */
	private static void setRecipients(Email email, MimeMessage mimeMsg) throws MessagingException, UnsupportedEncodingException {
		for (Recipient recipient : email.getRecipients()) {
			mimeMsg.addRecipient(convert(recipient.getType()), toInternetAddress(recipient.getAddress()));
		}
	}

	/**
	 * Set the content on the mime message.
	 * 
	 * <ul>
	 * <li>If the source email has only one textual content (text/html for
	 * example), the structure is:
	 * 
	 * <pre>
	 * [text/html] (root/body)
	 * </pre>
	 * 
	 * </li>
	 * <li>If the source email has HTML content with embedded attachments
	 * (images for example), the structure is:
	 * 
	 * <pre>
	 * [multipart/related] (root/body)
	 *   [text/html]       
	 *   [image/png]       (embedded image 1)
	 *   [image/gif]       (embedded image 2)
	 * </pre>
	 * 
	 * </li>
	 * <li>If the source email has HTML content with attachments, the structure
	 * is:
	 * 
	 * <pre>
	 * [multipart/mixed]              (root)
	 *   [text/html]                  (body)
	 *   [application/pdf]            (attached file 1)
	 *   [application/octet-stream]   (attached file 2)
	 * </pre>
	 * 
	 * </li>
	 * <li>If the source email has HTML content with embedded attachments
	 * (images for example) and additional attachments, the structure is:
	 * 
	 * <pre>
	 * [multipart/mixed]              (root)
	 *   [multipart/related]          (body)
	 *     [text/html]                
	 *     [image/png]                (embedded image 1)
	 *     [image/gif]                (embedded image 2)
	 *   [application/pdf]            (attached file 1)
	 *   [application/octet-stream]   (attached file 2)
	 * </pre>
	 * 
	 * </li>
	 * <li>If the source email has several textual contents (text/html and
	 * text/plain for example), the structure is:
	 * 
	 * <pre>
	 * [multipart/alternative]  (root/body)
	 *   [text/plain]           (alternative body)
	 *   [text/html]            (main body)
	 * </pre>
	 * 
	 * </li>
	 * <li>If the source email has several textual contents (text/html and
	 * text/plain for example) and embedded attachments (images for example),
	 * the structure is:
	 * 
	 * <pre>
	 * [multipart/related]          (root/body)
	 *   [multipart/alternative]    
	 *     [text/plain]             (alternative body)
	 *     [text/html]              (main body)
	 *   [image/png]                (embedded image 1)
	 *   [image/gif]                (embedded image 2)
	 * </pre>
	 * 
	 * </li>
	 * <li>If the source email has several textual contents (text/html and
	 * text/plain for example) and attachments, the structure is:
	 * 
	 * <pre>
	 * [multipart/mixed]              (root)
	 *   [multipart/alternative]      (body)
	 *     [text/plain]               (alternative body)
	 *     [text/html]                (main body)
	 *   [application/pdf]            (attached file 1)
	 *   [application/octet-stream]   (attached file 2)
	 * </pre>
	 * 
	 * </li>
	 * <li>If the source email has several textual contents (text/html and
	 * text/plain for example), embedded attachment (images for example) and
	 * attachments, the structure is:
	 * 
	 * <pre>
	 * [multipart/mixed]              (root)
	 *   [multipart/related]          (body)
	 *     [multipart/alternative]      
	 *       [text/plain]             (alternative body)
	 *       [text/html]              (main body)
	 *     [image/png]                (embedded image 1)
	 *     [image/gif]                (embedded image 2)
	 *   [application/pdf]            (attached file 1)
	 *   [application/octet-stream]   (attached file 2)
	 * </pre>
	 * 
	 * </li>
	 * </ul>
	 * 
	 * @param email
	 *            the source email
	 * @param mimeMsg
	 *            the mime message to fill
	 * @throws MessagingException
	 *             when the email address is not valid
	 * @throws ContentHandlerException
	 *             when the email address is not valid
	 * @throws AttachmentResourceHandlerException
	 *             when the email address is not valid
	 * @throws IOException
	 *             when the content can't be constructed
	 */
	private void setMimeContent(Email email, MimeMessage mimeMsg) throws MessagingException, ContentHandlerException, AttachmentResourceHandlerException, IOException {
		LOG.debug("Add message content for email {}", logString(email));

		Multipart mixedContainer = new MimeMultipart("mixed");

		// prepare the body
		contentHandler.setContent(mimeMsg, mixedContainer, email, email.getContent());

		// add the attachments (either embedded or attached)
		Multipart relatedContainer = getOrAddRelatedContainer(mixedContainer, email);
		for (Attachment attachment : email.getAttachments()) {
			Multipart attachmentContainer = isEmbeddableAttachment(attachment) ? relatedContainer : mixedContainer;
			attachmentHandler.addAttachment(attachmentContainer, attachment);
		}

		// set the content of the email
		if (hasDownloadableAttachments(email) || hasEmbeddableAttachments(email)) {
			mimeMsg.setContent(hasDownloadableAttachments(email) ? mixedContainer : relatedContainer);
		} else {
			// extract the body from the container (as it is not necessary)
			// and place the body at the root of the message
			BodyPart body = mixedContainer.getBodyPart(0);
			mimeMsg.setContent(body.getContent(), body.getContentType());
		}
	}

	private static Multipart getOrAddRelatedContainer(Multipart root, Email email) throws MessagingException, IOException {
		// no embeddable attachments means that there is no need of the related
		// container
		if (!hasEmbeddableAttachments(email)) {
			return null;
		}
		Multipart related = findRelatedContainer(root);
		if (related == null) {
			related = new MimeMultipart("related");
			moveBodyToRelatedContainer(root, related);
			addRelatedContainer(root, related);
		}
		return related;
	}

	private static void moveBodyToRelatedContainer(Multipart root, Multipart related) throws MessagingException {
		while (root.getCount() > 0) {
			related.addBodyPart(root.getBodyPart(0));
			root.removeBodyPart(0);
		}
	}

	private static void addRelatedContainer(Multipart root, Multipart related) throws MessagingException {
		MimeBodyPart part = new MimeBodyPart();
		part.setContent(related);
		root.addBodyPart(part);
	}

	private static Multipart findRelatedContainer(Multipart container) throws MessagingException, IOException {
		if (isRelated(container)) {
			return container;
		}
		for (int i = 0; i < container.getCount(); i++) {
			Object content = container.getBodyPart(i).getContent();
			if (content instanceof Multipart) {
				return findRelatedContainer((Multipart) content);
			}
		}
		return null;
	}

	private static boolean isRelated(Multipart mp) {
		return mp.getContentType().startsWith("multipart/related");
	}

	private static boolean isEmbeddableAttachment(Attachment attachment) {
		return INLINE.equals(attachment.getDisposition());
	}

	private static boolean isDownloadableAttachment(Attachment attachment) {
		return ATTACHMENT.equals(attachment.getDisposition());
	}

	private static boolean hasEmbeddableAttachments(Email email) {
		return email.getAttachments().stream().anyMatch(JavaMailSender::isEmbeddableAttachment);
	}

	private static boolean hasDownloadableAttachments(Email email) {
		return email.getAttachments().stream().anyMatch(JavaMailSender::isDownloadableAttachment);
	}

	private static RecipientType convert(fr.sii.ogham.email.message.RecipientType type) {
		switch (type) {
			case BCC:
				return RecipientType.BCC;
			case CC:
				return RecipientType.CC;
			case TO:
				return RecipientType.TO;
			default:
				throw new IllegalArgumentException("Invalid recipient type " + type);
		}
	}

	private static InternetAddress toInternetAddress(EmailAddress address) throws AddressException, UnsupportedEncodingException {
		return address.getPersonal() == null ? new InternetAddress(address.getAddress()) : new InternetAddress(address.getAddress(), address.getPersonal());
	}

	@Override
	public String toString() {
		return "JavaMailSender";
	}
}
