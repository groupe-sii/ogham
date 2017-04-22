package fr.sii.ogham.core.filler;

import java.util.List;
import java.util.Properties;

import fr.sii.ogham.core.exception.filler.FillMessageException;
import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.message.capability.HasToFluent;
import fr.sii.ogham.email.message.Email;

public class MessageAwareFiller implements MessageFiller {
	private Properties properties;
	private String baseKey;


	public MessageAwareFiller(Properties properties, String baseKey) {
		super();
		this.properties = properties;
		this.baseKey = baseKey;
	}


	@Override
	public void fill(Message message) throws FillMessageException {
		if(message instanceof HasToFluent && hasValue("to")) {
			((HasToFluent<?>) message).to(getValue("to"));
		}
		// TODO: handle list of values + conversions
		if(message instanceof Email && hasValue("cc")) {
			((Email) message).cc(getValue("cc"));
		}
		if(message instanceof Email && hasValue("bcc")) {
			((Email) message).bcc(getValue("bcc"));
		}
	}

	private boolean hasValue(String property) {
		return properties.get(baseKey+"."+property)!=null;
	}
	
	private String[] getValue(String property) {
		return split(properties.get(baseKey+"."+property));
	}
	
	private String[] split(Object value) {
		return value.toString().split(",\\s*");
	}
}
