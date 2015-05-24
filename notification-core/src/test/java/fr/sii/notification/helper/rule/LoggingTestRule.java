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
				LOG.info("┌──────────────────────────{}─{}────────────┐", description.getClassName().replaceAll(".", "─"), description.getMethodName().replaceAll(".", "─"));
				LOG.info("│            Starting test {}.{}            │", description.getClassName(), description.getMethodName());
				LOG.info("└──────────────────────────{}─{}────────────┘", description.getClassName().replaceAll(".", "─"), description.getMethodName().replaceAll(".", "─"));
				try {
					base.evaluate();
					LOG.info("┌─────────────────{}─{}──────────────────────────────┐", description.getClassName().replaceAll(".", "─"), description.getMethodName().replaceAll(".", "─"));
					LOG.info("│            Test {}.{} successfully done            │", description.getClassName(), description.getMethodName());
					LOG.info("└─────────────────{}─{}──────────────────────────────┘\r\n\r\n", description.getClassName().replaceAll(".", "─"), description.getMethodName().replaceAll(".", "─"));
				} catch(Throwable e) {
					LOG.info("┌─────────────────{}─{}────────────────────{}────────────┐", description.getClassName().replaceAll(".", "─"), description.getMethodName().replaceAll(".", "─"), e.toString().replaceAll(".", "─"));
					LOG.info("│            Test {}.{} has failed. Cause: {}            │", description.getClassName(), description.getMethodName(), e);
					LOG.info("└─────────────────{}─{}────────────────────{}────────────┘\r\n\r\n", description.getClassName().replaceAll(".", "─"), description.getMethodName().replaceAll(".", "─"), e.toString().replaceAll(".", "─"));
					throw e;
				}
			}
		};
	}

}
