package oghamtesting.it.extensions.logging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;

import java.io.StringWriter;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.platform.testkit.engine.EngineTestKit;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;

import fr.sii.ogham.testing.extension.common.LogTestInformation;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import testutils.TestPrinterFactoryAdapter;

public class LoggingRuleTest {
	static final String SUCCESS_HEADER = 
			"╔══════════════════════════════════════════════════════════════════════════════════════════════════╗\n" + 
			"║FakeTest.success                                                                                  ║\n" + 
			"╚══════════════════════════════════════════════════════════════════════════════════════════════════╝";
	static final String SUCCESS_FOOTER =
			"┌──────────────────────────────────────────────────────────────────────────────────────────────────┐\n" + 
			"│FakeTest.success                                                                                  │\n" + 
			"├──────────────────────────────────────────────────────────────────────────────────────────────────┤\n" + 
			"│SUCCESS                                                                                           │\n" + 
			"└──────────────────────────────────────────────────────────────────────────────────────────────────┘";
	
	
	static final String FAILURE_HEADER = 
			"╔══════════════════════════════════════════════════════════════════════════════════════════════════╗\n" + 
			"║FakeTest.failure                                                                                  ║\n" + 
			"╚══════════════════════════════════════════════════════════════════════════════════════════════════╝";
	static final String FAILURE_FOOTER =
			"┌──────────────────────────────────────────────────────────────────────────────────────────────────┐\n" + 
			"│FakeTest.failure                                                                                  │\n" + 
			"├──────────────────────────────────────────────────────────────────────────────────────────────────┤\n" + 
			"│FAILED                                                                                            │\n" + 
			"├──────────────────────────────────────────────────────────────────────────────────────────────────┤\n" + 
			"│oghamtesting.it.extensions.logging.LoggingRuleTest$CustomException: exception message             │\n" + 
			"└──────────────────────────────────────────────────────────────────────────────────────────────────┘";
	

	static final String CAUGHT_HEADER = 
			"╔══════════════════════════════════════════════════════════════════════════════════════════════════╗\n" + 
			"║FakeTest.caught                                                                                   ║\n" + 
			"╚══════════════════════════════════════════════════════════════════════════════════════════════════╝";
	static final String CAUGHT_FOOTER =
			"┌──────────────────────────────────────────────────────────────────────────────────────────────────┐\n" + 
			"│FakeTest.caught                                                                                   │\n" + 
			"├──────────────────────────────────────────────────────────────────────────────────────────────────┤\n" + 
			"│SUCCESS                                                                                           │\n" + 
			"└──────────────────────────────────────────────────────────────────────────────────────────────────┘";

	
	static final String CAUGHT_ANNOTATION_HEADER = 
			"╔══════════════════════════════════════════════════════════════════════════════════════════════════╗\n" + 
			"║FakeTest.caughtByAnnotation                                                                       ║\n" + 
			"╚══════════════════════════════════════════════════════════════════════════════════════════════════╝";
	static final String CAUGHT_ANNOTATION_FOOTER =
			"┌──────────────────────────────────────────────────────────────────────────────────────────────────┐\n" + 
			"│FakeTest.caughtByAnnotation                                                                       │\n" + 
			"├──────────────────────────────────────────────────────────────────────────────────────────────────┤\n" + 
			"│SUCCESS                                                                                           │\n" + 
			"└──────────────────────────────────────────────────────────────────────────────────────────────────┘";
	
	public static class UsingJunitRuleAnnotation {
		StringWriter writer;
		
		@Before
		public void setup() {
			System.setProperty("execute-fake-test-for-testing-logging-extension", "true");
			writer = new StringWriter();
			TestPrinterFactoryAdapter.setWriter(writer);
		}
		
		@After
		public void clean() {
			System.setProperty("execute-fake-test-for-testing-logging-extension", "");
			TestPrinterFactoryAdapter.reset();
		}
		
		@Test
		public void checkSuccessLogs() {
			EngineTestKit.engine("junit-vintage")
				.selectors(selectMethod(FakeTest.class, "success"))
				.execute()
					.testEvents()
						.assertStatistics(s -> s.aborted(0).failed(0).succeeded(1).skipped(0));
			String logs = writer.toString();
			assertThat(logs)
				.contains(SUCCESS_HEADER)
				.contains(SUCCESS_FOOTER);
		}
		
		@Test
		public void checkFailureLogs() {
			EngineTestKit.engine("junit-vintage")
				.selectors(selectMethod(FakeTest.class, "failure"))
				.execute()
					.testEvents()
						.assertStatistics(s -> s.aborted(0).failed(1).succeeded(0).skipped(0));
			String logs = writer.toString();
			assertThat(logs)
				.contains(FAILURE_HEADER)
				.contains(FAILURE_FOOTER);
		}
		
		@Test
		public void checkCaughtLogs() {
			EngineTestKit.engine("junit-vintage")
				.selectors(selectMethod(FakeTest.class, "caught"))
				.execute()
					.testEvents()
						.assertStatistics(s -> s.aborted(0).failed(0).succeeded(1).skipped(0));
			String logs = writer.toString();
			assertThat(logs)
				.contains(CAUGHT_HEADER)
				.contains(CAUGHT_FOOTER);
		}
		
		@Test
		public void checkCaughtByAnnotationLogs() {
			EngineTestKit.engine("junit-vintage")
				.selectors(selectMethod(FakeTest.class, "caughtByAnnotation"))
				.execute()
					.testEvents()
						.assertStatistics(s -> s.aborted(0).failed(0).succeeded(1).skipped(0));
			String logs = writer.toString();
			assertThat(logs)
				.contains(CAUGHT_ANNOTATION_HEADER)
				.contains(CAUGHT_ANNOTATION_FOOTER);
		}
		


