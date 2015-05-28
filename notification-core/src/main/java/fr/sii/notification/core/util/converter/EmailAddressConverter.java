package fr.sii.notification.core.util.converter;

import org.apache.commons.beanutils.converters.AbstractConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.notification.email.message.EmailAddress;

/**
 * Converts the provided object into an EmailAddress. This converter is used by
 * Apache Commons BeanUtils library.
 * 
 * @author Aurélien Baudet
 *
 */
public class EmailAddressConverter extends AbstractConverter {
	private static final Logger LOG = LoggerFactory.getLogger(EmailAddressConverter.class);
	
	@Override
	protected <T> T convertToType(Class<T> type, Object value) throws Throwable {
		LOG.debug("Converting string email address {} into EmailAddress", value);
		if (value instanceof String && EmailAddress.class.isAssignableFrom(type)) {
			return type.cast(new EmailAddress((String) value));
		}
		throw conversionException(type, value);
	}

	@Override
	protected Class<?> getDefaultType() {
		return EmailAddress.class;
	}

}
