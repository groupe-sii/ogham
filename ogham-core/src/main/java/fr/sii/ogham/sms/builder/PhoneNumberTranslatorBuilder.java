package fr.sii.ogham.sms.builder;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.sms.message.addressing.translator.PhoneNumberTranslator;

/**
 * Builder that helps to construct the {@link PhoneNumberTranslator}
 * implementation.
 * 
 * @author cdejonghe
 * 
 */
public interface PhoneNumberTranslatorBuilder extends Builder<PhoneNumberTranslator> {

	/**
	 * Tells the builder to use default values and behaviors.
	 * 
	 * @return this instance for fluent use
	 */
	PhoneNumberTranslatorBuilder useDefaults();

}
