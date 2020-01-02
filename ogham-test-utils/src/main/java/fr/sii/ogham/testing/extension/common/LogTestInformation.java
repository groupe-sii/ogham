package fr.sii.ogham.testing.extension.common;

import static fr.sii.ogham.testing.extension.common.TestInformationLogger.DEFAULT_MARKER;
import static fr.sii.ogham.testing.extension.common.TestInformationLogger.DEFAULT_MAX_LENGTH;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;
import org.spockframework.runtime.extension.ExtensionAnnotation;

import fr.sii.ogham.testing.extension.spock.LoggingTestExtension;

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
@ExtendWith(fr.sii.ogham.testing.extension.junit.LoggingTestExtension.class)
@ExtensionAnnotation(LoggingTestExtension.class)
public @interface LogTestInformation {
	/**
	 * The maximum length for each line.
	 * 
	 * @return the maximum length
	 */
	int maxLength() default DEFAULT_MAX_LENGTH;

	/**
	 * A custom log marker used to filter in logs.
	 * 
	 * @return the custom marker
	 */
	String marker() default DEFAULT_MARKER;

	/**
	 * The printer class to use. The class must have a default constructor.
	 * 
	 * @return the printer class to use
	 */
	Class<? extends Printer> printer() default Slf4jPrinter.class;
}
