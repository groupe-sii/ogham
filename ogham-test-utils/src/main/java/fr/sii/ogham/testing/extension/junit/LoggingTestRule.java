package fr.sii.ogham.testing.extension.junit;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

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
public class LoggingTestRule extends TestWatcher {
	private static final String SEPARATOR = ".";

	private final TestInformationLogger logger;

	/**
	 * Initializes with 100 characters per line, slf4j marker and "test-info"
	 * marker.
	 * 
	 */
	public LoggingTestRule() {
		super();
		this.logger = new TestInformationLogger();
	}

	/**
	 * Initializes with the provided max line length.
	 * 
	 * Uses Slf4j logger and default marker ("test-info").
	 * 
	 * @param maxLength
	 *            the length of each line
	 */
	public LoggingTestRule(int maxLength) {
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
	public LoggingTestRule(int maxLength, String marker) {
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
	public LoggingTestRule(int maxLength, String marker, Printer logger) {
		super();
		this.logger = new TestInformationLogger(maxLength, marker, logger);
	}

	@Override
	protected void starting(Description description) {
		logger.writeStart(getTestName(description));
	}

	@Override
	protected void succeeded(Description description) {
		logger.writeSuccess(getTestName(description));
	}

	@Override
	protected void failed(Throwable e, Description description) {
		logger.writeFailure(getTestName(description), e);
	}

	private static String getTestName(Description description) {
		return description.getTestClass().getSimpleName() + SEPARATOR + description.getMethodName();
	}

}
