package fr.sii.ogham.sms.filler;

import java.util.Map;

import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.filler.AbstractMessageAwareFiller;
import fr.sii.ogham.sms.message.Sms;

public class SmsFiller extends AbstractMessageAwareFiller<Sms> {

	public SmsFiller(PropertyResolver resolver, Map<String, ConfigurationValueBuilderHelper<?, String>> defaultValues) {
		super(resolver, defaultValues, Sms.class);
	}

	@Override
	protected void fill(Sms sms) {
		if(sms.getFrom()==null && containsProperty("from")) {
			sms.from(getProperty("from"));
		}
		if(!hasRecipients(sms) && containsProperty("to")) {
			sms.to(getPropertyArray("to"));
		}
	}
	
	private static boolean hasRecipients(Sms sms) {
		return sms.getRecipients()!=null && !sms.getRecipients().isEmpty();
	}
}
