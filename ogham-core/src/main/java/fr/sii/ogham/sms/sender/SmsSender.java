package fr.sii.ogham.sms.sender;

import fr.sii.ogham.core.sender.MultiImplementationSender;
import fr.sii.ogham.sms.message.Sms;

/**
 * Specialization of {@link MultiImplementationSender} for managing sms using
 * several implementations.
 * 
 * @author Aurélien Baudet
 *
 */
public class SmsSender extends MultiImplementationSender<Sms> {

	public SmsSender() {
		super();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SmsSender ").append(getImplementations());
		return builder.toString();
	}
}
