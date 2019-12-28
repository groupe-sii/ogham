package fr.sii.ogham.email.filler;

import static fr.sii.ogham.email.message.RecipientType.BCC;
import static fr.sii.ogham.email.message.RecipientType.CC;
import static fr.sii.ogham.email.message.RecipientType.TO;

import java.util.Map;

import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.filler.AbstractMessageAwareFiller;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.message.Recipient;
import fr.sii.ogham.email.message.RecipientType;

public class EmailFiller extends AbstractMessageAwareFiller<Email> {

	public EmailFiller(PropertyResolver resolver, Map<String, ConfigurationValueBuilderHelper<?, String>> defaultValues) {
		super(resolver, defaultValues, Email.class);
	}

	@Override
	protected void fill(Email email) {
		if(email.getSubject()==null && containsProperty("subject")) {
			email.subject(getProperty("subject"));
		}
		if(email.getFrom()==null && containsProperty("from")) {
			email.from(getProperty("from"));
		}
		if(!hasRecipient(email, TO) && containsProperty("to")) {
			email.to(getPropertyArray("to"));
		}
		if(!hasRecipient(email, CC) && containsProperty("cc")) {
			email.cc(getPropertyArray("cc"));
		}
		if(!hasRecipient(email, BCC) && containsProperty("bcc")) {
			email.bcc(getPropertyArray("bcc"));
		}
	}
	
	private static boolean hasRecipient(Email email, RecipientType type) {
		for(Recipient recipient : email.getRecipients()) {
			if(type == recipient.getType()) {
				return true;
			}
		}
		return false;
	}
}
