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
import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.message.fluent.SingleContentBuilder;
import fr.sii.ogham.core.util.Loggable;
import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;
import fr.sii.ogham.core.util.StringUtils;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.attachment.ContentDisposition;
import fr.sii.ogham.email.message.fluent.AttachBuilder;
import fr.sii.ogham.email.message.fluent.BodyBuilder;
import fr.sii.ogham.email.message.fluent.EmbedBuilder;

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
public class Email implements Message, HasContentFluent<Email>, HasSubject, HasSubjectFluent<Email>, HasRecipients<Recipient>, HasRecipientsFluent<Email, Recipient>, HasToFluent<Email>, Loggable {
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

	private final SingleContentBuilder<Email> htmlBuilder;
	private final SingleContentBuilder<Email> textBuilder;
	private final BodyBuilder bodyBuilder;
	private final AttachBuilder attachBuilder;
	private final EmbedBuilder embedBuilder;

	/**
	 * Instantiates an empty email
	 */
	public Email() {
		super();
		recipients = new ArrayList<>();
		attachments = new ArrayList<>();
		htmlBuilder = new SingleContentBuilder<>(this);
		textBuilder = new SingleContentBuilder<>(this);
		bodyBuilder = new BodyBuilder(this);
		attachBuilder = new AttachBuilder(this);
		embedBuilder = new EmbedBuilder(this);
	}

	// ----------------------- Getter/Setters -----------------------//

