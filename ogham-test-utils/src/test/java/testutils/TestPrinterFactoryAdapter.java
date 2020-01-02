package testutils;

import java.io.StringWriter;

import fr.sii.ogham.testing.extension.common.Printer;
import fr.sii.ogham.testing.extension.common.Slf4jPrinter;
import fr.sii.ogham.testing.extension.common.StringPrinter;

public class TestPrinterFactoryAdapter implements Printer {
	private Printer stringPrinter;
	private Printer slf4j = new Slf4jPrinter();
	private static StringWriter currentWriter;
	
	public static void setWriter(StringWriter writer) {
		currentWriter = writer;
	}
	public static void reset() {
		currentWriter = null;
	}
	
	@Override
	public void printHeader(String marker, String header) {
		getStringPrinter().printHeader(marker, header);
		slf4j.printHeader(marker, header);
	}

	@Override
	public void printSucess(String marker, String success) {
		getStringPrinter().printSucess(marker, success);
		slf4j.printSucess(marker, success);
	}

	@Override
	public void printFailure(String marker, String failure, Throwable e) {
		getStringPrinter().printFailure(marker, failure, e);
		slf4j.printFailure(marker, failure, e);
	}

	private Printer getStringPrinter() {
		if (stringPrinter == null) {
			stringPrinter = new StringPrinter(getWriter(), true);
		}
		return stringPrinter;
	}
	private StringWriter getWriter() {
		if (currentWriter == null) {
			return new StringWriter();
		}
		return currentWriter;
	}

}
