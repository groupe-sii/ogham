package fr.sii.ogham.assertion.sms;

import static fr.sii.ogham.assertion.AssertionHelper.assertThat;
import static fr.sii.ogham.assertion.OghamAssertions.usingContext;
import static java.util.Collections.unmodifiableList;
import static org.apache.commons.lang3.ArrayUtils.toObject;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matcher;

import fr.sii.ogham.assertion.HasParent;
import fr.sii.ogham.helper.sms.bean.SubmitSm;

/**
 * Make assertions on {@code short_message} field.
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            Parent type
 * @param <S>
 *            Sent SubmitSm type
 */
public class ShortMessageMessageAssert<P, S extends SubmitSm> extends HasParent<P> {
	private final List<ShortMessageWithContext<S>> actual;

	/**
	 * Initializes with the list of short messages and the parent.
	 * 
	 * @param actual
	 *            the list of short messages (whit extra context for error
	 *            reporting)
	 * @param parent
	 *            the parent (used by {@link #and()})
	 */
	public ShortMessageMessageAssert(List<ShortMessageWithContext<S>> actual, P parent) {
		super(parent);
		this.actual = unmodifiableList(actual);
	}

	/**
	 * Make assertions on the header byte array of short message field of the
	 * message(s) using fluent API. The header byte array may be null. This is
	 * the default behavior. However, if the original message is split into
	 * several segments, each segment has a header that contains information to
	 * indicate how the message was split (number of segments, reference number,
	 * current segment number, ...).
	 * 
	 * <pre>
	 * .receivedMessages()
	 *   .message(0)
	 *     .rawRequest()
	 *       .shortMessage()
	 *         .header(array(equalTo(0x01), equalTo(0x02)))
	 * </pre>
	 * 
	 * Will check if the header byte array of the first message is exactly
	 * [0x01, 0x02].
	 * 
	 * <pre>
	 * .receivedMessages()
	 *   .every()
	 *     .rawRequest()
	 *       .shortMessage()
	 *         .header(array(equalTo(0x01), equalTo(0x02)))
	 * </pre>
	 * 
	 * Will check if the header byte array of every message is exactly [0x01,
	 * 0x02].
	 * 
	 * @param matcher
	 *            the assertion to apply on the header
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public ShortMessageMessageAssert<P, S> header(Matcher<? super Byte[]> matcher) {
		String message = "header of ${name} of message ${messageIndex}";
		for (ShortMessageWithContext<S> shortMessageWithContext : actual) {
			S msg = shortMessageWithContext.getRequest();
			assertThat(toObject(getHeader(msg)), usingContext(message, shortMessageWithContext, matcher));
		}
		return this;
	}

	/**
	 * Make assertions on the payload byte array of short message field of the
	 * message(s) using fluent API. The payload is the part without the header
	 * (see {@link #header(Matcher)}). It contains the message (as byte array)
	 * displayed to the end-user.
	 * 
	 * <pre>
	 * .receivedMessages()
	 *   .message(0)
	 *     .rawRequest()
	 *       .shortMessage()
	 *         .payload(arrayWithSize(160))
	 * </pre>
	 * 
	 * Will check if the payload byte array of the first message has exactly 160
	 * bytes.
	 * 
	 * <pre>
	 * .receivedMessages()
	 *   .every()
	 *     .rawRequest()
	 *       .shortMessage()
	 *         .payload(arrayWithSize(160))
	 * </pre>
	 * 
	 * Will check if the payload byte array of the first message has exactly 160
	 * bytes.
	 * 
	 * @param matcher
	 *            the assertion to apply on the payload
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public ShortMessageMessageAssert<P, S> payload(Matcher<? super Byte[]> matcher) {
		String message = "payload of ${name} of message ${messageIndex}";
		for (ShortMessageWithContext<S> shortMessageWithContext : actual) {
			S msg = shortMessageWithContext.getRequest();
			assertThat(toObject(getPayload(msg)), usingContext(message, shortMessageWithContext, matcher));
		}
		return this;
	}

	@SuppressWarnings("squid:S1168")
	private byte[] getHeader(S msg) {
		byte[] shortMessage = msg.getShortMessage();
		if (msg.isUdhi()) {
			return Arrays.copyOfRange(shortMessage, 0, headerLength(shortMessage));
		}
		return null;
	}

	private static int headerLength(byte[] shortMessage) {
		return shortMessage[0] + 1;
	}

	private byte[] getPayload(S msg) {
		byte[] shortMessage = msg.getShortMessage();
		if (msg.isUdhi()) {
			return Arrays.copyOfRange(shortMessage, headerLength(shortMessage), shortMessage.length);
		}
		return shortMessage;
	}
}
