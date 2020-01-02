package fr.sii.ogham.testing.extension.common;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Simple logger that writes header, success and failure using a
 * {@link StringWriter}. The {@link StringWriter} is passed to the constructor
 * to ba able to read the written strings from the external.
 * 
 * <p>
 * This is mainly used for tests
 * 
 * @author Aurélien Baudet
 *
 */
public class StringPrinter implements Printer {
	private final StringWriter writer;
	private final boolean writeMarker;

	/**
	 * 
	 * @param writer
	 *            the writer that can be shared
	 * @param writeMarker
	 *            true to write marker
	 */
	public StringPrinter(StringWriter writer, boolean writeMarker) {
		super();
		this.writer = writer;
		this.writeMarker = writeMarker;
	}

	@Override
	public void printHeader(String marker, String header) {
		if (writeMarker) {
			writer.append("[marker:" + marker + "]\n");
		}
		writer.append(header + "\n\n");
	}

	@Override
	public void printSucess(String marker, String success) {
		if (writeMarker) {
			writer.append("[marker:" + marker + "]\n");
		}
		writer.append(success + "\n\n");
	}

	@Override
	public void printFailure(String marker, String failure, Throwable e) {
		if (writeMarker) {
			writer.append("[marker:" + marker + "]\n");
		}
		writer.append("Test failure:" + e + "\n");
		e.printStackTrace(new PrintWriter(writer));
		writer.append(failure + "\n\n");
	}

}
