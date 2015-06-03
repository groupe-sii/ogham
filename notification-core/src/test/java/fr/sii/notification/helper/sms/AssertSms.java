package fr.sii.notification.helper.sms;

import java.util.List;

import org.jsmpp.bean.SubmitSm;
import org.junit.Assert;

/**
 * Utility class for checking if the received SMS content is as expected.
 * 
 * @author Aurélien Baudet
 *
 */
public class AssertSms {
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
		Assert.assertEquals("Sender number should be " + expected.getSenderNumber(), expected.getSenderNumber(), actual.getSourceAddr());
		Assert.assertEquals("Receiver number should be " + expected.getReceiverNumber(), expected.getReceiverNumber(), actual.getDestAddress());
		Assert.assertEquals("Message not consistent with expected", expected.getMessage(), new String(actual.getShortMessage()));
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
	 * @param actual
	 *            the received SMS
	 */
	public static void assertEquals(ExpectedSms expected, List<SubmitSm> receivedMessages) {
		Assert.assertEquals("should have received exactly one message", 1, receivedMessages.size());
		assertEquals(expected, receivedMessages.get(0));
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
		Assert.assertEquals("should have received exactly " + expected.size() + " messages", expected.size(), receivedMessages.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), receivedMessages.get(i));
		}
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
		assertEquals(expected.getParts(), receivedMessages);
	}
}
