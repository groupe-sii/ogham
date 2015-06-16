package fr.sii.notification.sms.message.addressing.translator;

import fr.sii.notification.sms.message.PhoneNumber;
import fr.sii.notification.sms.message.addressing.NumberingPlanIndicator;
import fr.sii.notification.sms.message.addressing.TypeOfNumber;

/**
 * 
 * If the sender address is alphanumeric (contains both letters and numbers) or
 * non-numeric, TON is set to 5 and NPI to 0.
 * 
 * @author cdejonghe
 * 
 */
public class AlphanumericCodeNumberFormatHandler extends AbstractFixedPhoneNumberHandler {

	private static final String NUMERIC_ONLY_PATTERN = "(\\+)?[0-9]+";

	public AlphanumericCodeNumberFormatHandler() {
		super(TypeOfNumber.ALPHANUMERIC, NumberingPlanIndicator.UNKNOWN);
	}

	@Override
	public boolean supports(PhoneNumber phoneNumber) {
		return phoneNumber != null && phoneNumber.getNumber() != null
				&& !phoneNumber.getNumber().matches(NUMERIC_ONLY_PATTERN);
	}
}