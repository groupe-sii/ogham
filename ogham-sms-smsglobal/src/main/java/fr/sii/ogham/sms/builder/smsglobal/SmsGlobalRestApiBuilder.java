package fr.sii.ogham.sms.builder.smsglobal;

import fr.sii.ogham.core.fluent.Parent;

public interface SmsGlobalRestApiBuilder extends Parent<SmsGlobalBuilder> {
	SmsGlobalRestApiBuilder apiKey(String key);
}
