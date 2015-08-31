package fr.sii.ogham.sms.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.sms.message.addressing.translator.AlphanumericCodeNumberFormatHandler;
import fr.sii.ogham.sms.message.addressing.translator.CompositePhoneNumberTranslator;
import fr.sii.ogham.sms.message.addressing.translator.DefaultHandler;
import fr.sii.ogham.sms.message.addressing.translator.InternationalNumberFormatHandler;
import fr.sii.ogham.sms.message.addressing.translator.PhoneNumberTranslator;
import fr.sii.ogham.sms.message.addressing.translator.ShortCodeNumberFormatHandler;

/**
 * Builder to construct the {@link PhoneNumberTranslator} implementation in
 * charge of default addressing policy (TON / NPI).
 * 
 * @author cdejonghe
 * 
 */
public class SenderPhoneNumberTranslatorBuilder implements PhoneNumberTranslatorBuilder {
	private static final Logger LOG = LoggerFactory.getLogger(SenderPhoneNumberTranslatorBuilder.class);
	
	/**
	 * Enable alpha numeric code format (see {@link AlphanumericCodeNumberFormatHandler})
	 */
	private boolean enableAlphanum;
	
	/**
	 * Enable short code format (see {@link ShortCodeNumberFormatHandler})
	 */
	private boolean enableShortCode;
	
	/**
	 * Enable short code format (see {@link InternationalNumberFormatHandler})
	 */
	private boolean enableInternational;

	@Override
	public PhoneNumberTranslator build() throws BuildException {
		CompositePhoneNumberTranslator translator = new CompositePhoneNumberTranslator();
		if(enableAlphanum) {
			LOG.debug("Enable phone number with alpha numeric code format");
			translator.add(new AlphanumericCodeNumberFormatHandler());
		}
		if(enableShortCode) {
			LOG.debug("Enable phone number with short code format (less than 5 digits)");
			translator.add(new ShortCodeNumberFormatHandler());
		}
		if(enableInternational) {
			LOG.debug("Enable international phone number format");
			translator.add(new InternationalNumberFormatHandler());
		}
		translator.add(new DefaultHandler());
		return translator;
	}

	@Override
	public PhoneNumberTranslatorBuilder useDefaults() {
		enableAlphanumericCodeFormat();
		enableShortCodeFormat();
		enableInternationalNumberFormat();
		return this;
	}
	
	public PhoneNumberTranslatorBuilder enableAlphanumericCodeFormat() {
		this.enableAlphanum = true;
		return this;
	}

	public PhoneNumberTranslatorBuilder enableShortCodeFormat() {
		this.enableShortCode = true;
		return this;
	}
	
	public PhoneNumberTranslatorBuilder enableInternationalNumberFormat() {
		this.enableInternational = true;
		return this;
	}
}
