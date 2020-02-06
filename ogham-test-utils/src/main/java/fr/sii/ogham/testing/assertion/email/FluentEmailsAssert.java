package fr.sii.ogham.testing.assertion.email;

import static fr.sii.ogham.testing.assertion.util.AssertionHelper.assertThat;
import static fr.sii.ogham.testing.assertion.util.AssertionHelper.overrideDescription;
import static org.hamcrest.Matchers.lessThan;

import java.util.List;

import javax.mail.Message;

import org.hamcrest.Matcher;

import fr.sii.ogham.testing.assertion.util.AssertionRegistry;
import fr.sii.ogham.testing.util.HasParent;

public class FluentEmailsAssert<P> extends HasParent<P> {
	/**
	 * The list of messages that will be used for assertions
	 */
	private final List<? extends Message> actual;
	/**
	 * Registry to register assertions
	 */
	private final AssertionRegistry registry;


	public FluentEmailsAssert(List<? extends Message> actual, P parent, AssertionRegistry registry) {
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
	public FluentEmailsAssert<P> count(Matcher<Integer> matcher) {
		registry.register(() -> assertThat("Received messages count", actual.size(), matcher));
		return this;
	}

	/**
	 * Access a particular message to write assertions for it:
	 * 
	 * <pre>
	 * .message(0).subject(is("foobar"))
	 * </pre>
	 * 
	 * You can use this method to chain several assertions on different
	 * messages:
	 * 
	 * <pre>
	 * .message(0).subject(is("foobar"))
	 * .and()
	 * .message(1).subject(is("toto"))
	 * </pre>
	 * 
	 * 
	 * @param index
	 *            the index of the message in the received list
	 * @return the fluent API for chaining assertions on received messages
	 */
	public FluentEmailAssert<FluentEmailsAssert<P>> message(int index) {
		registry.register(() -> assertThat(index, overrideDescription("Assertions on message "+index+" can't be executed because "+actual.size()+" messages were received", lessThan(actual.size()))));
		return new FluentEmailAssert<>(index<actual.size() ? actual.get(index) : null, index, this, registry);
	}

	/**
	 * Fluent API to write assertions on every received messages. Any defined
	 * assertion will be applied on every message:
	 * 
	 * <pre>
	 * .receivedMessages().every().subject(is("foobar"))
	 * </pre>
	 * 
	 * Will check that subject of every message is "foobar".
	 * 
	 * <p>
	 * You can use this method to factorize several assertions on a message and
	 * then make dedicated assertions on some messages:
	 * 
	 * <pre>
	 * .receivedMessages().every().subject(is("foobar"))
	 *                    .and()
	 *                    .message(0).body().contentAsString(is("toto"))
	 * </pre>
	 * 
	 * Will check that subject of every message is "foobar" and that body of
	 * first received message is "toto".
	 * 
	 * @return the fluent API for chaining assertions on received messages
	 */
	public FluentEmailAssert<FluentEmailsAssert<P>> every() {
		return new FluentEmailAssert<>(actual, this, registry);
	}

	/**
	 * Fluent API to write assertions on every received messages. Any defined
	 * assertion will be applied on every message:
	 * 
	 * <pre>
	 * .receivedMessages().forEach().subject(is("foobar"))
	 * </pre>
	 * 
	 * Will check that subject of every message is "foobar".
	 * 
	 * <p>
	 * You can use this method to factorize several assertions on a message and
	 * then make dedicated assertions on some messages:
	 * 
	 * <pre>
	 * .receivedMessages().forEach().subject(is("foobar"))
	 *                    .and()
	 *                    .message(0).body().contentAsString(is("toto"))
	 * </pre>
	 * 
	 * Will check that subject of every message is "foobar" and that body of
	 * first received message is "toto".
	 * 
	 * @return the fluent API for chaining assertions on received messages
	 * @deprecated use {@link #every()} instead
	 */
	@Deprecated
	public FluentEmailAssert<FluentEmailsAssert<P>> forEach() {
		return every();
	}
}