	@Override
	public Content getContent() {
		if (content != null) {
			return content;
		}
		return buildContent();
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
		List<Attachment> merged = new ArrayList<>();
		// NOTE: normally it can't be null but EqualsVerifier uses reflection to
		// set it to null
		if (attachments != null) {
			merged.addAll(attachments);
		}
		// NOTE: normally it can't be null but EqualsVerifier uses reflection to
		// set it to null
		if (attachBuilder != null) {
			merged.addAll(attachBuilder.build());
		}
		// NOTE: normally it can't be null but EqualsVerifier uses reflection to
		// set it to null
		if (embedBuilder != null) {
			merged.addAll(embedBuilder.build());
		}
		return merged;
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
	 * <p>
	 * You can use this method to explicitly set a particular {@link Content}
	 * instance. For example:
	 * 
	 * <pre>
	 * {@code
	 * .content(new TemplateContent("path/to/template", obj));
	 * }
	 * </pre>
	 * 
	 * <p>
	 * If you prefer, you can instead use the fluent API to set the email
	 * content (body):
	 * 
	 * <pre>
	 * {@code
	 * .body().template("path/to/template", obj)
	 * }
	 * </pre>
	 * 
	 * @param content
	 *            the content of the message
	 * @return this instance for fluent chaining
	 * @see #body()
	 * @see #html()
	 * @see #text()
	 */
	public Email content(Content content) {
		setContent(content);
		return this;
	}

	/**
	 * Set the content (body) of the message. This is a shortcut to
	 * 
	 * <pre>
	 * {@code .content(new StringContent(content))}
	 * </pre>
	 * 
	 * <p>
	 * If you prefer, you can instead use the fluent API to set the email
	 * content (body):
	 * 
	 * <pre>
	 * {@code
	 * .body().string(content)
	 * }
	 * </pre>
	 * 
	 * 
	 * @param content
	 *            the content of the message
	 * @return this instance for fluent chaining
	 * @see #body()
	 * @see #html()
	 * @see #text()
	 */
	public Email content(String content) {
		return content(new StringContent(content));
	}

	/**
	 * Set the content (body) of the message (HTML part).
	 * 
	 * <p>
	 * You can use this method in addition to {@link #text()} to provide both a
	 * main body and an alternative textual body (that is used when HTML format
	 * is not supported by the email client).
	 * 
	 * <p>
	 * This method provides fluent chaining to guide developer. It has the same
	 * effect has using {@link #content(Content)}.
	 * 
	 * <p>
	 * If you also call either {@link #content(Content)},
	 * {@link #content(String)} or {@link #setContent(Content)} then this method
	 * has no effect.
	 * 
	 * @return the builder for building HTML part
	 * @since 3.0.0
	 */
	public SingleContentBuilder<Email> html() {
		return htmlBuilder;
	}

	/**
	 * Set the content (body) of the message (text part).
	 * 
	 * <p>
	 * You can use this method in addition to {@link #html()} to provide both a
	 * main body and an alternative textual body (that is used when HTML format
	 * is not supported by the email client). If you only call {@link #text()},
	 * then the textual content is used as the main body.
	 * 
	 * <p>
	 * This method provides fluent chaining to guide developer. It has the same
	 * effect has using {@link #content(Content)}.
	 * 
	 * <p>
	 * If you also call either {@link #content(Content)},
	 * {@link #content(String)} or {@link #setContent(Content)} then this method
	 * has no effect.
	 * 
	 * @return the builder for building text part
	 * @since 3.0.0
	 */
	public SingleContentBuilder<Email> text() {
		return textBuilder;
	}

	/**
	 * Set the content (body) of the message.
	 * 
	 * <p>
	 * This is the method that you can use in main circumstances to set the
	 * body:
	 * <ul>
	 * <li>When you want to set a single textual body (no alternative):
	 * 
	 * <pre>
	 * {@code .body().string("text")}
	 * </pre>
	 * 
	 * </li>
	 * <li>When you want to set a single HTML body (no alternative):
	 * 
	 * <pre>
	 * {@code .body().string("<html><body>Hello world</body></html>")}
	 * </pre>
	 * 
	 * </li>
	 * <li>When you want to set a single text body (no alternative) based on a
	 * template (extension depends on template parser):
	 * 
	 * <pre>
	 * {@code .body().template("path/to/text/template.txt", obj)}
	 * </pre>
	 * 
	 * </li>
	 * <li>When you want to set a single HTML body (no alternative) based on a
	 * template (extension depends on template parser):
	 * 
	 * <pre>
	 * {@code .body().template("path/to/text/template.html", obj)}
	 * </pre>
	 * 
	 * </li>
	 * <li>When you want to set both HTML and textual alternative based on two
	 * different templates (same path but without extension):
	 * 
	 * <pre>
	 * {@code .body().template("path/to/text/template", obj)}
	 * </pre>
	 * 
	 * </li>
	 * </ul>
	 * 
	 * <p>
	 * This method provides fluent chaining to guide developer. It has the same
	 * effect has using {@link #content(Content)}.
	 * 
	 * <p>
	 * If you also call either {@link #content(Content)},
	 * {@link #content(String)}, {@link #setContent(Content)}, {@link #html()}
	 * or {@link #text()} then this method has no effect because they are more
	 * specific.
	 * 
	 * @return the builder for building the body
	 * @since 3.0.0
	 */
	public BodyBuilder body() {
		return bodyBuilder;
	}

	/**
	 * Attach a file to the email. The attachment must have a name.
	 * 
	 * <p>
	 * The file is attached with the {@link ContentDisposition#ATTACHMENT}
	 * disposition.
	 * 
	 * <p>
	 * This method provides fluent chaining to guide the developer. This method
	 * has the same effect has using {@link #attach(Attachment)},
	 * {@link #attach(Attachment...)} or {@link #attach(List)}.
	 * 
	 * @return the builder for building the attachments
	 * @since 3.0.0
	 */
	public AttachBuilder attach() {
		return attachBuilder;
	}

	/**
	 * Embed a file in the email. This is mainly used for images. The embedded
	 * file must be referenced in the body of the email using a
	 * <a href="https://tools.ietf.org/html/rfc4021#section-2.2.2">Content-ID
	 * (or CID)</a>.
	 * 
	 * <p>
	 * The file is attached {@link ContentDisposition#INLINE} disposition.
	 * 
	 * <p>
	 * This method provides fluent chaining to guide the developer. This method
	 * has the same effect has using {@link #attach(Attachment)},
	 * {@link #attach(Attachment...)} or {@link #attach(List)}.
	 * 
	 * @return the builder for building the attachments
	 * @since 3.0.0
	 */
	public EmbedBuilder embed() {
		return embedBuilder;
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
			addresses[i] = new Recipient(t);
			i++;
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
			addresses[i] = new Recipient(t);
			i++;
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
			addresses[i] = new Recipient(t);
			i++;
		}
		return addresses;
	}

	@Override
	public String toString() {
		return toString(false);
	}

	@Override
	public String toLogString() {
		return toString(true);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(subject, getContent(), from, recipients, getAttachments()).hashCode();
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
				if (type == recipient.getType()) {
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
		builder.append("\r\n----------------------------------\r\n").append(includeContent ? getContent() : "<Content skipped>");
		if (attachments != null && !attachments.isEmpty()) {
			builder.append("\r\n----------------------------------").append("\r\nAttachments: ").append(getAttachments());
		}
		builder.append("\r\n==================================\r\n");
		return builder.toString();
	}

	private Content buildContent() {
		// NOTE: normally it can't be null but EqualsVerifier uses reflection to
		// set it to null
		Content html = htmlBuilder == null ? null : htmlBuilder.build();
		// NOTE: normally it can't be null but EqualsVerifier uses reflection to
		// set it to null
		Content text = textBuilder == null ? null : textBuilder.build();
		if (html != null && text != null) {
			return new MultiContent(text, html);
		}
		if (html != null) {
			return html;
		}
		if (text != null) {
			return text;
		}
		// NOTE: normally it can't be null but EqualsVerifier uses reflection to
		// set it to null
		Content body = bodyBuilder == null ? null : bodyBuilder.build();
		if (body != null) {
			return body;
		}
		return null;
	}

}
