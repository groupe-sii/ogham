package fr.sii.ogham.spock;

import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension;
import org.spockframework.runtime.model.FeatureInfo;
import org.spockframework.runtime.model.MethodInfo;
import org.spockframework.runtime.model.SpecInfo;

/**
 * Register {@link LoggingTestInterceptor} for tests annotated with
 * {@link LoggingTest} annotation.
 * 
 * @author Aurélien Baudet
 *
 */
public class LoggingTestExtension extends AbstractAnnotationDrivenExtension<LoggingTest> {

	@Override
	public void visitSpecAnnotation(LoggingTest annotation, SpecInfo spec) {
		for (FeatureInfo feature : spec.getFeatures()) {
			if (!feature.getFeatureMethod().getReflection().isAnnotationPresent(LoggingTest.class)) {
				feature.getFeatureMethod().addInterceptor(new LoggingTestInterceptor(annotation));
			}
		}
	}

	@Override
	public void visitFixtureAnnotation(LoggingTest annotation, MethodInfo fixtureMethod) {
		fixtureMethod.addInterceptor(new LoggingTestInterceptor(annotation));
	}

}
