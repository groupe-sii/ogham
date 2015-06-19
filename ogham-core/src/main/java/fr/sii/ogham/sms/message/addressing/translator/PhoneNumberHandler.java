package fr.sii.ogham.sms.message.addressing.translator;

import fr.sii.ogham.sms.message.PhoneNumber;

/**
 * Dedicated phone number translator exposing its capabilities.
 * 
 * @author cdejonghe
 * 
 */
public interface PhoneNumberHandler extends PhoneNumberTranslator {
	/**
	 * @return <code>true</code> if the handler knows how to deduce TON and NPI
	 *         of the given phone number
	 */
	boolean supports(PhoneNumber phoneNumber);

}
