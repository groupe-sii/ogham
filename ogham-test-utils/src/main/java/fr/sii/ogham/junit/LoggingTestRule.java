package fr.sii.ogham.junit;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

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
public class LoggingTestRule implements TestRule {
	private static final String SEPARATOR = ".";
	
	private final TestLogger logger;
	
	public LoggingTestRule(int maxLength) {
		super();
		this.logger = new TestLogger(maxLength);
	}

	public LoggingTestRule() {
		super();
		this.logger = new TestLogger();
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
			} catch(Throwable e) {		// NOSONAR
				logger.writeFailure(testName, e);
				throw e;
			}
		}
	}


}
