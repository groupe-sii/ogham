package fr.sii.ogham.sms.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.sms.message.addressing.translator.CompositePhoneNumberTranslator;
import fr.sii.ogham.sms.message.addressing.translator.DefaultHandler;
import fr.sii.ogham.sms.message.addressing.translator.InternationalNumberFormatHandler;
import fr.sii.ogham.sms.message.addressing.translator.PhoneNumberTranslator;

/**
 * Builder to construct the {@link PhoneNumberTranslator} implementation in
 * charge of default addressing policy (TON / NPI).
 * 
 * @author cdejonghe
 * 
 */
public class RecipientPhoneNumberTranslatorBuilder implements PhoneNumberTranslatorBuilder {
	private static final Logger LOG = LoggerFactory.getLogger(RecipientPhoneNumberTranslatorBuilder.class);
	
	/**
	 * Enable short code format (see {@link InternationalNumberFormatHandler})
	 */
	private boolean enableInternational;

	@Override
	public PhoneNumberTranslator build() throws BuildException {
		CompositePhoneNumberTranslator translator = new CompositePhoneNumberTranslator();
		if(enableInternational) {
			LOG.debug("Enable international phone number format");
			translator.add(new InternationalNumberFormatHandler());
		}
		translator.add(new DefaultHandler());
		return translator;
	}

	@Override
	public PhoneNumberTranslatorBuilder useDefaults() {
		enableInternationalNumberFormat();
		return this;
	}
	
	public PhoneNumberTranslatorBuilder enableInternationalNumberFormat() {
		this.enableInternational = true;
		return this;
	}

}
