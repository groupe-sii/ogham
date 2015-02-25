package fr.sii.notification.core.converter;

import org.apache.commons.beanutils.converters.AbstractConverter;

import fr.sii.notification.email.message.EmailAddress;

public class EmailAddressConverter extends AbstractConverter {
	@Override
	protected <T> T convertToType(Class<T> type, Object value) throws Throwable {
		if(value instanceof String && EmailAddress.class.isAssignableFrom(type)) {
			return type.cast(new EmailAddress((String) value));
		}
		throw conversionException(type, value);
	}

	@Override
	protected Class<?> getDefaultType() {
		return EmailAddress.class;
	}

}
