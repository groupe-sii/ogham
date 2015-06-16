package fr.sii.notification.sms.builder;

import fr.sii.notification.core.builder.Builder;
import fr.sii.notification.sms.message.addressing.translator.PhoneNumberTranslator;

public interface PhoneNumberTranslatorBuilder extends Builder<PhoneNumberTranslator> {

	public abstract PhoneNumberTranslatorBuilder useReceiverDefaults();

	public abstract PhoneNumberTranslatorBuilder useSenderDefaults();

}
