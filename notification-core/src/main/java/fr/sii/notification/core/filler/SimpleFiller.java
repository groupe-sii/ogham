package fr.sii.notification.core.filler;

import java.util.HashMap;
import java.util.Map;

import fr.sii.notification.core.exception.BeanException;
import fr.sii.notification.core.exception.FillMessageException;
import fr.sii.notification.core.message.Message;
import fr.sii.notification.core.util.BeanUtils;

public class SimpleFiller implements MessageFiller {

	private Map<String, Object> values;
	
	public SimpleFiller(String key, Object value) {
		this(new HashMap<String, Object>());
		values.put(key, value);
	}
	
	public SimpleFiller(Map<String, Object> values) {
		super();
		this.values = values;
	}

	@Override
	public void fill(Message message) throws FillMessageException {
		try {
			BeanUtils.populate(message, values);
		} catch (BeanException e) {
			throw new FillMessageException("Failed to fill message with provided values", message, e);
		}
	}

}
