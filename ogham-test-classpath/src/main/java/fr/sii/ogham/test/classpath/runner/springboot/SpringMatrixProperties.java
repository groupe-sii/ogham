package fr.sii.ogham.test.classpath.runner.springboot;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import fr.sii.ogham.test.classpath.core.JavaVersion;
import fr.sii.ogham.test.classpath.core.dependency.Dependency;
import lombok.Data;

@Data
@Component
@ConfigurationProperties("spring-matrix")
public class SpringMatrixProperties {
	private List<SingleMatrixProperties> matrix;
	private List<String> additionalDependencies;

	public List<JavaVersion> getDistinctJavaVersions() {
		List<JavaVersion> javaVersions = matrix
				.stream()
				.map(SingleMatrixProperties::getJavaVersions)
				.flatMap(Collection::stream)
				.distinct()
				.collect(toList());
		return javaVersions;
	}
	
	public List<Dependency> getAdditionalDependencies() {
		if (additionalDependencies == null) {
			return emptyList();
		}
		return additionalDependencies.stream()
				.map(Dependency::from)
				.collect(toList());
	}
}
