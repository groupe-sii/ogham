package fr.sii.ogham.testing.extension.junit;

import java.util.Optional;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.platform.commons.support.AnnotationSupport;

import fr.sii.ogham.testing.extension.common.LogTestInformation;
import fr.sii.ogham.testing.extension.common.Printer;
import fr.sii.ogham.testing.extension.common.TestInformationLogger;

/**
 * Write information about test. This is useful when there are many tests:
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
public class LoggingTestExtension implements BeforeEachCallback, AfterEachCallback, BeforeAllCallback {
	private TestInformationLogger logger;

	/**
	 * No logger initialized (it will be initialized using annotation). If you
	 * use {@link RegisterExtension} annotation, please use another constructor.
	 * 
	 */
	public LoggingTestExtension() {
		super();
	}

	/**
	 * Initializes with the provided max line length.
	 * 
	 * Uses Slf4j logger and default marker ("test-info").
	 * 
	 * @param maxLength
	 *            the length of each line
	 */
	public LoggingTestExtension(int maxLength) {
		super();
		this.logger = new TestInformationLogger(maxLength);
	}

	/**
	 * Initializes with the provided max line length and marker.
	 * 
	 * Uses Slf4j logger.
	 * 
	 * @param maxLength
	 *            the length of each line
	 * @param marker
	 *            the marker for logs
	 */
	public LoggingTestExtension(int maxLength, String marker) {
		super();
		this.logger = new TestInformationLogger(maxLength, marker);
	}

	/**
	 * 
	 * @param maxLength
	 *            the length of each line
	 * @param marker
	 *            the marker for logs
	 * @param logger
	 *            the logger
	 */
	public LoggingTestExtension(int maxLength, String marker, Printer logger) {
		super();
		this.logger = new TestInformationLogger(maxLength, marker, logger);
	}

	@Override
	public void beforeAll(ExtensionContext context) throws InstantiationException, IllegalAccessException {
		if (logger != null) {
			return;
		}
		LogTestInformation annotation = AnnotationSupport.findAnnotation(context.getElement(), LogTestInformation.class).orElse(null);
		if (annotation == null) {
			logger = new TestInformationLogger();
		} else {
			logger = new TestInformationLogger(annotation.maxLength(), annotation.marker(), annotation.printer().newInstance());
		}
	}

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		logger.writeStart(context.getDisplayName());
	}

	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		Optional<Throwable> executionException = context.getExecutionException();
		if (executionException.isPresent()) {
			logger.writeFailure(context.getDisplayName(), executionException.get());
		} else {
			logger.writeSuccess(context.getDisplayName());
		}
	}

}
