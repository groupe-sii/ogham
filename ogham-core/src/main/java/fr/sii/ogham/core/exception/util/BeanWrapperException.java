package fr.sii.ogham.core.exception.util;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingRuntimeException;
import fr.sii.ogham.core.util.bean.BeanWrapperUtils;

/**
 * General exception that is thrown when trying to wrap a bean for accessing its
 * properties.
 * 
 * @author Aurélien Baudet
 *
 * @see BeanWrapperUtils
 */
public class BeanWrapperException extends MessagingRuntimeException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public BeanWrapperException(String message, Throwable cause) {
		super(message, cause);
	}

	public BeanWrapperException(String message) {
		super(message);
	}
}
