package fr.sii.ogham.spock;

import static fr.sii.ogham.common.TestLogger.DEFAULT_MAX_LENGTH;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.spockframework.runtime.extension.ExtensionAnnotation;

/**
 * Sock extension to write information about test. This is useful when there are
 * many tests:
 * <ul>
 * <li>To quickly find the logs for the test</li>
 * <li>To quickly know if the test has failed or succeeded</li>
 * <li>To quickly identify the test failure</li>
 * <li>To quickly find failed tests</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
@Documented
@Retention(RUNTIME)
@Target({ METHOD, TYPE })
@ExtensionAnnotation(LoggingTestExtension.class)
public @interface LoggingTest {
	/**
	 * The maximum length for each line.
	 * 
	 * @return the maximum length
	 */
	int maxLength() default DEFAULT_MAX_LENGTH;
}
