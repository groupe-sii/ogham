package fr.sii.ogham.sms.filler;

import java.util.Map;

import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.filler.AbstractMessageAwareFiller;
import fr.sii.ogham.sms.message.Sms;

public class SmsFiller extends AbstractMessageAwareFiller<Sms> {

	public SmsFiller(Map<String, ConfigurationValueBuilderHelper<?, ?>> defaultValues) {
		super(defaultValues, Sms.class);
	}

	@Override
	protected void fill(Sms sms) {
		if(sms.getFrom()==null && containsProperty("from")) {
			sms.from(getProperty("from", String.class));
		}
		if(!hasRecipients(sms) && containsProperty("to")) {
			sms.to(getProperty("to", String[].class));
		}
	}
	
	private static boolean hasRecipients(Sms sms) {
		return sms.getRecipients()!=null && !sms.getRecipients().isEmpty();
	}
}
