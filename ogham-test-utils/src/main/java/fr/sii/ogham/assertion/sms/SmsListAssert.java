package fr.sii.ogham.assertion.sms;

import java.util.List;

import org.hamcrest.Matcher;
import org.jsmpp.bean.SubmitSm;

import fr.sii.ogham.assertion.AssertionHelper;
import fr.sii.ogham.assertion.HasParent;

public class SmsListAssert<P> extends HasParent<P> {
	/**
	 * The list of messages that will be used for assertions
	 */
	private final List<SubmitSm> actual;

	public SmsListAssert(List<SubmitSm> actual, P parent) {
		super(parent);
		this.actual = actual;
	}

	/**
	 * Assertion on the number of received messages:
	 * 
	 * <pre>
	 * .count(is(1))
	 * </pre>
	 * 
	 * @param matcher
	 *            the assertion applied on the number of received messages
	 * @return the fluent API for chaining assertions on received messages
	 */
	public SmsListAssert<P> count(Matcher<Integer> matcher) {
		AssertionHelper.assertThat(actual.size(), matcher);
		return this;
	}

	/**
	 * Access a particular message to write assertions for it:
	 * 
	 * <pre>
	 * .message(0).content(is("foobar"))
	 * </pre>
	 * 
	 * You can use this method to chain several assertions on different
	 * messages:
	 * 
	 * <pre>
	 * .message(0).content(is("foobar"))
	 * .and()
	 * .message(1).content(is("toto"))
	 * </pre>
	 * 
	 * 
	 * @param index
	 *            the index of the message in the received list
	 * @return the fluent API for chaining assertions on received messages
	 */
	public SmsAssert<SmsListAssert<P>> message(int index) {
		return new SmsAssert<>(actual.get(index), this);
	}

	/**
	 * Fluent API to write assertions on every received messages. Any defined
	 * assertion will be applied on every message:
	 * 
	 * <pre>
	 * .receivedMessages().forEach().content(is("foobar"))
	 * </pre>
	 * 
	 * Will check that content of every message is "foobar".
	 * 
	 * <p>
	 * You can use this method to factorize several assertions on a message and
	 * then make dedicated assertions on some messages:
	 * 
	 * <pre>
	 * .receivedMessages().forEach()
	 *                       .content(is("foobar"))
	 *                    .and()
	 *                    .message(0)
	 *                       .from().number(is("+33102030405"))
	 * </pre>
	 * 
	 * Will check that content of every message is "foobar" and that body of
	 * first received message is "+33102030405".
	 * 
	 * @return the fluent API for chaining assertions on received messages
	 */
	public SmsAssert<SmsListAssert<P>> forEach() {
		return new SmsAssert<>(actual, this);
	}

}