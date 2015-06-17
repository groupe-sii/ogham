package fr.sii.notification.sms.builder;

import fr.sii.notification.core.builder.Builder;
import fr.sii.notification.sms.message.addressing.translator.PhoneNumberTranslator;

/**
 * Builder that helps to construct the {@link PhoneNumberTranslator}
 * implementation.
 * 
 * @author cdejonghe
 * 
 */
public interface PhoneNumberTranslatorBuilder extends Builder<PhoneNumberTranslator> {

	/**
	 * @return default recipient phone number translator
	 */
	PhoneNumberTranslatorBuilder useRecipientDefaults();

	/**
	 * @return default sender phone number translator
	 */
	PhoneNumberTranslatorBuilder useSenderDefaults();

	/**
	 * @see CloudhopperSMPPBuilder
	 * @return default fallback phone number translator
	 */
	PhoneNumberTranslatorBuilder useFallbackDefaults();

}
