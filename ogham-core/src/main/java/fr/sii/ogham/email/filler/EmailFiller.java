package fr.sii.ogham.email.filler;

import static fr.sii.ogham.email.message.RecipientType.BCC;
import static fr.sii.ogham.email.message.RecipientType.CC;
import static fr.sii.ogham.email.message.RecipientType.TO;

import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.filler.AbstractMessageAwareFiller;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.message.Recipient;
import fr.sii.ogham.email.message.RecipientType;

public class EmailFiller extends AbstractMessageAwareFiller<Email> {

	public EmailFiller(PropertyResolver resolver, String baseKey) {
		super(resolver, baseKey, Email.class);
	}

	@Override
	protected void fill(Email email) {
		if(email.getSubject()==null && resolver.containsProperty(resolveKey("subject"))) {
			email.subject(resolver.getProperty(resolveKey("subject")));
		}
		if(email.getFrom()==null && resolver.containsProperty(resolveKey("from"))) {
			email.from(resolver.getProperty(resolveKey("from")));
		}
		if(!hasRecipient(email, TO) && resolver.containsProperty(resolveKey("to"))) {
			email.to(resolver.getProperty(resolveKey("to"), String[].class));
		}
		if(!hasRecipient(email, CC) && resolver.containsProperty(resolveKey("cc"))) {
			email.cc(resolver.getProperty(resolveKey("cc"), String[].class));
		}
		if(!hasRecipient(email, BCC) && resolver.containsProperty(resolveKey("bcc"))) {
			email.bcc(resolver.getProperty(resolveKey("bcc"), String[].class));
		}
	}
	
	private boolean hasRecipient(Email email, RecipientType type) {
		for(Recipient recipient : email.getRecipients()) {
			if(type.equals(recipient.getType())) {
				return true;
			}
		}
		return false;
	}
}
