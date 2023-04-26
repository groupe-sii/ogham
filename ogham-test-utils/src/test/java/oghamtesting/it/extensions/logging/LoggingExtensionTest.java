package oghamtesting.it.extensions.logging;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;

import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.platform.testkit.engine.EngineTestKit;

import fr.sii.ogham.testing.extension.common.LogTestInformation;
import testutils.TestPrinterFactoryAdapter;

class LoggingExtensionTest {
	static final String SUCCESS_HEADER = 
			"╔══════════════════════════════════════════════════════════════════════════════════════════════════╗\n" + 
			"║success()                                                                                         ║\n" + 
			"╚══════════════════════════════════════════════════════════════════════════════════════════════════╝";
	static final String SUCCESS_FOOTER =
			"┌──────────────────────────────────────────────────────────────────────────────────────────────────┐\n" + 
			"│success()                                                                                         │\n" + 
			"├──────────────────────────────────────────────────────────────────────────────────────────────────┤\n" + 
			"│SUCCESS                                                                                           │\n" + 
			"└──────────────────────────────────────────────────────────────────────────────────────────────────┘";
	
	
	static final String FAILURE_HEADER = 
			"╔══════════════════════════════════════════════════════════════════════════════════════════════════╗\n" + 
			"║failure()                                                                                         ║\n" + 
			"╚══════════════════════════════════════════════════════════════════════════════════════════════════╝";
	static final String FAILURE_FOOTER =
			"┌──────────────────────────────────────────────────────────────────────────────────────────────────┐\n" + 
			"│failure()                                                                                         │\n" + 
			"├──────────────────────────────────────────────────────────────────────────────────────────────────┤\n" + 
			"│FAILED                                                                                            │\n" + 
			"├──────────────────────────────────────────────────────────────────────────────────────────────────┤\n" + 
			"│oghamtesting.it.extensions.logging.LoggingExtensionTest$FakeTest$CustomException: exception       │\n" + 
			"│message                                                                                           │\n" +
			"└──────────────────────────────────────────────────────────────────────────────────────────────────┘";
	
	
	static final String CAUGHT_HEADER = 
			"╔══════════════════════════════════════════════════════════════════════════════════════════════════╗\n" + 
			"║caught()                                                                                          ║\n" + 
			"╚══════════════════════════════════════════════════════════════════════════════════════════════════╝";
	static final String CAUGHT_FOOTER =
			"┌──────────────────────────────────────────────────────────────────────────────────────────────────┐\n" + 
			"│caught()                                                                                          │\n" + 
			"├──────────────────────────────────────────────────────────────────────────────────────────────────┤\n" + 
			"│SUCCESS                                                                                           │\n" + 
			"└──────────────────────────────────────────────────────────────────────────────────────────────────┘";

	
	
	static final String SUCCESS_HEADER_ASCII = 
			"+==================================================================================================+\n" + 
			"|success()                                                                                         |\n" + 
			"+==================================================================================================+";
	static final String SUCCESS_FOOTER_ASCII =
			"+--------------------------------------------------------------------------------------------------+\n" + 
			"|success()                                                                                         |\n" + 
			"+--------------------------------------------------------------------------------------------------+\n" + 
			"|SUCCESS                                                                                           |\n" + 
			"+--------------------------------------------------------------------------------------------------+";
	
	
	static final String FAILURE_HEADER_ASCII = 
			"+==================================================================================================+\n" + 
			"|failure()                                                                                         |\n" + 
			"+==================================================================================================+";
	static final String FAILURE_FOOTER_ASCII =
			"+--------------------------------------------------------------------------------------------------+\n" + 
			"|failure()                                                                                         |\n" + 
			"+--------------------------------------------------------------------------------------------------+\n" + 
			"|FAILED                                                                                            |\n" + 
			"+--------------------------------------------------------------------------------------------------+\n" + 
			"|oghamtesting.it.extensions.logging.LoggingExtensionTest$FakeTest$CustomException: exception       |\n" + 
			"|message                                                                                           |\n" +
			"+--------------------------------------------------------------------------------------------------+";
	
	
	static final String CAUGHT_HEADER_ASCII = 
			"+==================================================================================================+\n" + 
			"|caught()                                                                                          |\n" + 
			"+==================================================================================================+";
	static final String CAUGHT_FOOTER_ASCII =
			"+--------------------------------------------------------------------------------------------------+\n" + 
			"|caught()                                                                                          |\n" + 
			"+--------------------------------------------------------------------------------------------------+\n" + 
			"|SUCCESS                                                                                           |\n" + 
			"+--------------------------------------------------------------------------------------------------+";

