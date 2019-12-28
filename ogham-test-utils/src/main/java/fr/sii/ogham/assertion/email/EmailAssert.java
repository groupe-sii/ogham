package fr.sii.ogham.assertion.email;

import static fr.sii.ogham.assertion.AssertionHelper.assertThat;
import static fr.sii.ogham.assertion.OghamAssertions.usingContext;
import static fr.sii.ogham.helper.email.EmailUtils.getAlternativePart;
import static fr.sii.ogham.helper.email.EmailUtils.getAttachments;
import static fr.sii.ogham.helper.email.EmailUtils.getBodyPart;
import static java.util.Arrays.asList;
import static javax.mail.Message.RecipientType.CC;
import static javax.mail.Message.RecipientType.TO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;

import org.hamcrest.Matcher;
import org.junit.Assert;

import fr.sii.ogham.assertion.HasParent;
import fr.sii.ogham.assertion.context.SingleMessageContext;
import fr.sii.ogham.helper.email.EmailUtils;
import fr.sii.ogham.helper.email.FileNamePredicate;

@SuppressWarnings("squid:S1192")
public class EmailAssert<P> extends HasParent<P> {
	/**
	 * The list of messages that will be used for assertions
	 */
	private final List<? extends Message> actual;

	public EmailAssert(Message actual, P parent) {
		this(Arrays.asList(actual), parent);
	}

	public EmailAssert(List<? extends Message> actual, P parent) {
		super(parent);
		this.actual = actual;
	}

