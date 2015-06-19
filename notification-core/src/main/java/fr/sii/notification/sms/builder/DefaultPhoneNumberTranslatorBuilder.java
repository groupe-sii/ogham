package fr.sii.notification.sms.builder;

import fr.sii.notification.core.exception.builder.BuildException;
import fr.sii.notification.sms.message.addressing.translator.AlphanumericCodeNumberFormatHandler;
import fr.sii.notification.sms.message.addressing.translator.CompositePhoneNumberTranslator;
import fr.sii.notification.sms.message.addressing.translator.DefaultHandler;
import fr.sii.notification.sms.message.addressing.translator.InternationalNumberFormatHandler;
import fr.sii.notification.sms.message.addressing.translator.PhoneNumberTranslator;
import fr.sii.notification.sms.message.addressing.translator.ShortCodeNumberFormatHandler;

/**
 * Builder to construct the {@link PhoneNumberTranslator}implementation in
 * charge of default addressing policy (TON / NPI).
 * 
 * @author cdejonghe
 * 
 */
// TODO For the moment each call will instantiate a new translator.
public class DefaultPhoneNumberTranslatorBuilder implements PhoneNumberTranslatorBuilder {
	private PhoneNumberTranslator translator;

	@Override
	public PhoneNumberTranslator build() throws BuildException {
		return translator;
	}

	@Override
	public PhoneNumberTranslatorBuilder useSenderDefaults() {
		translator = new CompositePhoneNumberTranslator(
				new AlphanumericCodeNumberFormatHandler(),
				new ShortCodeNumberFormatHandler(),
				new InternationalNumberFormatHandler(),
				new DefaultHandler());
		return this;
	}

	@Override
	public PhoneNumberTranslatorBuilder useRecipientDefaults() {
		translator = new CompositePhoneNumberTranslator(
				new InternationalNumberFormatHandler(),
				new DefaultHandler());
		return this;
	}

	@Override
	public PhoneNumberTranslatorBuilder useFallbackDefaults() {
		translator = new DefaultHandler();
		return this;
	}
}
