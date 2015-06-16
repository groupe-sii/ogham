package fr.sii.notification.sms.message.addressing.translator;

import fr.sii.notification.sms.message.PhoneNumber;
import fr.sii.notification.sms.message.addressing.AddressedPhoneNumber;
import fr.sii.notification.sms.message.addressing.NumberingPlanIndicator;
import fr.sii.notification.sms.message.addressing.TypeOfNumber;

/**
 * Addressing default strategy : TON and NPI will be fixed by the translator
 * 
 * @author cdejonghe
 * 
 */
public class FixedPhoneNumberTranslator implements PhoneNumberTranslator {
	private final TypeOfNumber ton;
	private final NumberingPlanIndicator npi;

	/**
	 * Initializes the translator with the given number, TON and NPI.
	 * 
	 * @param ton
	 * @param npi
	 */
	public FixedPhoneNumberTranslator(TypeOfNumber ton, NumberingPlanIndicator npi) {
		super();
		this.ton = ton;
		this.npi = npi;
	}

	@Override
	public AddressedPhoneNumber translate(PhoneNumber phoneNumber) {
		return new AddressedPhoneNumber(phoneNumber.getNumber(), ton, npi);
	}

}
