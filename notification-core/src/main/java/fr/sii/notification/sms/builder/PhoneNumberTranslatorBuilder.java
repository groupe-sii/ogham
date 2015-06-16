package fr.sii.notification.sms.builder;

import fr.sii.notification.core.builder.Builder;
import fr.sii.notification.core.exception.builder.BuildException;
import fr.sii.notification.sms.message.addressing.translator.AlphanumericCodeNumberFormatHandler;
import fr.sii.notification.sms.message.addressing.translator.CompositePhoneNumberTranslator;
import fr.sii.notification.sms.message.addressing.translator.DefaultHandler;
import fr.sii.notification.sms.message.addressing.translator.InternationalNumberFormatHandler;
import fr.sii.notification.sms.message.addressing.translator.PhoneNumberTranslator;
import fr.sii.notification.sms.message.addressing.translator.ShortCodeNumberFormatHandler;

/**
 * 
 * @author cdejonghe
 * 
 */
public class PhoneNumberTranslatorBuilder implements Builder<PhoneNumberTranslator> {
	private PhoneNumberTranslator translator;

	@Override
	public PhoneNumberTranslator build() throws BuildException {
		return translator;
	}

	public PhoneNumberTranslatorBuilder useDefaults() {
		translator = new CompositePhoneNumberTranslator(
				new AlphanumericCodeNumberFormatHandler(),
				new ShortCodeNumberFormatHandler(),
				new InternationalNumberFormatHandler(),
				new DefaultHandler());
		return this;
	}
}
