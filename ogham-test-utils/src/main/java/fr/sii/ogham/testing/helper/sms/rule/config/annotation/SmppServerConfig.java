package fr.sii.ogham.testing.helper.sms.rule.config.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import fr.sii.ogham.testing.helper.sms.rule.SmppServerRule;

/**
 * Configuration for local server used to simulate a SMPP server.
 * 
 * This annotation can be used on a test method to configure how the SMPP server
 * should behave.
 * 
 * This annotation is used by {@link SmppServerRule}s.
 * 
 * @author Aurélien Baudet
 *
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface SmppServerConfig {
	/**
	 * Control delays to simulate a slow server.
	 * 
	 * @return the delay configuration
	 */
	Slow slow() default @Slow();

	/**
	 * The allowed credentials
	 * 
	 * @return the allowed credentials
	 */
	Credentials[] credentials() default {};
}
