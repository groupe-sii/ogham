package fr.sii.ogham.testing.assertion.sms;

import static fr.sii.ogham.testing.assertion.util.AssertionHelper.assertThat;
import static fr.sii.ogham.testing.assertion.util.AssertionHelper.usingContext;
import static org.apache.commons.lang3.ArrayUtils.toObject;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;

import fr.sii.ogham.testing.sms.simulator.bean.Alphabet;
import fr.sii.ogham.testing.sms.simulator.bean.OptionalParameter;
import fr.sii.ogham.testing.sms.simulator.bean.SubmitSm;
import fr.sii.ogham.testing.sms.simulator.bean.Tag;
import fr.sii.ogham.testing.util.HasParent;

/**
 * Make assertions on received PDU.
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the parent type
 * @param <S>
 *            the type of received messages
 */
public class PduRequestAssert<P, S extends SubmitSm> extends HasParent<P> {
	private final List<PduRequestWithContext<S>> actual;

	/**
	 * 
	 * @param actual
	 *            the received messages
	 * @param parent
	 *            the parent
	 */
	public PduRequestAssert(List<PduRequestWithContext<S>> actual, P parent) {
		super(parent);
		this.actual = actual;
	}

	/**
	 * Make assertions on the encoding byte of the message(s) using fluent API.
	 * 
	 * <pre>
	 * .receivedMessages().message(0)
	 *   .rawRequest()
	 *     .encoding(is(0x01))
	 * </pre>
	 * 
	 * Will check if the encoding byte of the first message is exactly 0x01.
	 * 
	 * <pre>
	 * .receivedMessages().every()
	 *   .rawRequest()
	 *     .encoding(is(0x01))
	 * </pre>
	 * 
	 * Will check if the encoding byte of every message is exactly 0x01.
	 * 
	 * @param matcher
	 *            the assertion to apply on the encoding
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public PduRequestAssert<P, S> encoding(Matcher<Byte> matcher) {
		String message = "encoding of ${name} of message ${messageIndex}";
		for (PduRequestWithContext<S> rawContentWithContext : actual) {
			S msg = rawContentWithContext.getRequest();
			assertThat(org.jsmpp.bean.Alphabet.parseDataCoding(msg.getDataCoding()).value(), usingContext(message, rawContentWithContext, matcher));
		}
		return this;
	}

	/**
	 * Make assertions on the alphabet of the message(s) using fluent API. The
	 * alphabet is extracted from {@link SubmitSm#getDataCoding()} byte. It
	 * corresponds to the character table used to encode the message. It is a
	 * convenience method that is similar to {@link #encoding(Matcher)} but
	 * using human readable enum.
	 * 
	 * <pre>
	 * .receivedMessages().message(0)
	 *   .rawRequest()
	 *     .alphabet(is(Alphabet.ALPHA_DEFAULT))
	 * </pre>
	 * 
	 * Will check if the alphabet of the first message is exactly
	 * Alphabet.ALPHA_DEFAULT.
	 * 
	 * <pre>
	 * .receivedMessages().every()
	 *   .rawRequest()
	 *     .alphabet(is(Alphabet.ALPHA_DEFAULT))
	 * </pre>
	 * 
	 * Will check if the alphabet of every message is exactly
	 * Alphabet.ALPHA_DEFAULT.
	 * 
	 * @param matcher
	 *            the assertion to apply on the alphabet
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public PduRequestAssert<P, S> alphabet(Matcher<Alphabet> matcher) {
		String message = "alphabet of ${name} of message ${messageIndex}";
		for (PduRequestWithContext<S> rawContentWithContext : actual) {
			S msg = rawContentWithContext.getRequest();
			assertThat(Alphabet.from(org.jsmpp.bean.Alphabet.parseDataCoding(msg.getDataCoding()).value()), usingContext(message, rawContentWithContext, matcher));
		}
		return this;
	}

	/**
	 * Make assertions on the short message byte array of the message(s) using
	 * fluent API. The short message corresponds to the whole array of data. It
	 * always contains the payload (see
	 * {@link ShortMessageMessageAssert#payload(Matcher)}). It may contain the
	 * header (see {@link ShortMessageMessageAssert#header(Matcher)}). For
	 * example, if the original message is split into several segments, then
	 * each received message contains a header to indicate how the message was
	 * split (number of segments, reference number, current segment number,
	 * ...).
	 * 
	 * <pre>
	 * .receivedMessages().message(0)
	 *   .rawRequest()
	 *     .shortMessage(arrayWithSize(134))
	 * </pre>
	 * 
	 * Will check if the short message (header + payload) byte array of the
	 * first message has 134 bytes.
	 * 
	 * <pre>
	 * .receivedMessages().every()
	 *   .rawRequest()
	 *     .shortMessage(arrayWithSize(134))
	 * </pre>
	 * 
	 * Will check if the short message (header + payload) byte array of every
	 * message has 134 bytes.
	 * 
	 * @param matcher
	 *            the assertion to apply on the short message (header + payload)
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public PduRequestAssert<P, S> shortMessage(Matcher<? super Byte[]> matcher) {
		String message = "shortMessage of ${name} of message ${messageIndex}";
		for (PduRequestWithContext<S> rawContentWithContext : actual) {
			S msg = rawContentWithContext.getRequest();
			assertThat(toObject(msg.getShortMessage()), usingContext(message, rawContentWithContext, matcher));
		}
		return this;
	}

	/**
	 * Make assertions on the short message byte array of the message(s) using
	 * fluent API. The short message corresponds to the whole array of data. It
	 * always contains the payload (see
	 * {@link ShortMessageMessageAssert#payload(Matcher)}). It may contain the
	 * header (see {@link ShortMessageMessageAssert#header(Matcher)}). For
	 * example, if the original message is split into several segments, then
	 * each received message contains a header to indicate how the message was
	 * split (number of segments, reference number, current segment number,
	 * ...).
	 * 
	 * <pre>
	 * .receivedMessages().message(0)
	 *   .rawRequest()
	 *     .shortMessage()
	 *    	 .payload(arrayWithSize(134))
	 * </pre>
	 * 
	 * Will check if the short message payload byte array of the first message
	 * has 134 bytes.
	 * 
	 * <pre>
	 * .receivedMessages().every()
	 *   .rawRequest()
	 *     .shortMessage()
	 *    	 .payload(arrayWithSize(134))
	 * </pre>
	 * 
	 * Will check if the short message payload byte array of every message has
	 * 134 bytes.
	 * 
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public ShortMessageMessageAssert<PduRequestAssert<P, S>, S> shortMessage() {
		List<ShortMessageWithContext<S>> shortMessages = new ArrayList<>();
		for (PduRequestWithContext<S> rawContentWithContext : actual) {
			S msg = rawContentWithContext.getRequest();
			shortMessages.add(new ShortMessageWithContext<>(msg, rawContentWithContext));
		}
		return new ShortMessageMessageAssert<>(shortMessages, this);
	}

	/**
	 * Make assertions on optional parameter with particular tag.
	 * 
	 * <pre>
	 * .receivedMessages().message(0)
	 *   .rawRequest()
	 *     .optionalParameter(Tag.MESSAGE_PAYLOAD)
	 *    	 .value(arrayWithSize(134))
	 * </pre>
	 * 
	 * Will check if the short message payload byte array of the
	 * 'message_payload' optional parameter of the first message has 134 bytes.
	 * 
	 * <pre>
	 * .receivedMessages().every()
	 *   .rawRequest()
	 *     .optionalParameter(Tag.MESSAGE_PAYLOAD)
	 *    	 .value(arrayWithSize(134))
	 * </pre>
	 * 
	 * Will check if the short message payload byte array of the
	 * 'message_payload' optional parameter of every message has 134 bytes.
	 * 
	 * @param withTag
	 *            the particular tag to search and to make assertions on
	 * @see OptionalParameter
	 * @see Tag
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public OptionalParameterAssert<PduRequestAssert<P, S>> optionalParameter(Tag withTag) {
		List<OptionalParameterWithContext> parameters = new ArrayList<>();
		for (PduRequestWithContext<S> rawContentWithContext : actual) {
			OptionalParameter parameter = getOptionalParameter(rawContentWithContext.getRequest(), withTag);
			parameters.add(new OptionalParameterWithContext(withTag, parameter, rawContentWithContext));
		}
		return new OptionalParameterAssert<>(this, parameters);
	}

	private OptionalParameter getOptionalParameter(S msg, Tag tag) {
		return msg.getOptionalParameter(tag);
	}
}
