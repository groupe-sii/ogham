package fr.sii.ogham.testing.extension.junit.sms.config;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

/**
 * Allowed credentials for SMPP server.
 * 
 * @author Aurélien Baudet
 *
 */
@Documented
@Retention(RUNTIME)
public @interface Credentials {
	/**
	 * The system_id
	 * 
	 * @return the sytem_id
	 */
	String systemId();

	/**
	 * The password
	 * 
	 * @return the password
	 */
	String password();
}
