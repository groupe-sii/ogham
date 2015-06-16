package fr.sii.notification.sms.builder;

import fr.sii.notification.core.exception.builder.BuildException;
import fr.sii.notification.sms.message.addressing.translator.AlphanumericCodeNumberFormatHandler;
import fr.sii.notification.sms.message.addressing.translator.CompositePhoneNumberTranslator;
import fr.sii.notification.sms.message.addressing.translator.DefaultHandler;
import fr.sii.notification.sms.message.addressing.translator.InternationalNumberFormatHandler;
import fr.sii.notification.sms.message.addressing.translator.PhoneNumberTranslator;
import fr.sii.notification.sms.message.addressing.translator.ShortCodeNumberFormatHandler;

/**
 * Builder that helps to construct the {@link PhoneNumberTranslator}
 * implementation.
 * 
 * @author cdejonghe
 * 
 */
public class DefaultPhoneNumberTranslatorBuilder implements PhoneNumberTranslatorBuilder {
	private PhoneNumberTranslator translator;

	@Override
	public PhoneNumberTranslator build() throws BuildException {
		return translator;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public PhoneNumberTranslatorBuilder useSenderDefaults() {
		translator = new CompositePhoneNumberTranslator(
				new AlphanumericCodeNumberFormatHandler(),
				new ShortCodeNumberFormatHandler(),
				new InternationalNumberFormatHandler(),
				new DefaultHandler());
		return this;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public PhoneNumberTranslatorBuilder useReceiverDefaults() {
		translator = new CompositePhoneNumberTranslator(
				new InternationalNumberFormatHandler(),
				new DefaultHandler());
		return this;
	}
}
