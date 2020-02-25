package fr.sii.ogham.sms.builder.smsglobal;

import fr.sii.ogham.core.fluent.Parent;
import fr.sii.ogham.sms.builder.SmsBuilder;

public interface SmsGlobalBuilder extends Parent<SmsBuilder> {
	SmsGlobalRestApiBuilder rest();
}
