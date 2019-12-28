package fr.sii.ogham.junit;

import java.util.Optional;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import fr.sii.ogham.common.TestLogger;

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
public class LoggingTestExtension implements BeforeEachCallback, AfterEachCallback {
	private final TestLogger logger;
	
	public LoggingTestExtension(int maxLength) {
		super();
		this.logger = new TestLogger(maxLength);
	}
	
	public LoggingTestExtension() {
		super();
		this.logger = new TestLogger();
	}

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		logger.writeStart(context.getDisplayName());
	}

	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		Optional<Throwable> executionException = context.getExecutionException();
		if(executionException.isPresent()) {
			logger.writeFailure(context.getDisplayName(), executionException.get());
		} else {
			logger.writeSuccess(context.getDisplayName());
		}
	}

}
