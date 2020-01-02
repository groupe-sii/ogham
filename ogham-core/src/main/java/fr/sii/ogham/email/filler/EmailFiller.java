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

	public EmailFiller(PropertyResolver resolver, Map<String, ConfigurationValueBuilderHelper<?, ?>> defaultValues) {
		super(resolver, defaultValues, Email.class);
	}

	@Override
	protected void fill(Email email) {
		if(email.getSubject()==null && containsProperty("subject")) {
			email.subject(getProperty("subject", String.class));
		}
		if(email.getFrom()==null && containsProperty("from")) {
			email.from(getProperty("from", String.class));
		}
		if(!hasRecipient(email, TO) && containsProperty("to")) {
			email.to(getProperty("to", String[].class));
		}
		if(!hasRecipient(email, CC) && containsProperty("cc")) {
			email.cc(getProperty("cc", String[].class));
		}
		if(!hasRecipient(email, BCC) && containsProperty("bcc")) {
			email.bcc(getProperty("bcc", String[].class));
		}
	}
	
	private static boolean hasRecipient(Email email, RecipientType type) {
		if (email.getRecipients() == null) {
			return false;
		}
		for(Recipient recipient : email.getRecipients()) {
			if(type == recipient.getType()) {
				return true;
			}
		}
		return false;
	}
}
