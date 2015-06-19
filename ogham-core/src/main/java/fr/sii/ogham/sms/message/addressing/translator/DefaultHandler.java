package fr.sii.ogham.sms.message.addressing.translator;

import fr.sii.ogham.sms.message.PhoneNumber;
import fr.sii.ogham.sms.message.addressing.NumberingPlanIndicator;
import fr.sii.ogham.sms.message.addressing.TypeOfNumber;

/**
 * Default handler. If we don't know anything else, TON is set to 0, and NPI is
 * set to 1.
 * 
 * @author cdejonghe
 * 
 */
public class DefaultHandler extends AbstractFixedPhoneNumberHandler {

	public DefaultHandler() {
		super(TypeOfNumber.UNKNOWN, NumberingPlanIndicator.ISDN_TELEPHONE);
	}

	@Override
	public boolean supports(PhoneNumber phoneNumber) {
		return true;
	}
}