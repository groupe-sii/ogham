package fr.sii.ogham.junit;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class LoggingTestRule implements TestRule {
	private static final int MAX_LENGTH = 100;
	private static final String SEPARATOR = ".";
	
	private final TestLogger logger;
	
	public LoggingTestRule(int maxLength) {
		super();
		this.logger = new TestLogger(maxLength);
	}

	public LoggingTestRule() {
		this(MAX_LENGTH);
	}


	@Override
	public Statement apply(final Statement base, final Description description) {
		return new LoggingStatement(description, base);
	}

	
	private final class LoggingStatement extends Statement {
		private final Description description;
		private final Statement base;

		private LoggingStatement(Description description, Statement base) {
			this.description = description;
			this.base = base;
		}

		@Override
		public void evaluate() throws Throwable {
			String testName = description.getTestClass().getSimpleName()+SEPARATOR+description.getMethodName();
			try {
				logger.writeStart(testName);
				base.evaluate();
				logger.writeSuccess(testName);
			} catch(Exception e) {		// NOSONAR
				logger.writeFailure(testName, e);
				throw e;
			}
		}
	}


}
