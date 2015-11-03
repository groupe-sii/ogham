package fr.sii.ogham.sms.filler;

import java.util.List;
import java.util.Map;

import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.filler.AbstractMessageAwareFiller;
import fr.sii.ogham.sms.message.Sms;

public class SmsFiller extends AbstractMessageAwareFiller<Sms> {

	public SmsFiller(PropertyResolver resolver, Map<String, List<String>> keys) {
		super(resolver, keys, Sms.class);
	}

	@Override
	protected void fill(Sms sms) {
		if(sms.getFrom()==null && containsProperty("from")) {
			sms.from(getProperty("from"));
		}
		if(!hasRecipients(sms) && containsProperty("to")) {
			sms.to(getProperty("to", String[].class));
		}
	}
	
	private boolean hasRecipients(Sms sms) {
		return sms.getRecipients()!=null && !sms.getRecipients().isEmpty();
	}
}
