package fr.sii.ogham.test.classpath.runner.standalone;

import static fr.sii.ogham.test.classpath.matrix.MatrixUtils.expand;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import fr.sii.ogham.test.classpath.core.BuildTool;
import fr.sii.ogham.test.classpath.core.JavaVersion;
import fr.sii.ogham.test.classpath.core.dependency.Dependency;
import fr.sii.ogham.test.classpath.ogham.OghamDependency;
import lombok.Data;

@Data
@Component
@ConfigurationProperties("standalone-matrix")
public class StandaloneMatrixProperties {
	private List<String> java;
	private List<BuildTool> build;
	private List<String> oghamDependencies;
	private List<String> additionalDependencies;
	
	public List<List<OghamDependency>> getExpandedOghamDependencies() {
		List<List<OghamDependency>> deps = new ArrayList<>();
		for(String dep : oghamDependencies) {
			List<OghamDependency> oghamDeps = new ArrayList<>();
			deps.add(oghamDeps);
			if(!dep.isEmpty()) {
				for(String d : expand(dep)) {
					oghamDeps.add(OghamDependency.fromArtifactName(d));
				}
			}
		}
		return deps;
	}
	
	public List<JavaVersion> getJavaVersions() {
		List<JavaVersion> javaVersions = new ArrayList<>();
		for(String version : java) {
			javaVersions.add(JavaVersion.fromVersion(version));
		}
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
