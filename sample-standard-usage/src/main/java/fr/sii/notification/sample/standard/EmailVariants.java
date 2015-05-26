package fr.sii.notification.sample.standard;

import java.util.ArrayList;

import fr.sii.notification.core.message.content.StringContent;
import fr.sii.notification.email.attachment.Attachment;
import fr.sii.notification.email.message.Email;
import fr.sii.notification.email.message.EmailAddress;
import fr.sii.notification.email.message.Recipient;

public class EmailVariants {
	public static void main(String[] args) {
		new Email("subject", new StringContent("email content"), new ArrayList<Recipient>());
		new Email("subject", new StringContent("email content"), new Recipient[10]);
		new Email("subject", new StringContent("email content"), "<to address>", "<to address>");
		new Email("subject", new StringContent("email content"), "<to address>");
		new Email("subject", new StringContent("email content"));
		new Email("subject", "email content", "<to address>", "<to address>");
		new Email("subject", "email content", "<to address>");
		new Email("subject", "email content");
		new Email("subject", new StringContent("email content"), new EmailAddress("<from address>"), new EmailAddress("<to address>"), new EmailAddress("<to address>"));
		new Email("subject", new StringContent("email content"), new EmailAddress("<from address>"), new EmailAddress("<to address>"));
		new Email("subject", new StringContent("email content"), new EmailAddress("<from address>"));
		new Email("subject", new StringContent("email content"), new EmailAddress("<from address>"), new ArrayList<Recipient>());
		new Email("subject", new StringContent("email content"), new EmailAddress("<from address>"), new Recipient[10]);
		new Email("subject", new StringContent("email content"), new ArrayList<Recipient>(), new Attachment("file1"), new Attachment("file2"));
		new Email("subject", new StringContent("email content"), new ArrayList<Recipient>(), new Attachment("file1"));
		new Email("subject", new StringContent("email content"), new ArrayList<Recipient>());
		new Email("subject", new StringContent("email content"), new ArrayList<Recipient>(), new ArrayList<Attachment>());
		new Email("subject", new StringContent("email content"), new Recipient("<recipient address>"), new Attachment[10]);
		new Email("subject", new StringContent("email content"), new Recipient("<recipient address>"), new ArrayList<Attachment>());
		new Email("subject", new StringContent("email content"), new Recipient("<recipient address>"), new Recipient("<recipient address>"), new Recipient("<recipient address>"));
		new Email("subject", new StringContent("email content"), new Recipient("<recipient address>"), new Recipient("<recipient address>"));
		new Email("subject", new StringContent("email content"), new Recipient("<recipient address>"));
		new Email("subject", "email content", new EmailAddress("<from address>"), "<recipient address>", "<recipient address>");
		new Email("subject", "email content", new EmailAddress("<from address>"), "<recipient address>");
		new Email("subject", "email content", new EmailAddress("<from address>"));
		new Email("subject", "email content", new ArrayList<String>(), new Attachment("file1"), new Attachment("file2"));
		new Email("subject", "email content", new ArrayList<String>(), new Attachment("file1"));
		new Email("subject", "email content", new ArrayList<String>());
		new Email("subject", new StringContent("email content"), new EmailAddress("<from address>"), new ArrayList<Recipient>(), new Attachment("file1"), new Attachment("file2"));
		new Email("subject", new StringContent("email content"), new EmailAddress("<from address>"), new ArrayList<Recipient>(), new Attachment("file1"));
		new Email("subject", new StringContent("email content"), new EmailAddress("<from address>"), new ArrayList<Recipient>());
		new Email("subject", new StringContent("email content"), new EmailAddress("<from address>"), new ArrayList<Recipient>(), new ArrayList<Attachment>());
		new Email("subject", new StringContent("email content"), new EmailAddress("<from address>"), new Recipient("<recipient address>"), new Recipient("<recipient address>"), new Recipient("<recipient address>"));
		new Email("subject", new StringContent("email content"), new EmailAddress("<from address>"), new Recipient("<recipient address>"), new Recipient("<recipient address>"));
		new Email("subject", new StringContent("email content"), new EmailAddress("<from address>"), new Recipient("<recipient address>"));
		new Email("subject", new StringContent("email content"), new Recipient("<recipient address>"), new Attachment("file1"), new Attachment("file2"));
		new Email("subject", new StringContent("email content"), new Recipient("<recipient address>"), new Attachment("file1"));
		new Email("subject", new StringContent("email content"), new Recipient("<recipient address>"));
		new Email("subject", "email content", "<recipient address>", new Attachment("file1"), new Attachment("file2"), new Attachment("file3"));
		new Email("subject", "email content", "<recipient address>", new Attachment("file1"), new Attachment("file2"));
		new Email("subject", "email content", "<recipient address>", new Attachment("file1"));
	}
}
