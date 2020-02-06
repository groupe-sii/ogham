package fr.sii.ogham.testing.assertion.sms;

import static fr.sii.ogham.testing.assertion.util.AssertionHelper.assertThat;
import static fr.sii.ogham.testing.assertion.util.AssertionHelper.usingContext;

import java.util.List;

import org.hamcrest.Matcher;

import fr.sii.ogham.testing.assertion.util.AssertionRegistry;
import fr.sii.ogham.testing.sms.simulator.bean.NumberingPlanIndicator;
import fr.sii.ogham.testing.sms.simulator.bean.TypeOfNumber;
import fr.sii.ogham.testing.util.HasParent;

/**
 * Make assertions on phone number of received messages.
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the parent type
 */
public class FluentPhoneNumberAssert<P> extends HasParent<P> {
	/**
	 * The list of phone numbers that will be used for assertions
	 */
	private final List<PhoneNumberWithContext> actual;
	private final AssertionRegistry registry;

	/**
	 * 
	 * @param actual
	 *            the received messages
	 * @param parent
	 *            the parent
	 * @param registry
	 *            used to register assertions
	 */
	public FluentPhoneNumberAssert(List<PhoneNumberWithContext> actual, P parent, AssertionRegistry registry) {
		super(parent);
		this.actual = actual;
		this.registry = registry;
	}

	/**
	 * Make assertions on a phone number.
	 * 
	 * <pre>
	 * .receivedMessages().message(0).to()
	 *    .number(is("+33102030405"))
	 * </pre>
	 * 
	 * Will check if the recipient phone number of the first message is exactly
	 * "+33102030405".
	 * 
	 * <pre>
	 * .receivedMessages().every().to()
	 *    .number(is("+33102030405"))
	 * </pre>
	 * 
	 * Will check if the recipient phone number of every message is exactly
	 * "+33102030405".
	 * 
	 * @param matcher
	 *            the assertion to apply on the phone number
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public FluentPhoneNumberAssert<P> number(Matcher<String> matcher) {
		String message = "number of ${numberName} of message ${messageIndex}";
		for (PhoneNumberWithContext numberWithContext : actual) {
			PhoneNumberInfo number = numberWithContext.getNumber();
			registry.register(() -> assertThat(number.getAddress(), usingContext(message, numberWithContext, matcher)));
		}
		return this;
	}

	/**
	 * Make assertions on a phone number.
	 * 
	 * <pre>
	 * .receivedMessages().message(0).to()
	 *    .typeOfNumber(is(TypeOfNumber.INTERNATIONAL))
	 * </pre>
	 * 
	 * Will check if the recipient phone number type of the first message is an
	 * international number.
	 * 
	 * <pre>
	 * .receivedMessages().every().to()
	 *    .typeOfNumber(is(TypeOfNumber.INTERNATIONAL))
	 * </pre>
	 * 
	 * Will check if the recipient phone number type of every message is an
	 * international number.
	 * 
	 * @param matcher
	 *            the assertion to apply on the type of number
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public FluentPhoneNumberAssert<P> typeOfNumber(Matcher<TypeOfNumber> matcher) {
		String message = "TypeOfNumber of ${numberName} of message ${messageIndex}";
		for (PhoneNumberWithContext numberWithContext : actual) {
			PhoneNumberInfo number = numberWithContext.getNumber();
			registry.register(() -> assertThat(TypeOfNumber.valueOf(number.getTon()), usingContext(message, numberWithContext, matcher)));
		}
		return this;
	}

	/**
	 * Make assertions on a phone number.
	 * 
	 * <pre>
	 * .receivedMessages().message(0).to()
	 *    .numberingPlanIndicator(is(NumberingPlanIndicator.ISDN))
	 * </pre>
	 * 
	 * Will check if the numbering plan indicator of the recipient phone number
	 * of the first message is NumberingPlanIndicator.ISDN.
	 * 
	 * <pre>
	 * .receivedMessages().every().to()
	 *    .numberingPlanIndicator(is(NumberingPlanIndicator.ISDN))
	 * </pre>
	 * 
	 * Will check if the numbering plan indicator of the recipient phone number
	 * of every message is NumberingPlanIndicator.ISDN.
	 * 
	 * @param matcher
	 *            the assertion to apply on the numbering plan indicator
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public FluentPhoneNumberAssert<P> numberingPlanIndicator(Matcher<NumberingPlanIndicator> matcher) {
		String message = "NumberPlanIndicator of ${numberName} of message ${messageIndex}";
		for (PhoneNumberWithContext numberWithContext : actual) {
			PhoneNumberInfo number = numberWithContext.getNumber();
			registry.register(() -> assertThat(NumberingPlanIndicator.valueOf(number.getNpi()), usingContext(message, numberWithContext, matcher)));
		}
		return this;
	}
}
