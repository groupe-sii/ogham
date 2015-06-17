package fr.sii.notification.sms.message.addressing.translator;

import fr.sii.notification.sms.exception.message.PhoneNumberTranslatorException;
import fr.sii.notification.sms.message.PhoneNumber;
import fr.sii.notification.sms.message.addressing.AddressedPhoneNumber;

/**
 * Translates a given {@link PhoneNumber} to an {@link AddressedPhoneNumber},
 * deducing its TON and NPI.
 * 
 * @author cdejonghe
 * 
 */
public interface PhoneNumberTranslator {

	/**
	 * Translates a given {@link PhoneNumber} to an {@link AddressedPhoneNumber}
	 * , deducing its TON and NPI.
	 * 
	 * @param phoneNumber
	 *            the phone number to translate
	 * @return the corresponding number + adressing information
	 * @throws PhoneNumberTranslatorException
	 *             when the translation has failed
	 */
	AddressedPhoneNumber translate(PhoneNumber phoneNumber) throws PhoneNumberTranslatorException;
}
