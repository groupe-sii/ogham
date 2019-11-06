package fr.sii.ogham.junit;

import java.util.Optional;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class LoggingTestExtension implements BeforeEachCallback, AfterEachCallback {
	private final TestLogger logger;
	
	public LoggingTestExtension(int maxLength) {
		super();
		this.logger = new TestLogger(maxLength);
	}
	
	public LoggingTestExtension() {
		this(100);
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
