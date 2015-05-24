package fr.sii.notification.email.sender.impl;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.notification.core.exception.MessageException;
import fr.sii.notification.core.sender.AbstractSpecializedSender;
import fr.sii.notification.email.exception.javamail.ContentHandlerException;
import fr.sii.notification.email.message.Email;
import fr.sii.notification.email.message.EmailAddress;
import fr.sii.notification.email.message.Recipient;
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
	 * Extra operations to apply on the message
	 */
	private JavaMailInterceptor interceptor;

	/**
	 * Authentication mechanism
	 */
	private Authenticator authenticator;
	
	public JavaMailSender(Properties properties, JavaMailContentHandler contentHandler, Authenticator authenticator) {
		this(properties, contentHandler, authenticator, null);
	}
	
	public JavaMailSender(Properties properties, JavaMailContentHandler contentHandler, Authenticator authenticator, JavaMailInterceptor interceptor) {
		super();
		this.properties = properties;
		this.contentHandler = contentHandler;
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
			mimeMsg.setFrom(toInternetAddress(email.getFrom()));
			// set recipients (to, cc, bcc)
			for(Recipient recipient : email.getRecipients()) {
				mimeMsg.addRecipient(convert(recipient.getType()), toInternetAddress(recipient.getAddress()));
			}
			// set subject and content
			mimeMsg.setSubject(email.getSubject());
			LOG.debug("Add message content for email {}", email);
			// delegate content management to specialized classes
			contentHandler.setContent(mimeMsg, email.getContent());
			// TODO: manage attachments
			// default behavior is done => message is ready but let possibility to add extra operations to do on the message
			if(interceptor!=null) {
				LOG.debug("Executing extra operations for email {}", email);
				interceptor.intercept(mimeMsg, email);
			}
			// message is ready => send it
			LOG.info("Sending email using Java Mail API through server {}:{}...", properties.get("mail.smtp.host"), properties.get("mail.smtp.port"));
			Transport.send(mimeMsg);
		} catch (UnsupportedEncodingException | MessagingException | ContentHandlerException e) {
			throw new MessageException("failed to send message using Java Mail API", email, e);
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
