package fr.sii.ogham.assertion.sms;

import static fr.sii.ogham.assertion.AssertionHelper.assertThat;
import static fr.sii.ogham.assertion.OghamAssertions.usingContext;
import static fr.sii.ogham.helper.sms.SmsUtils.getSmsContent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matcher;
import org.jsmpp.bean.SubmitSm;

import fr.sii.ogham.assertion.HasParent;
import fr.sii.ogham.assertion.context.SingleMessageContext;

public class SmsAssert<P> extends HasParent<P> {
	/**
	 * The list of messages that will be used for assertions
	 */
	private final List<SubmitSm> actual;

	public SmsAssert(SubmitSm actual, P parent) {
		this(Arrays.asList(actual), parent);
	}

	public SmsAssert(List<SubmitSm> actual, P parent) {
		super(parent);
		this.actual = actual;
	}

	/**
	 * Make assertions on the content of the message(s).
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
	 * .receivedMessages().forEach()
	 *    .content(allOf(notNullValue(), is("hello world"))
	 * </pre>
	 * 
	 * Or:
	 * 
	 * <pre>
	 * .receivedMessages().forEach()
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
	public SmsAssert<P> content(Matcher<String> matcher) {
		String desc = "content of message ${messageIndex}";
		int index = 0;
		for (SubmitSm message : actual) {
			assertThat(getSmsContent(message), usingContext(desc, new SingleMessageContext(index++), matcher));
		}
		return this;
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
	 * .receivedMessages().forEach()
	 *    .from(notNullValue())
	 * </pre>
	 * 
	 * Will check if the sender phone number of every message is not null.
	 * 
	 * @param matcher
	 *            the assertion to apply on the phone number
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public SmsAssert<P> from(Matcher<PhoneNumberInfo> matcher) {
		String desc = "sender of message ${messageIndex}";
		int index = 0;
		for (SubmitSm message : actual) {
			PhoneNumberInfo number = new PhoneNumberInfo(message.getSourceAddr(), message.getSourceAddrNpi(), message.getSourceAddrTon());
			assertThat(number, usingContext(desc, new SingleMessageContext(index++), matcher));
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
	 * .receivedMessages().forEach().from()
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
	public PhoneNumberAssert<SmsAssert<P>> from() {
		List<PhoneNumberWithContext> numbers = new ArrayList<>();
		int index = 0;
		for (SubmitSm message : actual) {
			PhoneNumberInfo number = new PhoneNumberInfo(message.getSourceAddr(), message.getSourceAddrNpi(), message.getSourceAddrTon());
			numbers.add(new PhoneNumberWithContext(number, "sender", new SingleMessageContext(index++)));
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
	 * .receivedMessages().forEach()
	 *    .to(notNullValue())
	 * </pre>
	 * 
	 * Will check if the recipient phone number of every message is not null.
	 * 
	 * @param matcher
	 *            the assertion to apply on the phone number
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public SmsAssert<P> to(Matcher<PhoneNumberInfo> matcher) {
		String desc = "dest of message ${messageIndex}";
		int index = 0;
		for (SubmitSm message : actual) {
			PhoneNumberInfo number = new PhoneNumberInfo(message.getDestAddress(), message.getDestAddrNpi(), message.getDestAddrTon());
			assertThat(number, usingContext(desc, new SingleMessageContext(index++), matcher));
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
	 * .receivedMessages().forEach().to()
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
	public PhoneNumberAssert<SmsAssert<P>> to() {
		List<PhoneNumberWithContext> numbers = new ArrayList<>();
		int index = 0;
		for (SubmitSm message : actual) {
			PhoneNumberInfo number = new PhoneNumberInfo(message.getDestAddress(), message.getDestAddrNpi(), message.getDestAddrTon());
			numbers.add(new PhoneNumberWithContext(number, "dest", new SingleMessageContext(index++)));
		}
		return new PhoneNumberAssert<>(numbers, this);
	}

}
