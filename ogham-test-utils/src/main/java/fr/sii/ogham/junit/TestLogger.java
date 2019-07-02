package fr.sii.ogham.junit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestLogger {
	private static final Logger LOG = LoggerFactory.getLogger("");
	private static final String DASH = "─";

	private final int maxLength;
	
	public TestLogger(int maxLength) {
		super();
		this.maxLength = maxLength;
	}

	public void writeStart(String testName) {
		LOG.info(header());
		LOG.info("│{}│", format("Starting test "+testName));					// NOSONAR
		LOG.info(footer());
	}

	public void writeSuccess(String testName) {
		LOG.info(header());
		LOG.info("│{}│", format("Test "+testName+" successfully done"));	// NOSONAR
		LOG.info("{}\r\n\r\n", footer());
	}

	public void writeFailure(String testName, Throwable e) {
		LOG.error("");
		LOG.error("StackTrace:\r\n", e);
		LOG.error(header());
		LOG.error("│{}│", format("Test "+testName+" has failed"));
		LOG.error("│{}│", format("Cause: "+e));
		LOG.error("{}\r\n\r\n", footer());
	}

	private String dashLine() {
		return StringUtils.repeat(DASH, maxLength-2);
	}
	
	private String header() {
		return "┌"+dashLine()+"┐";
	}

	private String footer() {
		return "└"+dashLine()+"┘";
	}

	private String format(String text) {
		return StringUtils.center(StringUtils.abbreviate(text, maxLength-4), maxLength-2);
	}
}
