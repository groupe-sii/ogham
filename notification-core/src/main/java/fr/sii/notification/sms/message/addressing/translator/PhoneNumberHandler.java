package fr.sii.notification.sms.message.addressing.translator;

import fr.sii.notification.sms.message.PhoneNumber;

/**
 * 
 * @author cdejonghe
 * 
 */
public interface PhoneNumberHandler extends PhoneNumberTranslator {
	/**
	 * @return <code>true</code> if the hanlder knows how to translate the given
	 *         phone number
	 */
	boolean supports(PhoneNumber phoneNumber);
	
	
}