		public static class FakeTest {
			@SuppressWarnings("deprecation")	// need to test JUnit 4 behavior
			ExpectedException thrown = ExpectedException.none();
			@Rule public final RuleChain chain = RuleChain
					.outerRule(new LoggingTestRule(100, "foo", new TestPrinterFactoryAdapter()))
					.around(thrown);
			
			@Test
			@SuppressWarnings("squid:S2699")
			public void success() {
				Assume.assumeTrue(System.getProperty("execute-fake-test-for-testing-logging-extension", "").equals("true"));
			}
			
			@Test
			@SuppressWarnings("squid:S2699")
			public void failure() throws CustomException {
				Assume.assumeTrue(System.getProperty("execute-fake-test-for-testing-logging-extension", "").equals("true"));
				throw new CustomException("exception message", new IllegalArgumentException("cause message"));
			}
			
			@Test
			public void caught() throws CustomException {
				Assume.assumeTrue(System.getProperty("execute-fake-test-for-testing-logging-extension", "").equals("true"));
				thrown.expect(CustomException.class);
				throw new CustomException("exception message", new IllegalArgumentException("cause message"));
			}
			
			@Test(expected = CustomException.class)
			public void caughtByAnnotation() throws CustomException {
				Assume.assumeTrue(System.getProperty("execute-fake-test-for-testing-logging-extension", "").equals("true"));
				throw new CustomException("exception message", new IllegalArgumentException("cause message"));
			}
			
		}
	}

	@Ignore("JUnit 4 doesn't provide extensions using simply an annotation. "
			+ "Is this feature really needed ? "
			+ "Too many efforts for JUnit version that will become outdated")
	public static class TestClassAnnotatedWithLogTestInformation {
		StringWriter writer;

		@Before
		public void setup() {
			System.setProperty("execute-fake-test-for-testing-logging-extension", "true");
			writer = new StringWriter();
			TestPrinterFactoryAdapter.setWriter(writer);
		}
		
		@After
		public void clean() {
			System.setProperty("execute-fake-test-for-testing-logging-extension", "");
			TestPrinterFactoryAdapter.reset();
		}
		
		@Test
		public void checkAnnotatedSuccessLogs() {
			EngineTestKit.engine("junit-vintage")
				.selectors(selectMethod(FakeTest.class, "success"))
				.execute()
					.testEvents()
						.assertStatistics(s -> s.aborted(0).failed(0).succeeded(1).skipped(0));
			String logs = writer.toString();
			assertThat(logs)
				.contains(SUCCESS_HEADER)
				.contains(SUCCESS_FOOTER);
		}
		
		@Test
		public void checkAnnotatedFailureLogs() {
			EngineTestKit.engine("junit-vintage")
				.selectors(selectMethod(FakeTest.class, "failure"))
				.execute()
					.testEvents()
						.assertStatistics(s -> s.aborted(0).failed(1).succeeded(0).skipped(0));
			String logs = writer.toString();
			assertThat(logs)
				.contains(FAILURE_HEADER)
				.contains(FAILURE_FOOTER);
		}
		
		@Test
		public void checkAnnotatedCaughtLogs() {
			EngineTestKit.engine("junit-vintage")
				.selectors(selectMethod(FakeTest.class, "caught"))
				.execute()
					.testEvents()
						.assertStatistics(s -> s.aborted(0).failed(0).succeeded(1).skipped(0));
			String logs = writer.toString();
			assertThat(logs)
				.contains(CAUGHT_HEADER)
				.contains(CAUGHT_FOOTER);
		}
		
		@Test
		public void checkAnnotatedCaughtByAnnotationLogs() {
			EngineTestKit.engine("junit-vintage")
				.selectors(selectMethod(FakeTest.class, "caughtByAnnotation"))
				.execute()
					.testEvents()
						.assertStatistics(s -> s.aborted(0).failed(0).succeeded(1).skipped(0));
			String logs = writer.toString();
			assertThat(logs)
				.contains(CAUGHT_ANNOTATION_HEADER)
				.contains(CAUGHT_ANNOTATION_FOOTER);
		}
	
		@LogTestInformation(maxLength = 100, marker = "foo", printer = TestPrinterFactoryAdapter.class)
		public static class FakeTest {
			@SuppressWarnings("deprecation")	// need to test JUnit 4 behavior
			@Rule public final ExpectedException thrown = ExpectedException.none();
			
			@Test
			@SuppressWarnings("squid:S2699")
			public void success() {
				Assume.assumeTrue(System.getProperty("execute-fake-test-for-testing-logging-extension", "").equals("true"));
			}
			
			@Test
			@SuppressWarnings("squid:S2699")
			public void failure() throws CustomException {
				Assume.assumeTrue(System.getProperty("execute-fake-test-for-testing-logging-extension", "").equals("true"));
				throw new CustomException("exception message", new IllegalArgumentException("cause message"));
			}
			
			@Test
			public void caught() throws CustomException {
				Assume.assumeTrue(System.getProperty("execute-fake-test-for-testing-logging-extension", "").equals("true"));
				thrown.expect(CustomException.class);
				throw new CustomException("exception message", new IllegalArgumentException("cause message"));
			}
			
			@Test(expected = CustomException.class)
			public void caughtByAnnotation() throws CustomException {
				Assume.assumeTrue(System.getProperty("execute-fake-test-for-testing-logging-extension", "").equals("true"));
				throw new CustomException("exception message", new IllegalArgumentException("cause message"));
			}
		}
	}
	
	@SuppressWarnings("serial")
	public static class CustomException extends Exception {
		public CustomException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
