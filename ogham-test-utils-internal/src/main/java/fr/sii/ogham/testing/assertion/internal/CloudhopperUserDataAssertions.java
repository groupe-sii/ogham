package fr.sii.ogham.testing.assertion.internal;

import static org.apache.commons.lang3.reflect.FieldUtils.readField;
import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matcher;

import fr.sii.ogham.sms.sender.impl.CloudhopperSMPPSender;
import fr.sii.ogham.sms.sender.impl.cloudhopper.preparator.MessagePreparator;
import fr.sii.ogham.sms.sender.impl.cloudhopper.preparator.ShortMessagePreparator;
import fr.sii.ogham.sms.sender.impl.cloudhopper.preparator.TlvMessagePayloadMessagePreparator;
import fr.sii.ogham.testing.util.HasParent;

/**
 * Make assertions on "User Data" to ensure that it is transported using the
 * right field.
 * 
 * @author Aurélien Baudet
 *
 */
public class CloudhopperUserDataAssertions extends HasParent<CloudhopperAssertions> {

	private final CloudhopperSMPPSender cloudhopperSender;

	public CloudhopperUserDataAssertions(CloudhopperAssertions parent, CloudhopperSMPPSender cloudhopperSender) {
		super(parent);
		this.cloudhopperSender = cloudhopperSender;
	}

	/**
	 * Ensure that short message field is used to transport "User Data".
	 * 
	 * @param matcher
	 *            the matcher to ensure that short message field is used or not.
	 * @return this instance for fluent chaining
	 */
	public CloudhopperUserDataAssertions useShortMessage(Matcher<Boolean> matcher) {
		MessagePreparator preparator = getPreparator(cloudhopperSender);
		assertThat(preparator instanceof ShortMessagePreparator, matcher);
		return this;
	}

	/**
	 * Ensure that "message_payload" optional TLV parameter is used to transport
	 * "User Data".
	 * 
	 * @param matcher
	 *            the matcher to ensure that "message_payload" parameter is used
	 *            or not.
	 * @return this instance for fluent chaining
	 */
	public CloudhopperUserDataAssertions useMessagePayloadTlvParameter(Matcher<Boolean> matcher) {
		MessagePreparator preparator = getPreparator(cloudhopperSender);
		assertThat(preparator instanceof TlvMessagePayloadMessagePreparator, matcher);
		return this;
	}

	private MessagePreparator getPreparator(CloudhopperSMPPSender cloudhopperSender) {
		try {
			return (MessagePreparator) readField(cloudhopperSender, "messagePreparator", true);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Failed to get 'messagePreparator' of CloudhopperSMPPSender", e);
		}
	}
}
