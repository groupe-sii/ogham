package fr.sii.ogham.sms.sender;

import java.util.List;

import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.sender.MessageSender;
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

	public SmsSender(Condition<Message> condition, MessageSender implementation) {
		super(condition, implementation);
	}

	public SmsSender(List<Implementation> implementations) {
		super(implementations);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SmsSender ").append(getImplementations());
		return builder.toString();
	}
}
