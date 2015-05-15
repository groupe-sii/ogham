package fr.sii.notification.email.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.notification.core.message.Message;
import fr.sii.notification.core.message.attachment.Attachment;
import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.message.content.StringContent;

public class Email implements Message {
	private String subject;
	
	private Content content;
	
	private EmailAddress from;
	
	private List<Recipient> recipients;

	private List<Attachment> attachments;

	public Email(String subject, String content, String... recipients) {
		this(subject, new StringContent(content), toRecipient(recipients));
	}
	
	public Email(String subject, Content content, Recipient... recipients) {
		this(subject, content, new ArrayList<>(Arrays.asList(recipients)));
	}
	
	public Email(String subject, Content content, List<Recipient> recipients) {
		this(subject, content, recipients, new ArrayList<Attachment>());
	}

	public Email(String subject, Content content, EmailAddress... to) {
		this(subject, content, new ArrayList<>(Arrays.asList(toRecipient(to))));
	}
	
	public Email(String subject, Content content, String... to) {
		this(subject, content, new ArrayList<>(Arrays.asList(toRecipient(to))));
	}
	
	public Email(String subject, String content, EmailAddress from, String... recipients) {
		this(subject, new StringContent(content), from, toRecipient(recipients));
	}
	
	public Email(String subject, Content content, EmailAddress from, Recipient... recipients) {
		this(subject, content, from, new ArrayList<>(Arrays.asList(recipients)));
	}
	
	public Email(String subject, Content content, EmailAddress from, List<Recipient> recipients) {
		this(subject, content, from, recipients, new ArrayList<Attachment>());
	}

	public Email(String subject, Content content, EmailAddress from, EmailAddress... to) {
		this(subject, content, from, new ArrayList<>(Arrays.asList(toRecipient(to))));
	}
	
	public Email(String subject, String content, List<String> to, Attachment... attachments) {
		this(subject, new StringContent(content), new ArrayList<>(Arrays.asList(toRecipient(to.toArray(new String[to.size()])))), attachments);
	}

	public Email(String subject, Content content, List<Recipient> to, Attachment... attachments) {
		this(subject, content, to, new ArrayList<>(Arrays.asList(attachments)));
	}

	public Email(String subject, Content content, List<Recipient> to, List<Attachment> attachments) {
		this(subject, content, null, to, attachments);
	}

	public Email(String subject, Content content, EmailAddress from, List<Recipient> recipients, Attachment... attachments) {
		this(subject, content, from, recipients, new ArrayList<>(Arrays.asList(attachments)));
	}

	public Email(String subject, Content content, EmailAddress from, List<Recipient> recipients, List<Attachment> attachments) {
		super();
		this.subject = subject;
		this.content = content;
		this.from = from;
		this.recipients = recipients;
		this.attachments = attachments;
	}

	public Email addAttachment(Attachment attachment) {
		attachments.add(attachment);
		return this;
	}
	
	public Email addRecipient(Recipient recipient) {
		recipients.add(recipient);
		return this;
	}
	
	public Email addRecipient(String recipient) {
		addRecipient(new Recipient(recipient));
		return this;
	}
	
	public Email addRecipient(EmailAddress recipient) {
		addRecipient(new Recipient(recipient));
		return this;
	}
	
	public Email addRecipient(EmailAddress recipient, RecipientType type) {
		addRecipient(new Recipient(recipient, type));
		return this;
	}
	
	public static Recipient[] toRecipient(EmailAddress[] to) {
		Recipient[] addresses = new Recipient[to.length];
		int i = 0;
		for(EmailAddress t : to) {
			addresses[i++] = new Recipient(t);
		}
		return addresses;
	}
	
	public static Recipient[] toRecipient(String[] to) {
		Recipient[] addresses = new Recipient[to.length];
		int i = 0;
		for(String t : to) {
			addresses[i++] = new Recipient(t);
		}
		return addresses;
	}

	public Content getContent() {
		return content;
	}
	
	@Override
	public void setContent(Content content) {
		this.content = content;
	}

	public EmailAddress getFrom() {
		return from;
	}

	public void setFrom(EmailAddress from) {
		this.from = from;
	}
	
	public void setFrom(String from) {
		setFrom(new EmailAddress(from));
	}

	public List<Recipient> getRecipients() {
		return recipients;
	}

	public void setRecipients(List<Recipient> recipients) {
		this.recipients = recipients;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Email [subject=").append(subject).append(", content=").append(content).append(", from=").append(from).append(", recipients=").append(recipients).append(", attachments=").append(attachments).append("]");
		return builder.toString();
	}
}
