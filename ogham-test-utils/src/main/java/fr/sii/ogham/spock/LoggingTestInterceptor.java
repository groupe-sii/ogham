package fr.sii.ogham.spock;

import org.spockframework.runtime.extension.IMethodInterceptor;
import org.spockframework.runtime.extension.IMethodInvocation;

import fr.sii.ogham.common.TestLogger;

/**
 * Interceptor to write information about test in logs.
 * 
 * @author Aurélien Baudet
 *
 */
public class LoggingTestInterceptor implements IMethodInterceptor {
	private final TestLogger logger;

	public LoggingTestInterceptor(LoggingTest annotation) {
		this(new TestLogger(annotation.maxLength()));
	}

	public LoggingTestInterceptor(TestLogger logger) {
		super();
		this.logger = logger;
	}

	@Override
	public void intercept(IMethodInvocation invocation) throws Throwable {
		String testName = invocation.getFeature().getDescription().getDisplayName();
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
