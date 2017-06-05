package fr.sii.ogham.assertion.email;

import static fr.sii.ogham.assertion.AssertionHelper.assertThat;

import java.util.Collection;
import java.util.List;

import javax.mail.Message;

import org.hamcrest.Matcher;

public class ReceivedEmailsAssert {
	/**
	 * List of received messages
	 */
	private final List<? extends Message> actual;

	public ReceivedEmailsAssert(List<? extends Message> actual) {
		this.actual = actual;
	}

	/**
	 * Access fluent API to write assertions on a particular received message.
	 * 
	 * If you want to make assertions on several messages, you may prefer using:
	 * 
	 * <pre>
	 * .receivedMessages().message(0)
	 *                       .subject(is("foobar"))
	 *                    .and()
	 *                    .message(1)
	 *                       .subject(is("bar"))
	 * </pre>
	 * 
	 * @param index
	 *            the index of the received message
	 * @return the fluent API for assertions on the particular message
	 */
	public EmailAssert<ReceivedEmailsAssert> receivedMessage(int index) {
		return new EmailAssert<>(actual.get(index), this);
	}

	/**
	 * Fluent API to write assertions on received messages.
	 * 
	 * You can write assertions for all messages or a particular message.
	 * 
	 * For example, for writing assertion on a single message, you can write:
	 * 
	 * <pre>
	 * .receivedMessages().message(0)
	 *                       .subject(is("foobar"))
	 * </pre>
	 * 
	 * For writing assertions that are applied on every received message, you
	 * can write:
	 * 
	 * <pre>
	 * .receivedMessages().forEach()
	 *                       .subject(is("foobar"))
	 * </pre>
	 * 
	 * Will check that subject of every message is "foobar".
	 * 
	 * <p>
	 * You can use this method to factorize several assertions on a message and
	 * then make dedicated assertions on some messages:
	 * 
	 * <pre>
	 * .receivedMessages().forEach()
	 *                       .subject(is("foobar"))
	 *                    .and()
	 *                    .message(0)
	 *                       .body().contentAsString(is("toto"))
	 * </pre>
	 * 
	 * Will check that subject of every message is "foobar" and that body of
	 * first received message is "toto".
	 * 
	 * @return the fluent API for assertions on messages
	 */
	public EmailsAssert<ReceivedEmailsAssert> receivedMessages() {
		return new EmailsAssert<>(actual, this);
	}

	/**
	 * Fluent API to write assertions on received messages.
	 * 
	 * Make an assertion on received messages list (JavaMail message).
	 * 
	 * For example, for writing assertion on a single message, you can write:
	 * 
	 * <pre>
	 * .receivedMessages(is(Matchers.&lt;Message&gt;empty()))
	 * </pre>
	 * 
	 * @param matcher
	 *            the assertion to apply on message list
	 * @return the fluent API for assertions on messages
	 */
	public EmailsAssert<ReceivedEmailsAssert> receivedMessages(Matcher<Collection<? extends Message>> matcher) {
		assertThat(actual, matcher);
		return receivedMessages();
	}

}
