package fr.sii.ogham.helper.rule;

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
	private static final String SEPARATOR = ".";
	
	private int maxLength;
	
	public LoggingTestRule(int maxLength) {
		super();
		this.maxLength = maxLength;
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
			String dashLine = StringUtils.repeat(DASH, maxLength-2);
			String header = "┌"+dashLine+"┐";
			String footer = "└"+dashLine+"┘";
			LOG.info(header);
			LOG.info("│{}│", format("Starting test "+testName));
			LOG.info(footer);
			try {
				base.evaluate();
				LOG.info(header);
				LOG.info("│{}│", format("Test "+testName+" successfully done"));
				LOG.info("{}\r\n\r\n", footer);
			} catch(Exception e) {
				LOG.error(header);
				LOG.error("│{}│", format("Test "+testName+" has failed"));
				LOG.error("│{}│", format("Cause: "+e));
				LOG.info("{}\r\n\r\n", footer);
				throw e;
			}
		}

		private String format(String text) {
			return StringUtils.center(StringUtils.abbreviate(text, maxLength-4), maxLength-2);
		}
	}


}
