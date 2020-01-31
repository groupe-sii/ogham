package fr.sii.ogham.testing.assertion.sms;

import static fr.sii.ogham.testing.assertion.util.AssertionHelper.assertThat;
import static fr.sii.ogham.testing.assertion.util.AssertionHelper.usingContext;
import static fr.sii.ogham.testing.sms.simulator.decode.SmsUtils.getSmsContent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matcher;

import fr.sii.ogham.testing.assertion.context.SingleMessageContext;
import fr.sii.ogham.testing.sms.simulator.bean.SubmitSm;
import fr.sii.ogham.testing.sms.simulator.decode.Charset;
import fr.sii.ogham.testing.util.HasParent;

/**
 * Make assertions on particular message.
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the parent type
 * @param <S>
 *            the type of the {@link SubmitSm}
 */
public class SmsAssert<P, S extends SubmitSm> extends HasParent<P> {
	/**
	 * The list of messages that will be used for assertions
	 */
	private final List<S> actual;
	private int index;

	/**
	 * Initializes with a single received message
	 * 
	 * @param actual
	 *            received message
	 * @param index
	 *            the index of the message
	 * @param parent
	 *            the parent
	 */
	public SmsAssert(S actual, int index, P parent) {
		this(Arrays.asList(actual), parent);
		this.index = index;
	}

	/**
	 * Initializes with several received messages
	 * 
	 * @param actual
	 *            received messages
	 * @param parent
	 *            the parent
	 */
	public SmsAssert(List<S> actual, P parent) {
		super(parent);
		this.actual = actual;
	}

