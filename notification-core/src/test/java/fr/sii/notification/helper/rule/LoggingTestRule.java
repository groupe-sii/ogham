package fr.sii.notification.helper.rule;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingTestRule implements TestRule {
	private static final Logger LOG = LoggerFactory.getLogger(LoggingTestRule.class);

	@Override
	public Statement apply(final Statement base, final Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				LOG.info("Starting test {}.{}", description.getClassName(), description.getMethodName());
				try {
					base.evaluate();
				} catch(Throwable e) {
					LOG.info("Test {}.{} has failed. Cause: {}", description.getClassName(), description.getMethodName(), e);
				} finally {
					LOG.info("Test {}.{} successfully done", description.getClassName(), description.getMethodName());
				}
			}
		};
	}

}
