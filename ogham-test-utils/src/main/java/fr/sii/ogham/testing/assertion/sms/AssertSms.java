package fr.sii.ogham.testing.assertion.sms;

import fr.sii.ogham.testing.assertion.util.AssertionRegistry;
import fr.sii.ogham.testing.assertion.util.FailAtEndRegistry;
import fr.sii.ogham.testing.sms.simulator.bean.Address;
import fr.sii.ogham.testing.sms.simulator.bean.SubmitSm;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.function.Function;

import static fr.sii.ogham.testing.sms.simulator.decode.SmsUtils.getSmsContent;

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
		assertEquals("1/1", expected, actual, registry);
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
		registry.register(() -> Assertions.assertEquals(1, receivedMessages.size(), "should have received exactly one message"));
		assertEquals("1/1", expected, receivedMessages.size()==1 ? receivedMessages.get(0) : null, registry);
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
		registry.register(() -> Assertions.assertEquals(expected.size(), receivedMessages.size(), "should have received exactly " + expected.size() + " message(s)"));
		for (int i = 0; i < expected.size(); i++) {
			assertEquals((i+1)+"/"+expected.size(), expected.get(i), i<receivedMessages.size() ? receivedMessages.get(i) : null, registry);
		}
	}

	private static void assertEquals(String msgIdx, ExpectedSms expected, SubmitSm actual, AssertionRegistry registry) {
		registry.register(() -> Assertions.assertEquals(expected.getSenderNumber().getNumber(), getValue(actual, SubmitSm::getSourceAddress, Address::getAddress), "Sender number of message "+ msgIdx + " should be " + expected.getSenderNumber().getNumber()));
		registry.register(() -> Assertions.assertEquals((Byte) expected.getSenderNumber().getTon(), getValue(actual, SubmitSm::getSourceAddress, Address::getTon), "Sender ton of message "+ msgIdx + " should be " + expected.getSenderNumber().getTon()));
		registry.register(() -> Assertions.assertEquals((Byte) expected.getSenderNumber().getNpi(), getValue(actual, SubmitSm::getSourceAddress, Address::getNpi), "Sender npi of message "+ msgIdx + " should be " + expected.getSenderNumber().getNpi()));

		registry.register(() -> Assertions.assertEquals(expected.getReceiverNumber().getNumber(), getValue(actual, SubmitSm::getDestAddress, Address::getAddress), "Receiver number of message "+ msgIdx + " should be " + expected.getReceiverNumber().getNumber()));
		registry.register(() -> Assertions.assertEquals((Byte) expected.getReceiverNumber().getTon(), getValue(actual, SubmitSm::getDestAddress, Address::getTon), "Receiver ton of message "+ msgIdx + "  should be " + expected.getReceiverNumber().getTon()));
		registry.register(() -> Assertions.assertEquals((Byte) expected.getReceiverNumber().getNpi(), getValue(actual, SubmitSm::getDestAddress, Address::getNpi) ,"Receiver npi of message "+ msgIdx + "  should be " + expected.getReceiverNumber().getNpi()));

		registry.register(() -> Assertions.assertEquals(expected.getMessage(), actual==null ? null : getSmsContent(actual), "Message " + msgIdx + " not consistent with expected"));
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
