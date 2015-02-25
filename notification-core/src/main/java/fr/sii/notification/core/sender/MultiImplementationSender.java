package fr.sii.notification.core.sender;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import fr.sii.notification.core.condition.Condition;
import fr.sii.notification.core.exception.MessageException;
import fr.sii.notification.core.message.Message;

public class MultiImplementationSender<M extends Message> implements ConditionalSender {

	protected Map<Condition<Message>, NotificationSender> implementations;
	private NotificationSender sender;

	public MultiImplementationSender() {
		this(new HashMap<Condition<Message>, NotificationSender>());
	}
	
	public MultiImplementationSender(final Condition<Message> condition, final NotificationSender implementation) {
		this();
		addImplementation(condition, implementation);
	}

	public MultiImplementationSender(Map<Condition<Message>, NotificationSender> implementations) {
		super();
		this.implementations = implementations;
	}

	public MultiImplementationSender<M> addImplementation(Condition<Message> condition, NotificationSender implementation) {
		implementations.put(condition, implementation);
		return this;
	}

	@Override
	public boolean supports(Message message) {
		sender = null;
		boolean supports = message.getClass().isAssignableFrom(getManagedClass());
		if(supports) {
			for(Entry<Condition<Message>, NotificationSender> entry : implementations.entrySet()) {
				if(entry.getKey().accept(message)) {
					sender = entry.getValue();
					break;
				}
			}
		}
		return supports && sender!=null;
	}

	@SuppressWarnings("unchecked")
	private Class<M> getManagedClass() {
		Type genericSuperclass = getClass().getGenericSuperclass();
		if(genericSuperclass instanceof ParameterizedType) {
			return (Class<M>) ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
		}
		return null;
	}

	@Override
	public void send(Message message) throws MessageException {
		sender.send(message);
	}

}