package fr.sii.ogham.testing.extension.common;

import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Use Slf4j logger to write the header, success and failure.
 * 
 * @author Aurélien Baudet
 *
 */
public class Slf4jPrinter implements Printer {
	private static final String MESSAGE = "\n{}\n\n";
	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger("");
	private Marker marker;

	@Override
	public void printHeader(String marker, String header) {
		if (LOG.isInfoEnabled()) {
			LOG.info(getMarker(marker), MESSAGE, header);
		}
	}

	@Override
	public void printSucess(String marker, String success) {
		if (LOG.isInfoEnabled()) {
			LOG.info(getMarker(marker), MESSAGE, success);
		}
	}

	@Override
	public void printFailure(String marker, String failure, Throwable e) {
		if (LOG.isErrorEnabled()) {
			LOG.error(getMarker(marker), "Test failure:\n", e);
			LOG.error(getMarker(marker), MESSAGE, failure);
		}
	}

	private Marker getMarker(String marker) {
		if (this.marker == null) {
			this.marker = MarkerFactory.getMarker(marker);
		}
		return this.marker;
	}
}
