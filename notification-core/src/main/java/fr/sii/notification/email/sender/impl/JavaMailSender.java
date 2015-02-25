package fr.sii.notification.email.sender.impl;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import fr.sii.notification.core.exception.MessageException;
import fr.sii.notification.core.sender.AbstractSpecializedSender;
import fr.sii.notification.email.message.Email;
import fr.sii.notification.email.message.EmailAddress;
import fr.sii.notification.email.message.Recipient;

public class JavaMailSender extends AbstractSpecializedSender<Email> {

	private Properties properties;
	
	public JavaMailSender() {
		this(System.getProperties());
	}
	
	public JavaMailSender(Properties properties) {
		super();
		this.properties = properties;
	}

	@Override
	public void send(Email email) throws MessageException {
		try {
			Session session = Session.getDefaultInstance(properties);
			MimeMessage mimeMsg = new MimeMessage(session);
			mimeMsg.setFrom(toInternetAddress(email.getFrom()));
			for(Recipient recipient : email.getRecipients()) {
				mimeMsg.addRecipient(convert(recipient.getType()), toInternetAddress(recipient.getAddress()));
			}
			mimeMsg.setSubject(email.getSubject());
			mimeMsg.setText(email.getContent().toString());
			Transport.send(mimeMsg);
		} catch (UnsupportedEncodingException | MessagingException e) {
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

}
