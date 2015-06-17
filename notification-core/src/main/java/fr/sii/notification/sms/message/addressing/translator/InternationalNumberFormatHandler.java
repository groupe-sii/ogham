package fr.sii.notification.sms.message.addressing.translator;

import fr.sii.notification.sms.message.PhoneNumber;
import fr.sii.notification.sms.message.addressing.NumberingPlanIndicator;
import fr.sii.notification.sms.message.addressing.TypeOfNumber;

/**
 * Loose International phone number handler. If the sender starts with a "+",
 * TON is set to 1, and NPI is set to 1.
 * 
 * @author cdejonghe
 * 
 */
public class InternationalNumberFormatHandler extends AbstractFixedPhoneNumberHandler {

	private static final String INTERNATION_NUMBER_PREFIX = "+";

	public InternationalNumberFormatHandler() {
		super(TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.ISDN_TELEPHONE);
	}

	@Override
	public boolean supports(PhoneNumber phoneNumber) {
		return phoneNumber != null && phoneNumber.getNumber() != null && phoneNumber.getNumber().startsWith(INTERNATION_NUMBER_PREFIX);
	}
}