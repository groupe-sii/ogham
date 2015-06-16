package fr.sii.notification.sms.message.addressing.translator;

import java.util.ArrayList;
import java.util.List;

import fr.sii.notification.sms.message.PhoneNumber;
import fr.sii.notification.sms.message.addressing.AddressedPhoneNumber;

/**
 * 
 * @author cdejonghe
 * 
 */
public class CompositePhoneNumberTranslator implements PhoneNumberTranslator {
	private final List<PhoneNumberHandler> handlerList = new ArrayList<PhoneNumberHandler>();
	
	/**
	 * Initializes the translator with the given handlers.
	 * 
	 * @param handlers
	 *            all the handlers to request whe trying to translate a phone
	 *            number
	 */
	public CompositePhoneNumberTranslator(PhoneNumberHandler... handlers) {
		for (int i = 0; i < handlers.length; i++) {
			handlerList.add(handlers[i]);
		}
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
	public AddressedPhoneNumber translate(PhoneNumber phoneNumber)
			throws PhoneNumberTranslatorException {
		for (PhoneNumberHandler currentHandler : handlerList) {
			if (currentHandler.supports(phoneNumber)) {
				return currentHandler.translate(phoneNumber);
			}
		}
		throw new PhoneNumberTranslatorException("No handler referenced to translate the phone number : " + phoneNumber);
	}
}
