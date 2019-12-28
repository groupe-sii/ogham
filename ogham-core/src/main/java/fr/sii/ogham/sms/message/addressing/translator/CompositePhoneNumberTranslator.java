package fr.sii.ogham.sms.message.addressing.translator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.sii.ogham.sms.exception.message.PhoneNumberTranslatorException;
import fr.sii.ogham.sms.message.PhoneNumber;
import fr.sii.ogham.sms.message.addressing.AddressedPhoneNumber;

/**
 * Composite phone number translator, delegating the translation to a list of
 * handlers to try. It will use the first handler supporting the phone number
 * format.
 * 
 * @author cdejonghe
 * 
 */
public class CompositePhoneNumberTranslator implements PhoneNumberTranslator {
	private final List<PhoneNumberHandler> handlerList = new ArrayList<>();

	/**
	 * Initializes the translator with the given handlers.
	 * 
	 * @param handlers
	 *            all the handlers to request when trying to translate a phone
	 *            number
	 */
	public CompositePhoneNumberTranslator(PhoneNumberHandler... handlers) {
		Collections.addAll(handlerList, handlers);
	}

	/**
	 * 
	 * @param handler
	 *            the handler to add
	 */
	public void add(PhoneNumberHandler handler) {
		handlerList.add(handler);
	}

	@Override
	public AddressedPhoneNumber translate(PhoneNumber phoneNumber) throws PhoneNumberTranslatorException {
		for (PhoneNumberHandler currentHandler : handlerList) {
			if (currentHandler.supports(phoneNumber)) {
				return currentHandler.translate(phoneNumber);
			}
		}
		throw new PhoneNumberTranslatorException("No handler referenced to translate the phone number : " + phoneNumber);
	}
}