	/**
	 * Make assertions on the subject of the message(s).
	 * 
	 * <pre>
	 * .receivedMessages().message(0)
	 *    .subject(is("foobar"))
	 * </pre>
	 * 
	 * Will check if the subject of the first message is exactly "foobar".
	 * 
	 * <pre>
	 * .receivedMessages().every()
	 *    .subject(is("foobar"))
	 * </pre>
	 * 
	 * Will check that the subject of every message is exactly "foobar".
	 * 
	 * @param matcher
	 *            the assertion to apply on subject
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public EmailAssert<P> subject(Matcher<String> matcher) {
		try {
			String desc = "subject of message ${messageIndex}";
			int index = 0;
			for (Message message : actual) {
				assertThat(message.getSubject(), usingContext(desc, new SingleMessageContext(index++), matcher));
			}
			return this;
		} catch (MessagingException e) {
			throw new AssertionError("Failed to get subject of messsage", e);
		}
	}

	/**
	 * Make assertions on the body of the message(s) using fluent API. The body
	 * of the message is either:
	 * <ul>
	 * <li>The part of the message when it contains only one part</li>
	 * <li>The part with text or HTML mimetype if only one part with one of that
	 * mimetype</li>
	 * <li>The second part with text or HTML mimetype if there are two text or
	 * HTML parts</li>
	 * </ul>
	 * 
	 * <pre>
	 * .receivedMessages().message(0).body()
	 *    .contentAsString(is("email body"))
	 *    .contentType(is("text/plain"))
	 * </pre>
	 * 
	 * Will check if the body content of the first message is "email body" and
	 * content-type of the first message is "text/plain".
	 * 
	 * <pre>
	 * .receivedMessages().every().body()
	 *    .contentAsString(is("email body"))
	 *    .contentType(is("text/plain"))
	 * </pre>
	 * 
	 * Will check that the body content of every message is "email body" and
	 * content-type of every message is "text/plain".
	 * 
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public PartAssert<EmailAssert<P>> body() {
		try {
			int index = 0;
			List<PartWithContext> bodies = new ArrayList<>();
			for (Message message : actual) {
				bodies.add(new PartWithContext(getBodyPart(message), "body", new SingleMessageContext(index++)));
			}
			return new PartAssert<>(bodies, this);
		} catch (MessagingException e) {
			throw new AssertionError("Failed to get body of messsage", e);
		}
	}

	/**
	 * Make assertions on the alternative part of the message(s) using fluent
	 * API. The alternative is useful when sending HTML email that may be
	 * unreadable on some email clients. For example, a smartphone will display
	 * the 2 or 3 first lines as a summary. Many smartphones will take the HTML
	 * message as-is and will display HTML tags instead of content of email.
	 * Alternative is used to provide a textual visualization of the message
	 * that will be readable by any system.
	 * 
	 * <p>
	 * The alternative of the message is either:
	 * <ul>
	 * <li>null if there is only one part</li>
	 * <li>null if there is only one text or HTML part</li>
	 * <li>the first part if there are more than one text or HTML part</li>
	 * </ul>
	 * 
	 * <pre>
	 * .receivedMessages().message(0).alternative()
	 *    .contentAsString(is("email alternative"))
	 *    .contentType(is("text/plain"))
	 * </pre>
	 * 
	 * Will check if the body content of the first message is "email
	 * alternative" and content-type of the first message is "text/plain".
	 * 
	 * <pre>
	 * .receivedMessages().every().alternative()
	 *    .contentAsString(is("email alternative"))
	 *    .contentType(is("text/plain"))
	 * </pre>
	 * 
	 * Will check that the body content of every message is "email alternative"
	 * and content-type of every message is "text/plain".
	 * 
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public PartAssert<EmailAssert<P>> alternative() {
		try {
			int index = 0;
			List<PartWithContext> bodies = new ArrayList<>();
			for (Message message : actual) {
				bodies.add(new PartWithContext(getAlternativePart(message), "alternative", new SingleMessageContext(index++)));
			}
			return new PartAssert<>(bodies, this);
		} catch (MessagingException e) {
			throw new AssertionError("Failed to get body of messsage", e);
		}
	}

	/**
	 * Make assertions on the body of the message(s). The body of the message is
	 * either:
	 * <ul>
	 * <li>The part of the message when it contains only one part</li>
	 * <li>The part with text or HTML mimetype if only one part with one of that
	 * mimetype</li>
	 * <li>The second part with text or HTML mimetype if there are two text or
	 * HTML parts</li>
	 * </ul>
	 * 
	 * <pre>
	 * .receivedMessages().message(0)
	 *    .body(allOf(notNullValue(), instanceOf(MimeBodyPart.class))
	 * </pre>
	 * 
	 * Will check if the body of the first message is not null and is a
	 * MimeBodyPart instance.
	 * 
	 * <pre>
	 * .receivedMessages().every()
	 *    .body(allOf(notNullValue(), instanceOf(MimeBodyPart.class))
	 * </pre>
	 * 
	 * Will check that the body of every message is not null and is a
	 * MimeBodyPart instance.
	 * 
	 * <p>
	 * You can use the {@link #body()} variant to make more powerful assertions.
	 * 
	 * @param matcher
	 *            the assertion to apply on body
	 * @param <T>
	 *            the type used for the matcher
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public <T extends Part> EmailAssert<P> body(Matcher<? super Part> matcher) {	// NOSONAR
		try {
			String desc = "body of message ${messageIndex}";
			int index = 0;
			for (Message message : actual) {
				assertThat(getBodyPart(message), usingContext(desc, new SingleMessageContext(index++), matcher));
			}
			return this;
		} catch (MessagingException e) {
			throw new AssertionError("Failed to access attachments of messsage", e);
		}
	}

	/**
	 * Make assertions on the alternative part of the message(s). The
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
	 * <pre>
	 * .receivedMessages().message(0)
	 *    .alternative(allOf(notNullValue(), instanceOf(MimeBodyPart.class))
	 * </pre>
	 * 
	 * Will check if the alternative of the first message is not null and is a
	 * MimeBodyPart instance.
	 * 
	 * <pre>
	 * .receivedMessages().every()
	 *    .alternative(allOf(notNullValue(), instanceOf(MimeBodyPart.class))
	 * </pre>
	 * 
	 * Will check that the alternative of every message is not null and is a
	 * MimeBodyPart instance.
	 * 
	 * <p>
	 * You can use the {@link #alternative()} variant to make more powerful
	 * assertions.
	 * 
	 * @param matcher
	 *            the assertion to apply on alternative
	 * @param <T>
	 *            the type used for the matcher
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public <T extends Part> EmailAssert<P> alternative(Matcher<? super Part> matcher) {	// NOSONAR
		try {
			String desc = "alternative of message ${messageIndex}";
			int index = 0;
			for (Message message : actual) {
				assertThat(getAlternativePart(message), usingContext(desc, new SingleMessageContext(index++), matcher));
			}
			return this;
		} catch (MessagingException e) {
			throw new AssertionError("Failed to access attachments of messsage", e);
		}
	}

	/**
	 * Make assertions on the sender of the message(s) using fluent API.
	 * 
	 * <pre>
	 * .receivedMessages().message(0).from()
	 *    .address(hasItems("noreply@sii.fr"))
	 *    .personal(hasItems("Groupe SII"))
	 * </pre>
	 * 
	 * Will check if the sender email address of the first message is exactly
	 * "noreply@sii.fr" and sender displayed address of the first message is
	 * exactly "Groupe SII".
	 * 
	 * <pre>
	 * .receivedMessages().every().from()
	 *    .address(hasItems("noreply@sii.fr"))
	 *    .personal(hasItems("Groupe SII"))
	 * </pre>
	 * 
	 * Will check if the sender email address of every message is exactly
	 * "noreply@sii.fr" and sender displayed address of every message is exactly
	 * "Groupe SII".
	 * 
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public AddressListAssert<EmailAssert<P>> from() {
		try {
			int index = 0;
			List<AddressesWithContext> addresses = new ArrayList<>();
			for (Message message : actual) {
				addresses.add(new AddressesWithContext(asList((InternetAddress[]) message.getFrom()), "from", new SingleMessageContext(index++)));
			}
			return new AddressListAssert<>(addresses, this);
		} catch (MessagingException e) {
			throw new AssertionError("Failed to get from field of messsage", e);
		}
	}

	/**
	 * Make assertions on the sender of the message(s) using fluent API.
	 * 
	 * <pre>
	 * .receivedMessages().message(0).to()
	 *    .address(hasItems("recipient1@sii.fr", "recipient2@sii.fr"))
	 *    .personal(hasItems("Foo", "Bar"))
	 * </pre>
	 * 
	 * Will check if the list of email addresses of direct recipients (TO) of
	 * the first message are exactly "recipient1@sii.fr", "recipient2@sii.fr"
	 * and the list of displayed address of direct recipients (TO) of the first
	 * message are exactly "Foo", "Bar".
	 * 
	 * <pre>
	 * .receivedMessages().every().to()
	 *    .address(hasItems("recipient1@sii.fr", "recipient2@sii.fr"))
	 *    .personal(hasItems("Foo", "Bar"))
	 * </pre>
	 * 
	 * Will check if the list of email addresses of direct recipients (TO) of
	 * every message are exactly "recipient1@sii.fr", "recipient2@sii.fr" and
	 * the list of displayed address of direct recipients (TO) of every message
	 * are exactly "Foo", "Bar".
	 * 
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public AddressListAssert<EmailAssert<P>> to() {
		try {
			int index = 0;
			List<AddressesWithContext> addresses = new ArrayList<>();
			for (Message message : actual) {
				addresses.add(new AddressesWithContext(asList((InternetAddress[]) message.getRecipients(TO)), "to", new SingleMessageContext(index++)));
			}
			return new AddressListAssert<>(addresses, this);
		} catch (MessagingException e) {
			throw new AssertionError("Failed to get to field of messsage", e);
		}
	}

	/**
	 * Make assertions on the sender of the message(s) using fluent API.
	 * 
	 * <pre>
	 * .receivedMessages().message(0).cc()
	 *    .address(hasItems("recipient1@sii.fr", "recipient2@sii.fr"))
	 *    .personal(hasItems("Foo", "Bar"))
	 * </pre>
	 * 
	 * Will check if the list of email addresses of copy recipients (CC) of the
	 * first message are exactly "recipient1@sii.fr", "recipient2@sii.fr" and
	 * the list of displayed address of copy recipients (CC) of the first
	 * message are exactly "Foo", "Bar".
	 * 
	 * <pre>
	 * .receivedMessages().every().cc()
	 *    .address(hasItems("recipient1@sii.fr", "recipient2@sii.fr"))
	 *    .personal(hasItems("Foo", "Bar"))
	 * </pre>
	 * 
	 * Will check if the list of email addresses of copy recipients (CC) of
	 * every message are exactly "recipient1@sii.fr", "recipient2@sii.fr" and
	 * the list of displayed address of copy recipients (CC) of every message
	 * are exactly "Foo", "Bar".
	 * 
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public AddressListAssert<EmailAssert<P>> cc() {
		try {
			int index = 0;
			List<AddressesWithContext> addresses = new ArrayList<>();
			for (Message message : actual) {
				addresses.add(new AddressesWithContext(asList((InternetAddress[]) message.getRecipients(CC)), "cc", new SingleMessageContext(index++)));
			}
			return new AddressListAssert<>(addresses, this);
		} catch (MessagingException e) {
			throw new AssertionError("Failed to get cc field of messsage", e);
		}
	}

	/**
	 * Make assertions on the list of attachments of the message(s).
	 * 
	 * <pre>
	 * .receivedMessages().message(0)
	 *    .attachments(hasSize(1))
	 * </pre>
	 * 
	 * Will check if the number of attachments of the first message is exactly
	 * 1.
	 * 
	 * <pre>
	 * .receivedMessages().every()
	 *    .attachments(hasSize(1))
	 * </pre>
	 * 
	 * Will check that the number of attachments of every message is exactly 1.
	 * 
	 * <p>
	 * You can use the {@link #attachment(String)} or
	 * {@link #attachments(Predicate)} variants to make more powerful assertions
	 * on a particular attachment.
	 * 
	 * @param matcher
	 *            the assertion to apply on list of attachments
	 * @param <T>
	 *            the type used for the matcher
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public <T extends Collection<? extends BodyPart>> EmailAssert<P> attachments(Matcher<? super Collection<? extends BodyPart>> matcher) {	// NOSONAR
		try {
			String desc = "attachments of message ${messageIndex}";
			int index = 0;
			for (Message message : actual) {
				Object content = message.getContent();
				Assert.assertTrue("should be multipart message", content instanceof Multipart);
				assertThat(getAttachments(message), usingContext(desc, new SingleMessageContext(index++), matcher));
			}
			return this;
		} catch (MessagingException | IOException e) {
			throw new AssertionError("Failed to access attachments of messsage", e);
		}
	}

	/**
	 * Make assertions on a particular attachment of the message(s) using fluent
	 * API. The attachment is identified by its filename.
	 * 
	 * <pre>
	 * .receivedMessages().message(0).attachment("foo.pdf")
	 *    .contentType(is("application/pdf"))
	 * </pre>
	 * 
	 * Will check if the content-type of the attachment named "foo.pdf" of the
	 * first message is exactly "application/pdf".
	 * 
	 * <pre>
	 * .receivedMessages().every().attachment("foo.pdf")
	 *    .contentType(is("application/pdf"))
	 * </pre>
	 * 
	 * Will check that the content-type of attachment named "foo.pdf" of every
	 * message is exactly "application/pdf".
	 * 
	 * <p>
	 * This is a shortcut to {@link #attachments(Predicate)} with
	 * {@link FileNamePredicate};
	 * 
	 * @param filename
	 *            the name of the attachment to make assertions on it
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public PartAssert<EmailAssert<P>> attachment(String filename) {
		return attachments(new FileNamePredicate(filename));
	}

	/**
	 * Make assertions on a particular attachment of the message(s).
	 * 
	 * <pre>
	 * .receivedMessages().message(0).attachment(0)
	 *    .contentType(is("application/pdf"))
	 * </pre>
	 * 
	 * Will check if the content-type of the first attachment of the first
	 * message is exactly "application/pdf".
	 * 
	 * <pre>
	 * .receivedMessages().every().attachment(0)
	 *    .contentType(is("application/pdf"))
	 * </pre>
	 * 
	 * Will check if the content-type of the first attachment of every message
	 * is exactly "application/pdf".
	 * 
	 * @param index
	 *            the index of the attachment
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public PartAssert<EmailAssert<P>> attachment(int index) {
		try {
			int msgIndex = 0;
			List<PartWithContext> attachments = new ArrayList<>();
			for (Message message : actual) {
				Object content = message.getContent();
				Assert.assertTrue("should be multipart message", content instanceof Multipart);
				List<BodyPart> found = getAttachments(message);
				BodyPart attachment = index >= found.size() ? null : found.get(index);
				attachments.add(new PartWithContext(attachment, "attachment with index " + index, new SingleMessageContext(msgIndex++)));
			}
			return new PartAssert<>(attachments, this);
		} catch (MessagingException | IOException e) {
			throw new AssertionError("Failed to get attachment with index " + index + " of messsage", e);
		}
	}

	/**
	 * Make assertions on a one or several attachments of the message(s) using
	 * fluent API. The attachments are identified using provided predicate.
	 * 
	 * <pre>
	 * .receivedMessages().message(0)
	 *    .attachments(new PdfFilter()).filename(endsWith(".pdf"))
	 * </pre>
	 * 
	 * Will check if the name of every PDF attachments of the first message are
	 * named "foo.pdf".
	 * 
	 * <pre>
	 * .receivedMessages().every()
	 *    .attachments(new PdfFilter()).filename(endsWith(".pdf"))
	 * </pre>
	 * 
	 * Will check if the name of every PDF attachments of every message are
	 * named "foo.pdf".
	 * 
	 * 
	 * @param filter
	 *            the filter used to find attachments
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public PartAssert<EmailAssert<P>> attachments(Predicate<Part> filter) {
		try {
			int index = 0;
			List<PartWithContext> attachments = new ArrayList<>();
			for (Message message : actual) {
				Object content = message.getContent();
				Assert.assertTrue("should be multipart message", content instanceof Multipart);
				int attachmentIdx = 0;
				for (BodyPart attachment : EmailUtils.<BodyPart>getAttachments(message, filter)) {
					attachments.add(new PartWithContext(attachment, "attachment " + filter + "(" + attachmentIdx + ")", new SingleMessageContext(index)));
					attachmentIdx++;
				}
				index++;
			}
			return new PartAssert<>(attachments, this);
		} catch (MessagingException | IOException e) {
			throw new AssertionError("Failed to get attachment " + filter + " of messsage", e);
		}
	}

}
