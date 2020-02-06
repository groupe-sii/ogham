package fr.sii.ogham.testing.assertion.sms;

import static fr.sii.ogham.testing.assertion.util.AssertionHelper.assertThat;
import static fr.sii.ogham.testing.assertion.util.AssertionHelper.overrideDescription;
import static org.hamcrest.Matchers.lessThan;

import java.util.List;

import org.hamcrest.Matcher;

import fr.sii.ogham.testing.assertion.util.AssertionRegistry;
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
public class FluentSmsListAssert<P, S extends SubmitSm> extends HasParent<P> {
	/**
	 * The list of messages that will be used for assertions
	 */
	private final List<S> actual;
	private final AssertionRegistry registry;

	/**
	 * @param actual
	 *            the received messages
	 * @param parent
	 *            the parent
	 * @param registry
	 *            used to register assertions
	 */
	public FluentSmsListAssert(List<S> actual, P parent, AssertionRegistry registry) {
		super(parent);
		this.actual = actual;
		this.registry = registry;
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
	public FluentSmsListAssert<P, S> count(Matcher<Integer> matcher) {
		registry.register(() -> assertThat("Received messages count", actual.size(), matcher));
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
	public FluentSmsAssert<FluentSmsListAssert<P, S>, S> message(int index) {
		registry.register(() -> assertThat(index, overrideDescription("Assertions on message "+index+" can't be executed because "+actual.size()+" messages were received", lessThan(actual.size()))));
		return new FluentSmsAssert<>(index<actual.size() ? actual.get(index) : null, index, this, registry);
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
	public FluentSmsAssert<FluentSmsListAssert<P, S>, S> every() {
		return new FluentSmsAssert<>(actual, this, registry);
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
	public FluentSmsAssert<FluentSmsListAssert<P, S>, S> forEach() {
		return every();
	}

}