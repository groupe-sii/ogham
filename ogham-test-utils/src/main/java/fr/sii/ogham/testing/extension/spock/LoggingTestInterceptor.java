package fr.sii.ogham.testing.extension.spock;

import org.spockframework.runtime.extension.IMethodInterceptor;
import org.spockframework.runtime.extension.IMethodInvocation;

import fr.sii.ogham.testing.extension.common.LogTestInformation;
import fr.sii.ogham.testing.extension.common.TestInformationLogger;

/**
 * Interceptor to write information about test in logs.
 * 
 * @author Aurélien Baudet
 *
 */
public class LoggingTestInterceptor implements IMethodInterceptor {
	private final TestInformationLogger logger;

	public LoggingTestInterceptor(LogTestInformation annotation) throws InstantiationException, IllegalAccessException {
		this(new TestInformationLogger(annotation.maxLength(), annotation.marker(), annotation.printer().newInstance()));
	}

	public LoggingTestInterceptor(TestInformationLogger logger) {
		super();
		this.logger = logger;
	}

	@Override
	public void intercept(IMethodInvocation invocation) throws Throwable {
		String testName = invocation.getIteration().getName();
		try {
			logger.writeStart(testName);
			invocation.proceed();
			logger.writeSuccess(testName);
		} catch (Throwable e) { // NOSONAR
			logger.writeFailure(testName, e);
			throw e;
		}
	}

}
