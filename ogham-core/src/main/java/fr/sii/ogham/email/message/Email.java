package fr.sii.ogham.email.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.message.capability.HasContentFluent;
import fr.sii.ogham.core.message.capability.HasRecipients;
import fr.sii.ogham.core.message.capability.HasRecipientsFluent;
import fr.sii.ogham.core.message.capability.HasSubject;
import fr.sii.ogham.core.message.capability.HasSubjectFluent;
import fr.sii.ogham.core.message.capability.HasToFluent;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;
import fr.sii.ogham.core.util.StringUtils;
import fr.sii.ogham.email.attachment.Attachment;

/**
 * Email message that contains the following information:
 * <ul>
 * <li>The subject of the mail</li>
 * <li>The body of the mail (see {@link Content} and sub classes for more
 * information)</li>
 * <li>The sender address</li>
 * <li>The list of recipient addresses with the type (to, cc, bcc)</li>
 * <li>The list of attachments to join to the mail</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class Email implements Message, HasContentFluent<Email>, HasSubject, HasSubjectFluent<Email>, HasRecipients<Recipient>, HasRecipientsFluent<Email, Recipient>, HasToFluent<Email> {
	/**
	 * The subject
	 */
	private String subject;

	/**
	 * The email body content
	 */
	private Content content;

	/**
	 * The sender address
	 */
	private EmailAddress from;

	/**
	 * The list of recipient addresses with the associated type (to, cc, bcc)
	 */
	private List<Recipient> recipients;

	/**
	 * The list of attachments to send with the email
	 */
	private List<Attachment> attachments;

	/**
	 * Instantiates an empty email
	 */
	public Email() {
		super();
		recipients = new ArrayList<>();
		attachments = new ArrayList<>();
	}

	// ----------------------- Getter/Setters -----------------------//

	@Override
	public Content getContent() {
		return content;
	}

	@Override
	public void setContent(Content content) {
		this.content = content;
	}

	/**
	 * Get the sender address
	 * 
	 * @return the sender address
	 */
	public EmailAddress getFrom() {
		return from;
	}

	/**
	 * Set the sender address.
	 * 
	 * @param from
	 *            the sender address
	 */
	public void setFrom(EmailAddress from) {
		this.from = from;
	}

	/**
	 * Set the sender address as string (typical address syntax is of the form
	 * "user@host.domain" or "Personal Name &lt;user@host.domain&gt;").
	 * 
	 * @param from
	 *            the sender address string (typical address syntax is of the
	 *            form "user@host.domain" or "Personal Name
	 *            &lt;user@host.domain&gt;").
	 */
	public void setFrom(String from) {
		setFrom(new EmailAddress(from));
	}

	/**
	 * Get the list of recipients of the mail.
	 * 
	 * @return the list of recipients
	 */
	@Override
	public List<Recipient> getRecipients() {
		return recipients;
	}

	/**
	 * Set the whole list of recipients.
	 * 
	 * @param recipients
	 *            the list of recipients
	 */
	@Override
	public void setRecipients(List<Recipient> recipients) {
		this.recipients = recipients;
	}

	/**
	 * Get the subject of the mail.
	 * 
	 * @return the subject of the mail
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Set the subject of the mail.
	 * 
	 * @param subject
	 *            the subject of the mail
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Get the list of attachments.
	 * 
	 * @return the list of attachments
	 */
	public List<Attachment> getAttachments() {
		return attachments;
	}

	/**
	 * Set the whole list of attachments.
	 * 
	 * @param attachments
	 *            the list of attachments
	 */
	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	// ----------------------- Fluent API -----------------------//

	/**
	 * Set the subject of the mail.
	 * 
	 * @param subject
	 *            the subject of the mail
	 * @return this instance for fluent chaining
	 */
	public Email subject(String subject) {
		setSubject(subject);
		return this;
	}

	/**
	 * Set the content (body) of the message.
	 * 
	 * @param content
	 *            the content of the message
	 * @return this instance for fluent chaining
	 */
	public Email content(Content content) {
		setContent(content);
		return this;
	}

	/**
	 * Set the content (body) of the message.
	 * 
	 * @param content
	 *            the content of the message
	 * @return this instance for fluent chaining
	 */
	public Email content(String content) {
		return content(new StringContent(content));
	}

	/**
	 * Set the sender address. It supports both "user@domain.host" and "Personal
	 * Name &lt;user@host.domain&gt;" formats.
	 * 
	 * @param from
	 *            the sender address
	 * @return this instance for fluent chaining
	 */
	public Email from(EmailAddress from) {
		setFrom(from);
		return this;
	}

	/**
	 * Set the sender address as string (typical address syntax is of the form
	 * "user@host.domain" or "Personal Name &lt;user@host.domain&gt;").
	 * 
	 * @param from
	 *            the sender address string (typical address syntax is of the
	 *            form "user@host.domain" or "Personal Name
	 *            &lt;user@host.domain&gt;").
	 * @return this instance for fluent chaining
	 */
	public Email from(String from) {
		setFrom(from);
		return this;
	}

	/**
	 * Set the whole list of recipients.
	 * 
	 * @param recipients
	 *            the list of recipients
	 * @return this instance for fluent chaining
	 */
	@Override
	public Email recipients(List<Recipient> recipients) {
		setRecipients(recipients);
		return this;
	}

	/**
	 * Set the whole list of attachments.
	 * 
	 * @param attachments
	 *            the list of attachments
	 * @return this instance for fluent chaining
	 */
	public Email attach(List<Attachment> attachments) {
		setAttachments(attachments);
		return this;
	}

	/**
	 * Set the whole list of attachments.
	 * 
	 * @param attachments
	 *            the list of attachments
	 * @return this instance for fluent chaining
	 */
	public Email attach(Attachment... attachments) {
		for (Attachment attachment : attachments) {
			attach(attachment);
		}
		return this;
	}

	/**
	 * Add an attachment to join to the mail.
	 * 
	 * @param attachment
	 *            the attachment to add
	 * @return this instance for fluent chaining
	 */
	public Email attach(Attachment attachment) {
		attachments.add(attachment);
		return this;
	}

	/**
	 * Add a recipient of the mail.
	 * 
	 * @param recipients
	 *            one or several recipient to add
	 * @return this instance for fluent chaining
	 */
	@Override
	public Email recipient(Recipient... recipients) {
		this.recipients.addAll(Arrays.asList(recipients));
		return this;
	}

	/**
	 * Add a "to" recipient address. It supports both "user@domain.host" and
	 * "Personal Name &lt;user@host.domain&gt;" formats.
	 * 
	 * @param to
	 *            one or several recipient addresses
	 * @return this instance for fluent chaining
	 */
	public Email to(String... to) {
		for (String t : to) {
			to(new EmailAddress(t));
		}
		return this;
	}

	/**
	 * Add a "to" recipient address.
	 * 
	 * @param to
	 *            one or several recipient addresses
	 * @return this instance for fluent chaining
	 */
	public Email to(EmailAddress... to) {
		for (EmailAddress t : to) {
			recipient(t, RecipientType.TO);
		}
		return this;
	}

	/**
	 * Add a "cc" recipient address. It supports both "user@domain.host" and
	 * "Personal Name &lt;user@host.domain&gt;" formats.
	 * 
	 * @param cc
	 *            one or several recipient addresses
	 * @return this instance for fluent chaining
	 */
	public Email cc(String... cc) {
		for (String c : cc) {
			cc(new EmailAddress(c));
		}
		return this;
	}

	/**
	 * Add a "cc" recipient address.
	 * 
	 * @param cc
	 *            one or several recipient addresses
	 * @return this instance for fluent chaining
	 */
	public Email cc(EmailAddress... cc) {
		for (EmailAddress c : cc) {
			recipient(c, RecipientType.CC);
		}
		return this;
	}

	/**
	 * Add a "bcc" recipient address. It supports both "user@domain.host" and
	 * "Personal Name &lt;user@host.domain&gt;" formats.
	 * 
	 * @param bcc
	 *            one or several recipient addresses
	 * @return this instance for fluent chaining
	 */
	public Email bcc(String... bcc) {
		for (String b : bcc) {
			bcc(new EmailAddress(b));
		}
		return this;
	}

	/**
	 * Add a "bcc" recipient address.
	 * 
	 * @param bcc
	 *            one or several recipient addresses
	 * @return this instance for fluent chaining
	 */
	public Email bcc(EmailAddress... bcc) {
		for (EmailAddress b : bcc) {
			recipient(b, RecipientType.BCC);
		}
		return this;
	}

	/**
	 * Add a recipient specifying its address and the type (to, cc, bcc).
	 * 
	 * @param recipient
	 *            the recipient address
	 * @param type
	 *            the type (to, cc, bcc)
	 * @return this instance for fluent chaining
	 */
	public Email recipient(EmailAddress recipient, RecipientType type) {
		recipient(new Recipient(recipient, type));
		return this;
	}

	// ----------------------- Utilities -----------------------//

	/**
	 * Converts a list of string to a list of recipients. Each recipient will
	 * have the type {@link RecipientType#TO}.
	 * 
	 * @param to
	 *            the list of addresses to convert (typical address syntax is of
	 *            the form "user@host.domain" or "Personal Name
	 *            &lt;user@host.domain&gt;")
	 * @return the list of recipients
	 */
	public static Recipient[] toRecipient(List<String> to) {
		Recipient[] addresses = new Recipient[to.size()];
		int i = 0;
		for (String t : to) {
			addresses[i++] = new Recipient(t);
		}
		return addresses;
	}

	/**
	 * Converts a list of {@link EmailAddress} to a list of recipients. Each
	 * recipient will have the type {@link RecipientType#TO}.
	 * 
	 * @param to
	 *            the list of addresses to convert
	 * @return the list of recipients
	 */
	public static Recipient[] toRecipient(EmailAddress[] to) {
		Recipient[] addresses = new Recipient[to.length];
		int i = 0;
		for (EmailAddress t : to) {
			addresses[i++] = new Recipient(t);
		}
		return addresses;
	}

	/**
	 * Converts a list of string to a list of recipients. Each recipient will
	 * have the type {@link RecipientType#TO}.
	 * 
	 * @param to
	 *            the list of addresses to convert (typical address syntax is of
	 *            the form "user@host.domain" or "Personal Name
	 *            &lt;user@host.domain&gt;")
	 * @return the list of recipients
	 */
	public static Recipient[] toRecipient(String[] to) {
		Recipient[] addresses = new Recipient[to.length];
		int i = 0;
		for (String t : to) {
			addresses[i++] = new Recipient(t);
		}
		return addresses;
	}

	@Override
	public String toString() {
		return toString(true);
	}

	public String toSummaryString() {
		return toString(false);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(subject, content, from, recipients, attachments).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("subject", "content", "from", "recipients", "attachments").isEqual();
	}

	private String toString(boolean includeContent) {
		StringBuilder builder = new StringBuilder();
		builder.append("Email message\r\nFrom: ").append(from);
		for (RecipientType type : RecipientType.values()) {
			List<EmailAddress> addresses = new ArrayList<>();
			for (Recipient recipient : recipients) {
				if (type.equals(recipient.getType())) {
					addresses.add(recipient.getAddress());
				}
			}
			if (!addresses.isEmpty()) {
				builder.append("\r\n");
				builder.append(type).append(": ");
				builder.append(StringUtils.join(addresses, ", "));
			}
		}
		builder.append("\r\nSubject: ").append(subject);
		builder.append("\r\n----------------------------------\r\n").append(includeContent ? content : "<Content skipped>");
		if (attachments != null && !attachments.isEmpty()) {
			builder.append("\r\n----------------------------------").append("\r\nAttachments: ").append(attachments);
		}
		builder.append("\r\n==================================\r\n");
		return builder.toString();
	}
	
}
