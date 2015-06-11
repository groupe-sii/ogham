package fr.sii.notification.core.util.converter;

import org.apache.commons.beanutils.converters.AbstractConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.notification.sms.message.Sender;

/**
 * Converts the provided object into an {@link SmsSenderConverter}. This
 * converter is used by Apache Commons BeanUtils library.
 * 
 * @author Aurélien Baudet
 *
 */
public class SmsSenderConverter extends AbstractConverter {
	private static final Logger LOG = LoggerFactory.getLogger(SmsSenderConverter.class);

	@Override
	protected <T> T convertToType(Class<T> type, Object value) throws Throwable {
		LOG.debug("Converting string sms sender {} into Sender", value);
		if (value instanceof String && Sender.class.isAssignableFrom(type)) {
			return type.cast(new Sender((String) value));
		}
		throw conversionException(type, value);
	}

	@Override
	protected Class<?> getDefaultType() {
		return Sender.class;
	}

}
