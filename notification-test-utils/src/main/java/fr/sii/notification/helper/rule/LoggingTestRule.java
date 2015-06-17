package fr.sii.notification.helper.rule;

import org.apache.commons.lang3.StringUtils;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingTestRule implements TestRule {
	private static final Logger LOG = LoggerFactory.getLogger(LoggingTestRule.class);
	private static final int MAX_LENGTH = 100;
	private static final String DASH = "─";
	private static final String ANY = ".";

	@Override
	public Statement apply(final Statement base, final Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				String className = StringUtils.abbreviate(description.getClassName(), MAX_LENGTH);
				String descriptionDashes = className.replaceAll(ANY, DASH);
				String methodName = StringUtils.abbreviate(description.getMethodName(), MAX_LENGTH);
				String methodNameDashes = methodName.replaceAll(ANY, DASH);
				LOG.info("┌──────────────────────────{}─{}────────────┐", descriptionDashes, methodNameDashes);
				LOG.info("│            Starting test {}.{}            │", className, methodName);
				LOG.info("└──────────────────────────{}─{}────────────┘", descriptionDashes, methodNameDashes);
				try {
					base.evaluate();
					LOG.info("┌─────────────────{}─{}──────────────────────────────┐", descriptionDashes, methodNameDashes);
					LOG.info("│            Test {}.{} successfully done            │", className, methodName);
					LOG.info("└─────────────────{}─{}──────────────────────────────┘\r\n\r\n", descriptionDashes, methodNameDashes);
				} catch(Throwable e) {
					String errorMessage = StringUtils.abbreviate(e.toString(), MAX_LENGTH);
					String errorDashes = errorMessage.replaceAll(ANY, DASH);
					LOG.info("┌─────────────────{}─{}────────────────────{}────────────┐", descriptionDashes, methodNameDashes, errorDashes);
					LOG.info("│            Test {}.{} has failed. Cause: {}            │", className, methodName, errorMessage);
					LOG.info("└─────────────────{}─{}────────────────────{}────────────┘\r\n\r\n", descriptionDashes, methodNameDashes, errorDashes);
					throw e;
				}
			}
		};
	}

}
