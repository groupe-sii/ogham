package fr.sii.ogham.testing.extension.spock;

import fr.sii.ogham.testing.extension.common.LogTestInformation;
import org.spockframework.runtime.extension.IAnnotationDrivenExtension;
import org.spockframework.runtime.model.FeatureInfo;
import org.spockframework.runtime.model.MethodInfo;
import org.spockframework.runtime.model.SpecInfo;

/**
 * Register {@link LoggingTestInterceptor} for tests annotated with
 * {@link LogTestInformation} annotation.
 * 
 * @author Aurélien Baudet
 *
 */
public class LoggingTestExtension implements IAnnotationDrivenExtension<LogTestInformation> {

	@Override
	public void visitSpecAnnotation(LogTestInformation annotation, SpecInfo spec) {
		for (FeatureInfo feature : spec.getFeatures()) {
			if (!feature.getFeatureMethod().getReflection().isAnnotationPresent(LogTestInformation.class)) {
				intercept(annotation, feature.getFeatureMethod());
			}
		}
	}

	@Override
	public void visitFixtureAnnotation(LogTestInformation annotation, MethodInfo fixtureMethod) {
		intercept(annotation, fixtureMethod);
	}


	private static void intercept(LogTestInformation annotation, MethodInfo methodInfo) {
		try {
			methodInfo.addInterceptor(new LoggingTestInterceptor(annotation));
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalArgumentException("Failed to create logger instance", e);
		}
	}

}
