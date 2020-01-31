package fr.sii.ogham.testing.assertion.sms;

import java.util.List;

import org.hamcrest.Matcher;

import fr.sii.ogham.testing.assertion.util.AssertionHelper;
import fr.sii.ogham.testing.sms.simulator.bean.SubmitSm;
import fr.sii.ogham.testing.util.HasParent;

/**
 * Make assertions on received messages
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the parent type
 * @param <S>
 *            the type of {@link SubmitSm}
 */
public class SmsListAssert<P, S extends SubmitSm> extends HasParent<P> {
	/**
	 * The list of messages that will be used for assertions
	 */
	private final List<S> actual;

	/**
	 * @param actual
	 *            the received messages
	 * @param parent
	 *            the parent
	 */
	public SmsListAssert(List<S> actual, P parent) {
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
	public SmsListAssert<P, S> count(Matcher<Integer> matcher) {
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
	public SmsAssert<SmsListAssert<P, S>, S> message(int index) {
		return new SmsAssert<>(actual.get(index), index, this);
	}

	/**
	 * Fluent API to write assertions on every received messages. Any defined
	 * assertion will be applied on every message:
	 * 
	 * <pre>
	 * .receivedMessages().every().content(is("foobar"))
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
	 * Will check that content of every message is "foobar" and that body of
	 * first received message is "+33102030405".
	 * 
	 * @return the fluent API for chaining assertions on received messages
	 */
	public SmsAssert<SmsListAssert<P, S>, S> every() {
		return new SmsAssert<>(actual, this);
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
	 * @deprecated use {@link #every()} instead
	 */
	@Deprecated
	public SmsAssert<SmsListAssert<P, S>, S> forEach() {
		return new SmsAssert<>(actual, this);
	}

}