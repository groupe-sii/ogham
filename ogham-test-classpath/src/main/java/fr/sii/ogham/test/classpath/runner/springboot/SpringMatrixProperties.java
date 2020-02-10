package fr.sii.ogham.test.classpath.runner.springboot;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import fr.sii.ogham.test.classpath.core.JavaVersion;
import lombok.Data;

@Data
@Component
@ConfigurationProperties("spring-matrix")
public class SpringMatrixProperties {
	private List<SingleMatrixProperties> matrix;
	
	public List<JavaVersion> getDistinctJavaVersions() {
		List<JavaVersion> javaVersions = matrix
				.stream()
				.map(SingleMatrixProperties::getJavaVersions)
				.flatMap(Collection::stream)
				.distinct()
				.collect(toList());
		return javaVersions;
	}
}
