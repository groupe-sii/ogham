package fr.sii.notification.email.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.notification.core.message.Message;
import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.message.content.StringContent;
import fr.sii.notification.core.util.ArrayUtils;
import fr.sii.notification.core.util.EqualsBuilder;
import fr.sii.notification.core.util.HashCodeBuilder;
import fr.sii.notification.core.util.StringUtils;
import fr.sii.notification.email.attachment.Attachment;
import fr.sii.notification.email.builder.EmailBuilder;

/**
 * Email message that contains the following information:
 * <ul>
 * <li>The subject of the mail</li>
 * <li>The body of the mail (see {@link Content} and sub classes for more
 * information</li>
 * <li>The sender address</li>
 * <li>The list of recipient addresses with the type (to, cc, bcc)</li>
 * <li>The list of attachments to join to the mail</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class Email implements Message {
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
	 * Initialize the email with the following information:
	 * <ul>
	 * <li>The subject of the mail</li>
	 * <li>The body of the mail (see {@link Content} and sub classes for more
	 * information</li>
	 * <li>The sender address</li>
	 * <li>None, one or several "to" recipient addresses (typical address syntax
	 * is of the form "user@host.domain" or
	 * "Personal Name &lt;user@host.domain&gt;"), it will create a list of
	 * {@link Recipient} with {@link RecipientType#TO} for you</li>
	 * </ul>
	 * 
	 * <p>
	 * No sender address is specified. This information can be automatically
	 * added according to the behaviors you have selected see
	 * {@link EmailBuilder} for more information). Alternatively, you can
	 * manually add it using {@link #setFrom(EmailAddress)} or
	 * {@link #setFrom(String)} methods.
	 * </p>
	 * <p>
	 * No attachment is added to the email. You can add attachments later by
	 * calling {@link #setAttachments(List)} or
	 * {@link #addAttachment(Attachment)}.
	 * </p>
	 * 
	 * @param subject
	 *            the subject of the mail
	 * @param content
	 *            the body of the mail
	 * @param to
	 *            the list of "to" recipients of the mail
	 */
	public Email(String subject, String content, String... to) {
		this(subject, new StringContent(content), toRecipient(to));
	}
	
	/**
	 * Initialize the email with the following information:
	 * <ul>
	 * <li>The subject of the mail</li>
	 * <li>The body of the mail (see {@link Content} and sub classes for more
	 * information</li>
	 * <li>The sender address</li>
	 * <li>Array of recipients</li>
	 * </ul>
	 * 
	 * <p>
	 * No sender address is specified. This information can be automatically
	 * added according to the behaviors you have selected see
	 * {@link EmailBuilder} for more information). Alternatively, you can
	 * manually add it using {@link #setFrom(EmailAddress)} or
	 * {@link #setFrom(String)} methods.
	 * </p>
	 * <p>
	 * No attachment is added to the email. You can add attachments later by
	 * calling {@link #setAttachments(List)} or
	 * {@link #addAttachment(Attachment)}.
	 * </p>
	 * 
	 * @param subject
	 *            the subject of the mail
	 * @param content
	 *            the body of the mail
	 * @param recipients
	 *            the array of recipients of the mail
	 */
	public Email(String subject, Content content, Recipient[] recipients) {
		this(subject, content, new ArrayList<>(Arrays.asList(recipients)));
	}

	/**
	 * Initialize the email with the following information:
	 * <ul>
	 * <li>The subject of the mail</li>
	 * <li>The body of the mail (see {@link Content} and sub classes for more
	 * information</li>
	 * <li>The sender address</li>
	 * <li>One or several recipients</li>
	 * </ul>
	 * 
	 * <p>
	 * No sender address is specified. This information can be automatically
	 * added according to the behaviors you have selected see
	 * {@link EmailBuilder} for more information). Alternatively, you can
	 * manually add it using {@link #setFrom(EmailAddress)} or
	 * {@link #setFrom(String)} methods.
	 * </p>
	 * <p>
	 * No attachment is added to the email. You can add attachments later by
	 * calling {@link #setAttachments(List)} or
	 * {@link #addAttachment(Attachment)}.
	 * </p>
	 * 
	 * @param subject
	 *            the subject of the mail
	 * @param content
	 *            the body of the mail
	 * @param recipient
	 *            the first recipient of the mail (force to have at least one
	 *            recipient)
	 * @param recipients
	 *            the list of recipients of the mail
	 */
	public Email(String subject, Content content, Recipient recipient, Recipient... recipients) {
		this(subject, content, new ArrayList<>(Arrays.asList(recipients)));
	}

	/**
	 * Initialize the email with the following information:
	 * <ul>
	 * <li>The subject of the mail</li>
	 * <li>The body of the mail (see {@link Content} and sub classes for more
	 * information</li>
	 * <li>The sender address</li>
	 * <li>None, one or several "to" recipient addresses, it will create a list
	 * of {@link Recipient} with {@link RecipientType#TO} for you</li>
	 * </ul>
	 * 
	 * <p>
	 * No sender address is specified. This information can be automatically
	 * added according to the behaviors you have selected see
	 * {@link EmailBuilder} for more information). Alternatively, you can
	 * manually add it using {@link #setFrom(EmailAddress)} or
	 * {@link #setFrom(String)} methods.
	 * </p>
	 * <p>
	 * No attachment is added to the email. You can add attachments later by
	 * calling {@link #setAttachments(List)} or
	 * {@link #addAttachment(Attachment)}.
	 * </p>
	 * 
	 * @param subject
	 *            the subject of the mail
	 * @param content
	 *            the body of the mail
	 * @param recipients
	 *            the list of "to" recipients of the mail
	 */
	public Email(String subject, Content content, List<Recipient> recipients) {
		this(subject, content, recipients, new ArrayList<Attachment>());
	}

	/**
	 * Initialize the email with the following information:
	 * <ul>
	 * <li>The subject of the mail</li>
	 * <li>The body of the mail (see {@link Content} and sub classes for more
	 * information</li>
	 * <li>The sender address</li>
	 * <li>None, one or several "to" recipient addresses, it will create a list of
	 * {@link Recipient} with {@link RecipientType#TO} for you</li>
	 * </ul>
	 * 
	 * <p>
	 * No attachment is added to the email. You can add attachments later by
	 * calling {@link #setAttachments(List)} or
	 * {@link #addAttachment(Attachment)}.
	 * </p>
	 * 
	 * @param subject
	 *            the subject of the mail
	 * @param content
	 *            the body of the mail
	 * @param from
	 *            the sender address
	 * @param to
	 *            the list of "to" recipients of the mail
	 */
	public Email(String subject, Content content, EmailAddress from, EmailAddress... to) {
		this(subject, content, from, new ArrayList<>(Arrays.asList(toRecipient(to))));
	}

	/**
	 * Initialize the email with the following information:
	 * <ul>
	 * <li>The subject of the mail</li>
	 * <li>The body of the mail (see {@link Content} and sub classes for more
	 * information</li>
	 * <li>The sender address</li>
	 * <li>None, one or several "to" recipient addresses, it will create a list
	 * of {@link Recipient} with {@link RecipientType#TO} for you</li>
	 * </ul>
	 * 
	 * <p>
	 * No sender address is specified. This information can be automatically
	 * added according to the behaviors you have selected see
	 * {@link EmailBuilder} for more information). Alternatively, you can
	 * manually add it using {@link #setFrom(EmailAddress)} or
	 * {@link #setFrom(String)} methods.
	 * </p>
	 * <p>
	 * No attachment is added to the email. You can add attachments later by
	 * calling {@link #setAttachments(List)} or
	 * {@link #addAttachment(Attachment)}.
	 * </p>
	 * 
	 * @param subject
	 *            the subject of the mail
	 * @param content
	 *            the body of the mail
	 * @param to
	 *            the list of "to" recipients of the mail
	 */
	public Email(String subject, Content content, String... to) {
		this(subject, content, new ArrayList<>(Arrays.asList(toRecipient(to))));
	}

	/**
	 * Initialize the email with the following information:
	 * <ul>
	 * <li>The subject of the mail</li>
	 * <li>The body of the mail (see {@link Content} and sub classes for more
	 * information</li>
	 * <li>The sender address</li>
	 * <li>None, one or several "to" recipient addresses (typical address syntax
	 * is of the form "user@host.domain" or
	 * "Personal Name &lt;user@host.domain&gt;"), it will create a list of
	 * {@link Recipient} with {@link RecipientType#TO} for you</li>
	 * </ul>
	 * 
	 * <p>
	 * No attachment is added to the email. You can add attachments later by
	 * calling {@link #setAttachments(List)} or
	 * {@link #addAttachment(Attachment)}.
	 * </p>
	 * 
	 * @param subject
	 *            the subject of the mail
	 * @param content
	 *            the body of the mail
	 * @param from
	 *            the sender address
	 * @param to
	 *            the list of "to" recipients of the mail
	 */
	public Email(String subject, String content, EmailAddress from, String... to) {
		this(subject, new StringContent(content), from, toRecipient(to));
	}

	/**
	 * Initialize the email with the following information:
	 * <ul>
	 * <li>The subject of the mail</li>
	 * <li>The body of the mail (see {@link Content} and sub classes for more
	 * information</li>
	 * <li>The sender address</li>
	 * <li>Array of recipients</li>
	 * </ul>
	 * 
	 * <p>
	 * No attachment is added to the email. You can add attachments later by
	 * calling {@link #setAttachments(List)} or
	 * {@link #addAttachment(Attachment)}.
	 * </p>
	 * 
	 * @param subject
	 *            the subject of the mail
	 * @param content
	 *            the body of the mail
	 * @param from
	 *            the sender address
	 * @param recipients
	 *            the array of recipients of the mail
	 */
	public Email(String subject, Content content, EmailAddress from, Recipient[] recipients) {
		this(subject, content, from, new ArrayList<>(Arrays.asList(recipients)));
	}

	/**
	 * Initialize the email with the following information:
	 * <ul>
	 * <li>The subject of the mail</li>
	 * <li>The body of the mail (see {@link Content} and sub classes for more
	 * information</li>
	 * <li>The sender address</li>
	 * <li>One or several recipients</li>
	 * </ul>
	 * 
	 * <p>
	 * No attachment is added to the email. You can add attachments later by
	 * calling {@link #setAttachments(List)} or
	 * {@link #addAttachment(Attachment)}.
	 * </p>
	 * 
	 * @param subject
	 *            the subject of the mail
	 * @param content
	 *            the body of the mail
	 * @param from
	 *            the sender address
	 * @param recipient
	 *            the first recipient (force to have at least one recipient)
	 * @param recipients
	 *            the list of recipients of the mail
	 */
	public Email(String subject, Content content, EmailAddress from, Recipient recipient, Recipient... recipients) {
		this(subject, content, from, new ArrayList<>(Arrays.asList(ArrayUtils.concat(recipient, recipients))));
	}

	/**
	 * Initialize the email with the following information:
	 * <ul>
	 * <li>The subject of the mail</li>
	 * <li>The body of the mail (see {@link Content} and sub classes for more
	 * information</li>
	 * <li>The sender address</li>
	 * <li>The list of recipients</li>
	 * </ul>
	 * 
	 * <p>
	 * No attachment is added to the email. You can add attachments later by
	 * calling {@link #setAttachments(List)} or
	 * {@link #addAttachment(Attachment)}.
	 * </p>
	 * 
	 * @param subject
	 *            the subject of the mail
	 * @param content
	 *            the body of the mail
	 * @param from
	 *            the sender address
	 * @param recipients
	 *            the list of recipients of the mail
	 */
	public Email(String subject, Content content, EmailAddress from, List<Recipient> recipients) {
		this(subject, content, from, recipients, new ArrayList<Attachment>());
	}

	/**
	 * Initialize the email with the following information:
	 * <ul>
	 * <li>The subject of the mail</li>
	 * <li>The body of the mail as string, it will create a
	 * {@link StringContent} for you</li>
	 * <li>The single address used in to field (typical address syntax is of the
	 * form "user@host.domain" or "Personal Name &lt;user@host.domain&gt;"), it
	 * will create a {@link Recipient} with {@link RecipientType#TO} for you</li>
	 * <li>One or several attachments to join to the mail</li>
	 * </ul>
	 * 
	 * <p>
	 * No sender address is specified. This information can be automatically
	 * added according to the behaviors you have selected see
	 * {@link EmailBuilder} for more information). Alternatively, you can
	 * manually add it using {@link #setFrom(EmailAddress)} or
	 * {@link #setFrom(String)} methods.
	 * </p>
	 * 
	 * @param subject
	 *            the subject of the mail
	 * @param content
	 *            the body of the mail
	 * @param to
	 *            the address of the single "to" recipient of the mail
	 * @param attachment
	 *            one required attachment (force to have at least one
	 *            attachment)
	 * @param attachments
	 *            the list of other attachments
	 */
	public Email(String subject, String content, String to, Attachment attachment, Attachment... attachments) {
		this(subject, new StringContent(content), new Recipient(to), ArrayUtils.concat(attachment, attachments));
	}

	/**
	 * Initialize the email with the following information:
	 * <ul>
	 * <li>The subject of the mail</li>
	 * <li>The body of the mail as string, it will create a
	 * {@link StringContent} for you</li>
	 * <li>The list of "to" recipient addresses (typical address syntax is of
	 * the form "user@host.domain" or "Personal Name &lt;user@host.domain&gt;"),
	 * it will create a list of {@link Recipient} with {@link RecipientType#TO}
	 * type for you</li>
	 * <li>None, one or several attachments to join to the mail</li>
	 * </ul>
	 * 
	 * <p>
	 * No sender address is specified. This information can be automatically
	 * added according to the behaviors you have selected see
	 * {@link EmailBuilder} for more information). Alternatively, you can
	 * manually add it using {@link #setFrom(EmailAddress)} or
	 * {@link #setFrom(String)} methods.
	 * </p>
	 * 
	 * @param subject
	 *            the subject of the mail
	 * @param content
	 *            the body of the mail
	 * @param to
	 *            the address of the single "to" recipient of the mail
	 * @param attachments
	 *            the list of attachments
	 */
	public Email(String subject, String content, List<String> to, Attachment... attachments) {
		this(subject, new StringContent(content), new ArrayList<>(Arrays.asList(toRecipient(to))), attachments);
	}

	/**
	 * Initialize the email with the following information:
	 * <ul>
	 * <li>The subject of the mail</li>
	 * <li>The body of the mail (see {@link Content} and sub classes for more
	 * information</li>
	 * <li>A single recipient with its type (to, cc, bcc)</li>
	 * <li>Array of attachments to join to the mail</li>
	 * </ul>
	 * 
	 * <p>
	 * No sender address is specified. This information can be automatically
	 * added according to the behaviors you have selected see
	 * {@link EmailBuilder} for more information). Alternatively, you can
	 * manually add it using {@link #setFrom(EmailAddress)} or
	 * {@link #setFrom(String)} methods.
	 * </p>
	 * 
	 * @param subject
	 *            the subject of the mail
	 * @param content
	 *            the body of the mail
	 * @param recipient
	 *            the recipient of the mail
	 * @param attachments
	 *            the array of attachments
	 */
	public Email(String subject, Content content, Recipient recipient, Attachment[] attachments) {
		this(subject, content, recipient, new ArrayList<>(Arrays.asList(attachments)));
	}

	/**
	 * Initialize the email with the following information:
	 * <ul>
	 * <li>The subject of the mail</li>
	 * <li>The body of the mail (see {@link Content} and sub classes for more
	 * information</li>
	 * <li>A single recipient with its type (to, cc, bcc)</li>
	 * <li>One or several attachments to join to the mail</li>
	 * </ul>
	 * 
	 * <p>
	 * No sender address is specified. This information can be automatically
	 * added according to the behaviors you have selected see
	 * {@link EmailBuilder} for more information). Alternatively, you can
	 * manually add it using {@link #setFrom(EmailAddress)} or
	 * {@link #setFrom(String)} methods.
	 * </p>
	 * 
	 * @param subject
	 *            the subject of the mail
	 * @param content
	 *            the body of the mail
	 * @param recipient
	 *            the recipient of the mail
	 * @param attachment
	 *            the first attachment (force to have at least one attachment)
	 * @param attachments
	 *            the list of attachments
	 */
	public Email(String subject, Content content, Recipient recipient, Attachment attachment, Attachment... attachments) {
		this(subject, content, recipient, new ArrayList<>(Arrays.asList(ArrayUtils.concat(attachment, attachments))));
	}

	/**
	 * Initialize the email with the following information:
	 * <ul>
	 * <li>The subject of the mail</li>
	 * <li>The body of the mail (see {@link Content} and sub classes for more
	 * information</li>
	 * <li>The list of recipient addresses with the type (to, cc, bcc)</li>
	 * <li>None, one or several attachments to join to the mail</li>
	 * </ul>
	 * 
	 * <p>
	 * No sender address is specified. This information can be automatically
	 * added according to the behaviors you have selected see
	 * {@link EmailBuilder} for more information). Alternatively, you can
	 * manually add it using {@link #setFrom(EmailAddress)} or
	 * {@link #setFrom(String)} methods.
	 * </p>
	 * 
	 * @param subject
	 *            the subject of the mail
	 * @param content
	 *            the body of the mail
	 * @param recipients
	 *            the list of recipients of the mail
	 * @param attachments
	 *            the list of attachments
	 */
	public Email(String subject, Content content, List<Recipient> recipients, Attachment... attachments) {
		this(subject, content, recipients, new ArrayList<>(Arrays.asList(attachments)));
	}

	/**
	 * Initialize the email with the following information:
	 * <ul>
	 * <li>The subject of the mail</li>
	 * <li>The body of the mail (see {@link Content} and sub classes for more
	 * information</li>
	 * <li>A single recipient with its type (to, cc, bcc)</li>
	 * <li>The list of attachments to join to the mail</li>
	 * </ul>
	 * 
	 * <p>
	 * No sender address is specified. This information can be automatically
	 * added according to the behaviors you have selected see
	 * {@link EmailBuilder} for more information). Alternatively, you can
	 * manually add it using {@link #setFrom(EmailAddress)} or
	 * {@link #setFrom(String)} methods.
	 * </p>
	 * 
	 * @param subject
	 *            the subject of the mail
	 * @param content
	 *            the body of the mail
	 * @param recipient
	 *            the recipient of the mail
	 * @param attachments
	 *            the list of attachments
	 */
	public Email(String subject, Content content, Recipient recipient, List<Attachment> attachments) {
		this(subject, content, new ArrayList<>(Arrays.asList(recipient)), attachments);
	}

	/**
	 * Initialize the email with the following information:
	 * <ul>
	 * <li>The subject of the mail</li>
	 * <li>The body of the mail (see {@link Content} and sub classes for more
	 * information</li>
	 * <li>The list of recipient addresses with the type (to, cc, bcc)</li>
	 * <li>The list of attachments to join to the mail</li>
	 * </ul>
	 * 
	 * <p>
	 * No sender address is specified. This information can be automatically
	 * added according to the behaviors you have selected see
	 * {@link EmailBuilder} for more information). Alternatively, you can
	 * manually add it using {@link #setFrom(EmailAddress)} or
	 * {@link #setFrom(String)} methods.
	 * </p>
	 * 
	 * @param subject
	 *            the subject of the mail
	 * @param content
	 *            the body of the mail
	 * @param recipients
	 *            the list of recipients of the mail
	 * @param attachments
	 *            the list of attachments
	 */
	public Email(String subject, Content content, List<Recipient> recipients, List<Attachment> attachments) {
		this(subject, content, null, recipients, attachments);
	}

	/**
	 * Initialize the email with the following information:
	 * <ul>
	 * <li>The subject of the mail</li>
	 * <li>The body of the mail (see {@link Content} and sub classes for more
	 * information</li>
	 * <li>The sender address</li>
	 * <li>The list of recipient addresses with the type (to, cc, bcc)</li>
	 * <li>None, one or several attachments to join to the mail</li>
	 * </ul>
	 * 
	 * @param subject
	 *            the subject of the mail
	 * @param content
	 *            the body of the mail
	 * @param from
	 *            the sender address
	 * @param recipients
	 *            the list of recipients of the mail
	 * @param attachments
	 *            the list of attachments
	 */
	public Email(String subject, Content content, EmailAddress from, List<Recipient> recipients, Attachment... attachments) {
		this(subject, content, from, recipients, new ArrayList<>(Arrays.asList(attachments)));
	}

	/**
	 * Initialize the email with the following information:
	 * <ul>
	 * <li>The subject of the mail</li>
	 * <li>The body of the mail (see {@link Content} and sub classes for more
	 * information</li>
	 * <li>The sender address</li>
	 * <li>The list of recipient addresses with the type (to, cc, bcc)</li>
	 * <li>The list of attachments to join to the mail</li>
	 * </ul>
	 * 
	 * @param subject
	 *            the subject of the mail
	 * @param content
	 *            the body of the mail
	 * @param from
	 *            the sender address
	 * @param recipients
	 *            the list of recipients of the mail
	 * @param attachments
	 *            the list of attachments
	 */
	public Email(String subject, Content content, EmailAddress from, List<Recipient> recipients, List<Attachment> attachments) {
		super();
		this.subject = subject;
		this.content = content;
		this.from = from;
		this.recipients = recipients;
		this.attachments = attachments;
	}

	/**
	 * Add an attachment to join to the mail.
	 * 
	 * @param attachment
	 *            the attachment to add
	 * @return this instance for fluent use
	 */
	public Email addAttachment(Attachment attachment) {
		attachments.add(attachment);
		return this;
	}

	/**
	 * Add a recipient of the mail.
	 * 
	 * @param recipient
	 *            the recipient to add
	 * @return this instance for fluent use
	 */
	public Email addRecipient(Recipient recipient) {
		recipients.add(recipient);
		return this;
	}

	/**
	 * Add a "to" recipient address.
	 * 
	 * @param to
	 *            the recipient address
	 * @return this instance for fluent use
	 */
	public Email addTo(String to) {
		addTo(new EmailAddress(to));
		return this;
	}

	/**
	 * Add a "to" recipient address.
	 * 
	 * @param to
	 *            the recipient address
	 * @return this instance for fluent use
	 */
	public Email addTo(EmailAddress to) {
		addRecipient(to, RecipientType.TO);
		return this;
	}

	/**
	 * Add a "cc" recipient address.
	 * 
	 * @param cc
	 *            the recipient address
	 * @return this instance for fluent use
	 */
	public Email addCc(String cc) {
		addCc(new EmailAddress(cc));
		return this;
	}

	/**
	 * Add a "cc" recipient address.
	 * 
	 * @param cc
	 *            the recipient address
	 * @return this instance for fluent use
	 */
	public Email addCc(EmailAddress cc) {
		addRecipient(cc, RecipientType.CC);
		return this;
	}

	/**
	 * Add a "cc" recipient address.
	 * 
	 * @param cc
	 *            the recipient address
	 * @return this instance for fluent use
	 */
	public Email addBcc(String cc) {
		addBcc(new EmailAddress(cc));
		return this;
	}

	/**
	 * Add a "bcc" recipient address.
	 * 
	 * @param bcc
	 *            the recipient address
	 * @return this instance for fluent use
	 */
	public Email addBcc(EmailAddress bcc) {
		addRecipient(bcc, RecipientType.BCC);
		return this;
	}

	/**
	 * Add a recipient specifying his address and the type (to, cc, bcc).
	 * 
	 * @param recipient
	 *            the recipient address
	 * @param type
	 *            the type (to, cc, bcc)
	 * @return this instance for fluent use
	 */
	public Email addRecipient(EmailAddress recipient, RecipientType type) {
		addRecipient(new Recipient(recipient, type));
		return this;
	}

	/**
	 * Converts a list of string to a list of recipients. Each recipient will
	 * have the type {@link RecipientType#TO}.
	 * 
	 * @param to
	 *            the list of addresses to convert (typical address syntax is of
	 *            the form "user@host.domain" or
	 *            "Personal Name &lt;user@host.domain&gt;")
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
	 *            the form "user@host.domain" or
	 *            "Personal Name &lt;user@host.domain&gt;")
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
	public Content getContent() {
		return content;
	}

	@Override
	public void setContent(Content content) {
		this.content = content;
	}

	/**
	 * Set the content of the message.
	 * 
	 * @param content
	 *            the content of the message
	 * @return this instance for fluent use
	 */
	public Email withContent(Content content) {
		setContent(content);
		return this;
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
	 * Set the sender address.
	 * 
	 * @param from
	 *            the sender address
	 * @return this instance for fluent use
	 */
	public Email withFrom(EmailAddress from) {
		setFrom(from);
		return this;
	}

	/**
	 * Set the sender address as string (typical address syntax is of the form
	 * "user@host.domain" or "Personal Name &lt;user@host.domain&gt;").
	 * 
	 * @param from
	 *            the sender address string (typical address syntax is of the
	 *            form "user@host.domain" or
	 *            "Personal Name &lt;user@host.domain&gt;").
	 */
	public void setFrom(String from) {
		setFrom(new EmailAddress(from));
	}

	/**
	 * Set the sender address as string (typical address syntax is of the form
	 * "user@host.domain" or "Personal Name &lt;user@host.domain&gt;").
	 * 
	 * @param from
	 *            the sender address string (typical address syntax is of the
	 *            form "user@host.domain" or
	 *            "Personal Name &lt;user@host.domain&gt;").
	 * @return this instance for fluent use
	 */
	public Email withFrom(String from) {
		setFrom(from);
		return this;
	}

	/**
	 * Get the list of recipients of the mail.
	 * 
	 * @return the list of recipients
	 */
	public List<Recipient> getRecipients() {
		return recipients;
	}

	/**
	 * Set the whole list of recipients.
	 * 
	 * @param recipients
	 *            the list of recipients
	 */
	public void setRecipients(List<Recipient> recipients) {
		this.recipients = recipients;
	}

	/**
	 * Set the whole list of recipients.
	 * 
	 * @param recipients
	 *            the list of recipients
	 * @return this instance for fluent use
	 */
	public Email withRecipients(List<Recipient> recipients) {
		setRecipients(recipients);
		return this;
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
	 * Set the subject of the mail.
	 * 
	 * @param subject
	 *            the subject of the mail
	 * @return this instance for fluent use
	 */
	public Email withSubject(String subject) {
		setSubject(subject);
		return this;
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

	/**
	 * Set the whole list of attachments.
	 * 
	 * @param attachments
	 *            the list of attachments
	 * @return this instance for fluent use
	 */
	public Email withAttachments(List<Attachment> attachments) {
		setAttachments(attachments);
		return this;
	}

	@Override
	public String toString() {
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
		builder.append("\r\nSubject: ").append(subject).append("\r\n----------------------------------\r\n").append(content);
		if (attachments != null && !attachments.isEmpty()) {
			builder.append("\r\n----------------------------------").append("\r\nAttachments: ").append(attachments);
		}
		builder.append("\r\n==================================\r\n");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(subject).append(content).append(from).append(recipients).append(attachments).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("subject", "content", "from", "recipients", "attachments").isEqual();
	}
	
	
}
