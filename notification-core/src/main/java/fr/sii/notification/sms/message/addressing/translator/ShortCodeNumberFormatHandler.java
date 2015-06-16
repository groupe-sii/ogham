package fr.sii.notification.sms.message.addressing.translator;

import fr.sii.notification.sms.message.PhoneNumber;
import fr.sii.notification.sms.message.addressing.NumberingPlanIndicator;
import fr.sii.notification.sms.message.addressing.TypeOfNumber;

/**
 * Phone number handler for short codes. If the sender address is a short code,
 * TON is set to 3, and NPI is set to 0. A number is considered to be a short
 * code if the length of the number is 5 digts or less.
 * 
 * @author cdejonghe
 * 
 */
public class ShortCodeNumberFormatHandler extends AbstractFixedPhoneNumberHandler {

	private static final int SHORTCODE_LENGTH = 5;

	public ShortCodeNumberFormatHandler() {
		super(TypeOfNumber.NETWORK_SPECIFIC, NumberingPlanIndicator.UNKNOWN);
	}

	@Override
	public boolean supports(PhoneNumber phoneNumber) {
		return phoneNumber != null && phoneNumber.getNumber() != null && phoneNumber.getNumber().length() <= SHORTCODE_LENGTH;
	}
}