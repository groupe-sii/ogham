package fr.sii.ogham.testing.assertion.sms;

import static fr.sii.ogham.testing.sms.simulator.decode.SmsUtils.getSmsContent;

import java.util.List;
import java.util.function.Function;

import org.junit.Assert;

import fr.sii.ogham.testing.assertion.util.AssertionRegistry;
import fr.sii.ogham.testing.assertion.util.FailAtEndRegistry;
import fr.sii.ogham.testing.sms.simulator.bean.Address;
import fr.sii.ogham.testing.sms.simulator.bean.SubmitSm;

/**
 * Utility class for checking if the received SMS content is as expected.
 * 
 * @author Aurélien Baudet
 *
 */
public final class AssertSms {

	/**
	 * Assert that the fields of the received SMS using SMPP protocol are equal
	 * to the expected values. It will check that:
	 * <ul>
	 * <li>The received sender address corresponds to the expected phone number
	 * of the sender</li>
	 * <li>The received receiver address corresponds to the expected phone
	 * number of the receiver</li>
	 * <li>The received message corresponds to the expected message</li>
	 * </ul>
	 * 
	 * @param expected
	 *            all the fields with their expected values
	 * @param actual
	 *            the received SMS
	 */
	public static void assertEquals(ExpectedSms expected, SubmitSm actual) {
		AssertionRegistry registry = new FailAtEndRegistry();
		assertEquals(expected, actual, registry);
		registry.execute();
	}

	/**
	 * Assert that the fields of the received SMS using SMPP protocol are equal
	 * to the expected values. It will check that:
	 * <ul>
	 * <li>The received sender address corresponds to the expected phone number
	 * of the sender</li>
	 * <li>The received receiver address corresponds to the expected phone
	 * number of the receiver</li>
	 * <li>The received message corresponds to the expected message</li>
	 * </ul>
	 * <p>
	 * It also checks that there is exactly one received message.
	 * </p>
	 * <p>
	 * This is a shortcut useful in unit testing.
	 * </p>
	 * 
	 * @param expected
	 *            all the fields with their expected values
	 * @param receivedMessages
	 *            the list of received SMS
	 */
	public static void assertEquals(ExpectedSms expected, List<SubmitSm> receivedMessages) {
		AssertionRegistry registry = new FailAtEndRegistry();
		registry.register(() -> Assert.assertEquals("should have received exactly one message", 1, receivedMessages.size()));
		assertEquals(expected, receivedMessages.size()==1 ? receivedMessages.get(0) : null, registry);
		registry.execute();
	}

	/**
	 * Assert that the fields of each received SMS using SMPP protocol are equal
	 * to values of each expected message. For each message, it will check that:
	 * <ul>
	 * <li>The received sender address corresponds to the expected phone number
	 * of the sender</li>
	 * <li>The received receiver address corresponds to the expected phone
	 * number of the receiver</li>
	 * <li>The received message corresponds to the expected message</li>
	 * </ul>
	 * <p>
	 * It also checks that there are exactly the same number of received message
	 * as the expected number.
	 * </p>
	 * 
	 * @param expected
	 *            all the fields with their expected values
	 * @param receivedMessages
	 *            the received SMS
	 */
	public static void assertEquals(List<ExpectedSms> expected, List<SubmitSm> receivedMessages) {
		AssertionRegistry registry = new FailAtEndRegistry();
		assertEquals(expected, receivedMessages, registry);
		registry.execute();
	}

	/**
	 * <p>
	 * When message is too long, the SMS is split into several parts. This
	 * method helps to check this case.
	 * </p>
	 * Assert that the fields of each received SMS using SMPP protocol are equal
	 * to values of each expected message part. For each message part, it will
	 * check that:
	 * <ul>
	 * <li>The received sender address corresponds to the expected phone number
	 * of the sender</li>
	 * <li>The received receiver address corresponds to the expected phone
	 * number of the receiver</li>
	 * <li>The received message corresponds to the expected message</li>
	 * </ul>
	 * <p>
	 * It also checks that there are exactly the same number of received message
	 * as the expected number of parts.
	 * </p>
	 * 
	 * @param expected
	 *            all the fields with their expected values
	 * @param receivedMessages
	 *            the received SMS
	 */
	public static void assertEquals(SplitSms expected, List<SubmitSm> receivedMessages) {
		AssertionRegistry registry = new FailAtEndRegistry();
		assertEquals(expected.getParts(), receivedMessages, registry);
		registry.execute();
	}

	private static void assertEquals(List<ExpectedSms> expected, List<SubmitSm> receivedMessages, AssertionRegistry registry) {
		registry.register(() -> Assert.assertEquals("should have received exactly " + expected.size() + " messages", expected.size(), receivedMessages.size()));
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), i<receivedMessages.size() ? receivedMessages.get(i) : null, registry);
		}
	}

	private static void assertEquals(ExpectedSms expected, SubmitSm actual, AssertionRegistry registry) {
		registry.register(() -> Assert.assertEquals("Sender number should be " + expected.getSenderNumber().getNumber(), expected.getSenderNumber().getNumber(), getValue(actual, SubmitSm::getSourceAddress, Address::getAddress)));
		registry.register(() -> Assert.assertEquals("Sender ton should be " + expected.getSenderNumber().getTon(), (Byte) expected.getSenderNumber().getTon(), getValue(actual, SubmitSm::getSourceAddress, Address::getTon)));
		registry.register(() -> Assert.assertEquals("Sender npi should be " + expected.getSenderNumber().getNpi(), (Byte) expected.getSenderNumber().getNpi(), getValue(actual, SubmitSm::getSourceAddress, Address::getNpi)));

		registry.register(() -> Assert.assertEquals("Receiver number should be " + expected.getReceiverNumber().getNumber(), expected.getReceiverNumber().getNumber(), getValue(actual, SubmitSm::getDestAddress, Address::getAddress)));
		registry.register(() -> Assert.assertEquals("Receiver ton should be " + expected.getReceiverNumber().getTon(), (Byte) expected.getReceiverNumber().getTon(), getValue(actual, SubmitSm::getDestAddress, Address::getTon)));
		registry.register(() -> Assert.assertEquals("Receiver npi should be " + expected.getReceiverNumber().getNpi(), (Byte) expected.getReceiverNumber().getNpi(), getValue(actual, SubmitSm::getDestAddress, Address::getNpi)));

		registry.register(() -> Assert.assertEquals("Message not consistent with expected", expected.getMessage(), actual==null ? null : getSmsContent(actual)));
	}

	private static <T> T getValue(SubmitSm actual, Function<SubmitSm, Address> addressAccessor, Function<Address, T> valueAccessor) {
		if (actual == null) {
			return null;
		}
		Address address = addressAccessor.apply(actual);
		if (address == null) {
			return null;
		}
		return valueAccessor.apply(address);
	}

	private AssertSms() {
		super();
	}
}