	StringWriter writer;
	
	@BeforeEach
	void setup() {
		System.setProperty("execute-fake-test-for-testing-logging-extension", "true");
		writer = new StringWriter();
		TestPrinterFactoryAdapter.setWriter(writer);
	}
	
	@AfterEach
	void clean() {
		System.setProperty("execute-fake-test-for-testing-logging-extension", "");
		TestPrinterFactoryAdapter.reset();
	}
	
	@Test
	void checkSuccessLogs() {
		EngineTestKit.engine("junit-jupiter")
			.selectors(selectMethod(FakeTest.class, "success"))
			.execute()
				.testEvents()
					.assertStatistics(s -> s.aborted(0).failed(0).succeeded(1).skipped(0));
		String logs = writer.toString();
		assertThat(logs)
			.contains(isAscii() ? SUCCESS_HEADER_ASCII : SUCCESS_HEADER)
			.contains(isAscii() ? SUCCESS_FOOTER_ASCII : SUCCESS_FOOTER);
	}
	
	@Test
	void checkFailureLogs() {
		EngineTestKit.engine("junit-jupiter")
			.selectors(selectMethod(FakeTest.class, "failure"))
			.execute()
				.testEvents()
					.assertStatistics(s -> s.aborted(0).failed(1).succeeded(0).skipped(0));
		String logs = writer.toString();
		assertThat(logs)
			.contains(isAscii() ? FAILURE_HEADER_ASCII : FAILURE_HEADER)
			.contains(isAscii() ? FAILURE_FOOTER_ASCII : FAILURE_FOOTER);
	}
	
	@Test
	void checkCaughtLogs() {
		EngineTestKit.engine("junit-jupiter")
			.selectors(selectMethod(FakeTest.class, "caught"))
			.execute()
				.testEvents()
					.assertStatistics(s -> s.aborted(0).failed(0).succeeded(1).skipped(0));
		String logs = writer.toString();
		assertThat(logs)
			.contains(isAscii() ? CAUGHT_HEADER_ASCII : CAUGHT_HEADER)
			.contains(isAscii() ? CAUGHT_FOOTER_ASCII : CAUGHT_FOOTER);
	}
	
	@LogTestInformation(maxLength = 100, marker = "foo", printer = TestPrinterFactoryAdapter.class)
	@EnabledIfSystemProperty(named = "execute-fake-test-for-testing-logging-extension", matches = "true")
	static class FakeTest {
		
		@Test
		@SuppressWarnings("squid:S2699")
		void success() {
		}
		
		@Test
		@SuppressWarnings("squid:S2699")
		void failure() throws CustomException {
			throw new CustomException("exception message", new IllegalArgumentException("cause message"));
		}
		
		@Test
		void caught() throws CustomException {
			assertThrows(CustomException.class, () -> {
				throw new CustomException("exception message", new IllegalArgumentException("cause message"));
			}, "thrown");
		}
		
		@SuppressWarnings("serial")
		public static class CustomException extends Exception {
			public CustomException(String message, Throwable cause) {
				super(message, cause);
			}
		}
	}

	private static boolean isAscii() {
		return !Charset.defaultCharset().contains(StandardCharsets.UTF_8);
	}
}
