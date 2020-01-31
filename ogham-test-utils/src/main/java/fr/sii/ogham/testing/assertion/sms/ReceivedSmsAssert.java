package fr.sii.ogham.testing.assertion.sms;

import java.util.Collection;
import java.util.List;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

import fr.sii.ogham.testing.sms.simulator.bean.SubmitSm;

/**
 * Make assertions on received messages
 * 
 * @author Aurélien Baudet
 *
 * @param <S>
 *            the type of the {@link SubmitSm} to make assertions on
 */
public class ReceivedSmsAssert<S extends SubmitSm> {
	/**
	 * List of received messages
	 */
	private final List<S> actual;

	/**
	 * Initializes with the list of received messages
	 * 
	 * @param actual
	 *            received messages
	 */
	public ReceivedSmsAssert(List<S> actual) {
		this.actual = actual;
	}

	/**
	 * Access fluent API to write assertions on a particular received message.
	 * 
	 * If you want to make assertions on several messages, you may prefer using:
	 * 
	 * <pre>
	 * .receivedMessages().message(0)
	 *                       .content(is("foobar"))
	 *                    .and()
	 *                    .message(1)
	 *                       .content(is("bar"))
	 * </pre>
	 * 
	 * @param index
	 *            the index of the received message
	 * @return the fluent API for assertions on the particular message
	 */
	public SmsAssert<ReceivedSmsAssert<S>, S> receivedMessage(int index) {
		return new SmsAssert<>(actual.get(index), index, this);
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
	 *                       .content(is("foobar"))
	 * </pre>
	 * 
	 * For writing assertions that are applied on every received message, you
	 * can write:
	 * 
	 * <pre>
	 * .receivedMessages().every()
	 *                       .content(is("foobar"))
	 * </pre>
	 * 
	 * Will check that content of every message is "foobar".
	 * 
	 * <p>
	 * You can use this method to factorize several assertions on a message and
	 * then make dedicated assertions on some messages:
	 * 
	 * <pre>
	 * .receivedMessages().every()
	 *                       .content(is("foobar"))
	 *                    .and()
	 *                    .message(0)
	 *                       .from().number(is("+33102030405"))
	 * </pre>
	 * 
	 * Will check that content of every message is "foobar" and that phone
	 * nuumber of sender of first received message is "+33102030405".
	 * 
	 * @return the fluent API for assertions on messages
	 */
	public SmsListAssert<ReceivedSmsAssert<S>, S> receivedMessages() {
		return new SmsListAssert<>(actual, this);
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
	public SmsListAssert<ReceivedSmsAssert<S>, S> receivedMessages(Matcher<Collection<? extends S>> matcher) {
		MatcherAssert.assertThat(actual, matcher);
		return receivedMessages();
	}

}