	/**
	 * Make assertions on the content of the message(s). Alphabet/encoding is
	 * automatically determined from message (based on Data Coding Scheme).
	 * 
	 * <pre>
	 * .receivedMessages().message(0)
	 *    .content(allOf(notNullValue(), is("hello world"))
	 * </pre>
	 * 
	 * Or:
	 * 
	 * <pre>
	 * .receivedMessages().message(0)
	 *    .content(notNullValue())
	 *    .content(is("hello world"))
	 * </pre>
	 * 
	 * Will check if the content of the first message is not null and is exactly
	 * "hello world".
	 * 
	 * <pre>
	 * .receivedMessages().every()
	 *    .content(allOf(notNullValue(), is("hello world"))
	 * </pre>
	 * 
	 * Or:
	 * 
	 * <pre>
	 * .receivedMessages().every()
	 *    .content(notNullValue())
	 *    .content(is("hello world"))
	 * </pre>
	 * 
	 * Will check if the content of every message is not null and is exactly
	 * "hello world".
	 * 
	 * @param matcher
	 *            the assertion to apply on message content
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public SmsAssert<P, S> content(Matcher<? super String> matcher) {
		String desc = "content of message ${messageIndex}";
		int msgIdx = index;
		for (S message : actual) {
			assertThat(getSmsContent(message), usingContext(desc, new SingleMessageContext(msgIdx++), matcher));
		}
		return this;
	}

	/**
	 * Make assertions on the content of the message(s). Alphabet/encoding used
	 * to decode the message is explicitly provided.
	 * 
	 * <pre>
	 * .receivedMessages().message(0)
	 *    .content(allOf(notNullValue(), is("hello world"))
	 * </pre>
	 * 
	 * Or:
	 * 
	 * <pre>
	 * .receivedMessages().message(0)
	 *    .content(notNullValue())
	 *    .content(is("hello world"))
	 * </pre>
	 * 
	 * Will check if the content of the first message is not null and is exactly
	 * "hello world".
	 * 
	 * <pre>
	 * .receivedMessages().every()
	 *    .content(allOf(notNullValue(), is("hello world"))
	 * </pre>
	 * 
	 * Or:
	 * 
	 * <pre>
	 * .receivedMessages().every()
	 *    .content(notNullValue())
	 *    .content(is("hello world"))
	 * </pre>
	 * 
	 * Will check if the content of every message is not null and is exactly
	 * "hello world".
	 * 
	 * @param charset
	 *            the charset to use to decode the message (bytes to string)
	 * @param matcher
	 *            the assertion to apply on message content
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public SmsAssert<P, S> content(Charset charset, Matcher<? super String> matcher) {
		String desc = "content of message ${messageIndex}";
		int msgIdx = index;
		for (S message : actual) {
			assertThat(getSmsContent(message, charset), usingContext(desc, new SingleMessageContext(msgIdx++), matcher));
		}
		return this;
	}

	/**
	 * Make assertions on the raw request ({@link SubmitSm}) of the message(s)
	 * using fluent API.
	 * 
	 * <pre>
	 * .receivedMessages().message(0).rawRequest()
	 *   .alphabet(is(Alphabet.ALPHA_DEFAULT))
	 *   .shortMessage()
	 *     .header(array(equalTo(0x01), equalTo(0x02), equalTo(0x03))))
	 *     .payload(arrayWithSize(10))
	 * </pre>
	 * 
	 * Will check if the received byte array of the first message has a header
	 * with some expected bytes, the alphabet used to encode the message is
	 * ALPHA_DEFAULT and the payload has exactly 10 bytes.
	 * 
	 * <pre>
	 * .receivedMessages().every().rawRequest()
	 *   .alphabet(is(Alphabet.ALPHA_DEFAULT))
	 *   .shortMessage()
	 *     .header(array(equalTo(0x01), equalTo(0x02), equalTo(0x03))))
	 *     .payload(arrayWithSize(10))
	 * </pre>
	 * 
	 * Will check if the received byte array of every message has a header with
	 * some expected bytes, the alphabet used to encode the message is
	 * ALPHA_DEFAULT and the payload has exactly 10 bytes.
	 * 
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public PduRequestAssert<SmsAssert<P, S>, S> rawRequest() {
		List<PduRequestWithContext<S>> requests = new ArrayList<>();
		int msgIdx = index;
		for (S request : actual) {
			requests.add(new PduRequestWithContext<>(request, "raw request", new SingleMessageContext(msgIdx++)));
		}
		return new PduRequestAssert<>(requests, this);
	}

	/**
	 * Make assertions on the sender of the message(s) using fluent API.
	 * 
	 * <pre>
	 * .receivedMessages().message(0)
	 *    .from(notNullValue())
	 * </pre>
	 * 
	 * Will check if the sender phone number of the first message is not null.
	 * 
	 * <pre>
	 * .receivedMessages().every()
	 *    .from(notNullValue())
	 * </pre>
	 * 
	 * Will check if the sender phone number of every message is not null.
	 * 
	 * @param matcher
	 *            the assertion to apply on the phone number
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public SmsAssert<P, S> from(Matcher<PhoneNumberInfo> matcher) {
		String desc = "sender of message ${messageIndex}";
		int msgIdx = index;
		for (S message : actual) {
			PhoneNumberInfo number = new PhoneNumberInfo(message.getSourceAddress());
			assertThat(number, usingContext(desc, new SingleMessageContext(msgIdx++), matcher));
		}
		return this;
	}

	/**
	 * Make assertions on the sender of the message(s) using fluent API.
	 * 
	 * <pre>
	 * .receivedMessages().message(0).from()
	 *    .number(is("+33102030405"))
	 *    .typeOfNumber(is(TypeOfNumber.INTERNATIONAL))
	 * </pre>
	 * 
	 * Will check if the sender phone number of the first message is exactly
	 * "+33102030405" and sender phone number type of the first message is an
	 * international number.
	 * 
	 * <pre>
	 * .receivedMessages().every().from()
	 *    .number(is("+33102030405"))
	 *    .typeOfNumber(is(TypeOfNumber.INTERNATIONAL))
	 * </pre>
	 * 
	 * Will check if the sender phone number of every message is exactly
	 * "+33102030405" and sender phone number type of every message is an
	 * international number.
	 * 
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public PhoneNumberAssert<SmsAssert<P, S>> from() {
		List<PhoneNumberWithContext> numbers = new ArrayList<>();
		int msgIdx = index;
		for (S message : actual) {
			PhoneNumberInfo number = new PhoneNumberInfo(message.getSourceAddress());
			numbers.add(new PhoneNumberWithContext(number, "sender", new SingleMessageContext(msgIdx++)));
		}
		return new PhoneNumberAssert<>(numbers, this);
	}

	/**
	 * Make assertions on the recipient of the message(s) using fluent API.
	 * 
	 * <pre>
	 * .receivedMessages().message(0)
	 *    .to(notNullValue())
	 * </pre>
	 * 
	 * Will check if the recipient phone number of the first message is not
	 * null.
	 * 
	 * <pre>
	 * .receivedMessages().every()
	 *    .to(notNullValue())
	 * </pre>
	 * 
	 * Will check if the recipient phone number of every message is not null.
	 * 
	 * @param matcher
	 *            the assertion to apply on the phone number
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public SmsAssert<P, S> to(Matcher<PhoneNumberInfo> matcher) {
		String desc = "recipient of message ${messageIndex}";
		int msgIdx = index;
		for (S message : actual) {
			PhoneNumberInfo number = new PhoneNumberInfo(message.getDestAddress());
			assertThat(number, usingContext(desc, new SingleMessageContext(msgIdx++), matcher));
		}
		return this;
	}

	/**
	 * Make assertions on the recipient of the message(s) using fluent API.
	 * 
	 * <pre>
	 * .receivedMessages().message(0).to()
	 *    .number(is("+33102030405"))
	 *    .typeOfNumber(is(TypeOfNumber.INTERNATIONAL))
	 * </pre>
	 * 
	 * Will check if the recipient phone number of the first message is exactly
	 * "+33102030405" and recipient phone number type of the first message is an
	 * international number.
	 * 
	 * <pre>
	 * .receivedMessages().every().to()
	 *    .number(is("+33102030405"))
	 *    .typeOfNumber(is(TypeOfNumber.INTERNATIONAL))
	 * </pre>
	 * 
	 * Will check if the recipient phone number of every message is exactly
	 * "+33102030405" and recipient phone number type of every message is an
	 * international number.
	 * 
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public PhoneNumberAssert<SmsAssert<P, S>> to() {
		List<PhoneNumberWithContext> numbers = new ArrayList<>();
		int msgIdx = index;
		for (S message : actual) {
			PhoneNumberInfo number = new PhoneNumberInfo(message.getDestAddress());
			numbers.add(new PhoneNumberWithContext(number, "recipient", new SingleMessageContext(msgIdx++)));
		}
		return new PhoneNumberAssert<>(numbers, this);
	}

}
