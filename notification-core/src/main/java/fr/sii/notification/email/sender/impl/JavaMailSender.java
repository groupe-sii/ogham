package fr.sii.notification.email.sender.impl;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Authenticator;
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

import fr.sii.notification.core.exception.MessageException;
import fr.sii.notification.core.sender.AbstractSpecializedSender;
import fr.sii.notification.email.attachment.Attachment;
import fr.sii.notification.email.exception.javamail.AttachmentSourceHandlerException;
import fr.sii.notification.email.exception.javamail.ContentHandlerException;
import fr.sii.notification.email.message.Email;
import fr.sii.notification.email.message.EmailAddress;
import fr.sii.notification.email.message.Recipient;
import fr.sii.notification.email.sender.impl.javamail.JavaMailAttachmentSourceHandler;
import fr.sii.notification.email.sender.impl.javamail.JavaMailContentHandler;
import fr.sii.notification.email.sender.impl.javamail.JavaMailInterceptor;

/**
 * Java mail API implementation.
 * 
 * @author Aurélien Baudet
 * @see JavaMailContentHandler
 */
public class JavaMailSender extends AbstractSpecializedSender<Email> {
	private static final Logger LOG = LoggerFactory.getLogger(JavaMailSender.class);

	/**
	 * Properties that is used to initialize the session
	 */
	private Properties properties;
	
	/**
	 * The content handler used to add message content
	 */
	private JavaMailContentHandler contentHandler;
	
	/**
	 * The attachment handler used to add attachments to the mail
	 */
	private JavaMailAttachmentSourceHandler attachmentHandler;
	
	/**
	 * Extra operations to apply on the message
	 */
	private JavaMailInterceptor interceptor;

	/**
	 * Authentication mechanism
	 */
	private Authenticator authenticator;
	
	public JavaMailSender(Properties properties, JavaMailContentHandler contentHandler, JavaMailAttachmentSourceHandler attachmentSourceHandler, Authenticator authenticator) {
		this(properties, contentHandler, attachmentSourceHandler, authenticator, null);
	}
	
	public JavaMailSender(Properties properties, JavaMailContentHandler contentHandler, JavaMailAttachmentSourceHandler attachmentHandler, Authenticator authenticator, JavaMailInterceptor interceptor) {
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
			// prepare the message
			Session session = Session.getDefaultInstance(properties, authenticator);
			LOG.debug("Create the mime message for email {}", email);
			MimeMessage mimeMsg = new MimeMessage(session);
			// set the sender address
			if(email.getFrom()==null) {
				throw new IllegalArgumentException("The sender address has not been set");
			}
			mimeMsg.setFrom(toInternetAddress(email.getFrom()));
			// set recipients (to, cc, bcc)
			for(Recipient recipient : email.getRecipients()) {
				mimeMsg.addRecipient(convert(recipient.getType()), toInternetAddress(recipient.getAddress()));
			}
			// set subject and content
			mimeMsg.setSubject(email.getSubject());
			LOG.debug("Add message content for email {}", email);
			MimeMultipart multipart = new MimeMultipart();
			// delegate content management to specialized classes
			contentHandler.setContent(mimeMsg, multipart, email.getContent());
			// add attachments
			for(Attachment attachment : email.getAttachments()) {
				addAttachment(multipart, attachment);
			}
			mimeMsg.setContent(multipart);
			// default behavior is done => message is ready but let possibility to add extra operations to do on the message
			if(interceptor!=null) {
				LOG.debug("Executing extra operations for email {}", email);
				interceptor.intercept(mimeMsg, email);
			}
			// message is ready => send it
			LOG.info("Sending email using Java Mail API through server {}:{}...", properties.get("mail.smtp.host"), properties.get("mail.smtp.port"));
			Transport.send(mimeMsg);
		} catch (UnsupportedEncodingException | MessagingException | ContentHandlerException | AttachmentSourceHandlerException e) {
			throw new MessageException("failed to send message using Java Mail API", email, e);
		}
	}
	
	private void addAttachment(Multipart multipart, Attachment attachment) throws AttachmentSourceHandlerException {
		MimeBodyPart part = new MimeBodyPart();
		try {
			part.setFileName(attachment.getSource().getName());
			part.setDisposition(attachment.getDisposition());
			part.setDescription(attachment.getDescription());
			attachmentHandler.setData(part, attachment.getSource(), attachment);
			multipart.addBodyPart(part);
		} catch (MessagingException e) {
			throw new AttachmentSourceHandlerException("Failed to attach "+attachment.getSource().getName(), attachment, e);
		}
	}
	
	private RecipientType convert(fr.sii.notification.email.message.RecipientType type) {
		switch(type) {
			case BCC:
				return RecipientType.BCC;
			case CC:
				return RecipientType.CC;
			case TO:
				return RecipientType.TO;
		}
		throw new IllegalArgumentException("Invalid recipient type "+type);
	}
	
	private static InternetAddress toInternetAddress(EmailAddress address) throws AddressException, UnsupportedEncodingException {
		return address.getPersonal()==null ? new InternetAddress(address.getAddress()) : new InternetAddress(address.getAddress(), address.getPersonal());
	}

	@Override
	public String toString() {
		return "JavaMailSender";
	}
}
