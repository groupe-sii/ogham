package fr.sii.notification.core.filler;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.notification.core.exception.filler.FillMessageException;
import fr.sii.notification.core.exception.template.BeanException;
import fr.sii.notification.core.message.Message;
import fr.sii.notification.core.util.BeanUtils;

/**
 * A simple filler that add values on the message. It adds all values defined in
 * a map into the message. The keys can use dot character '.' to set nested
 * properties. The map is indexed by the name of the property of the message to
 * set.
 * 
 * @author Aurélien Baudet
 *
 */
public class SimpleFiller implements MessageFiller {
	private static final Logger LOG = LoggerFactory.getLogger(SimpleFiller.class);

	/**
	 * The map that contains the values to set. The map is indexed by the name
	 * of the property of the message to set.
	 */
	private Map<String, Object> values;

	/**
	 * Initialize the filler with a single property with the value to set.
	 * 
	 * @param key
	 *            the name of the property to set on the message
	 * @param value
	 *            the value to set
	 */
	public SimpleFiller(String key, Object value) {
		this(new HashMap<String, Object>());
		values.put(key, value);
	}

	/**
	 * Initialize the filler with several pairs of property/value to set.
	 * 
	 * @param values
	 *            the map of property/value to set on the message
	 */
	public SimpleFiller(Map<String, Object> values) {
		super();
		this.values = values;
	}

	@Override
	public void fill(Message message) throws FillMessageException {
		try {
			LOG.debug("Filling message {} with map {}", message, values);
			BeanUtils.populate(message, values);
		} catch (BeanException e) {
			throw new FillMessageException("Failed to fill message with provided values", message, e);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SimpleFiller ").append(values);
		return builder.toString();
	}

	public Map<String, Object> getValues() {
		return values;
	}
}
