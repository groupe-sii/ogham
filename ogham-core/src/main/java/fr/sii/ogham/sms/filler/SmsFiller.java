package fr.sii.ogham.sms.filler;

import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.filler.AbstractMessageAwareFiller;
import fr.sii.ogham.sms.message.Sms;

public class SmsFiller extends AbstractMessageAwareFiller<Sms> {

	public SmsFiller(PropertyResolver resolver, String baseKey) {
		super(resolver, baseKey, Sms.class);
	}

	@Override
	protected void fill(Sms sms) {
		if(sms.getFrom()==null && resolver.containsProperty(resolveKey("from"))) {
			sms.from(resolver.getProperty(resolveKey("from")));
		}
		if(!hasRecipients(sms) && resolver.containsProperty(resolveKey("to"))) {
			sms.to(resolver.getProperty(resolveKey("to"), String[].class));
		}
	}
	
	private boolean hasRecipients(Sms sms) {
		return sms.getRecipients()!=null && !sms.getRecipients().isEmpty();
	}
}
