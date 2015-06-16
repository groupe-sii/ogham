package fr.sii.notification.sms.message.addressing.translator;

import fr.sii.notification.sms.message.PhoneNumber;
import fr.sii.notification.sms.message.addressing.AddressedPhoneNumber;
import fr.sii.notification.sms.message.addressing.NumberingPlanIndicator;
import fr.sii.notification.sms.message.addressing.TypeOfNumber;

/**
 * Extending this abstract class is an easy way to define the different
 * elements) {@link CompositePhoneNumberTranslator}.
 * 
 * @author cdejonghe
 * 
 */
public abstract class AbstractFixedPhoneNumberHandler implements PhoneNumberHandler {
	/** Delegate translator. */
	private final FixedPhoneNumberTranslator delegate;

	/**
	 * Initializes the translator with the given number, TON and NPI.
	 * 
	 * @param ton
	 * @param npi
	 */
	public AbstractFixedPhoneNumberHandler(TypeOfNumber ton, NumberingPlanIndicator npi) {
		delegate = new FixedPhoneNumberTranslator(ton, npi);
	}

	@Override
	public AddressedPhoneNumber translate(PhoneNumber phoneNumber)
			throws PhoneNumberTranslatorException {
		return delegate.translate(phoneNumber);
	}
}
