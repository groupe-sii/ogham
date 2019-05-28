package fr.sii.ogham.test.classpath.runner.standalone;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import fr.sii.ogham.test.classpath.core.BuildTool;
import fr.sii.ogham.test.classpath.core.JavaVersion;
import fr.sii.ogham.test.classpath.ogham.OghamDependency;
import lombok.Data;

@Data
@Component
@ConfigurationProperties("standalone-matrix")
public class StandaloneMatrixProperties {
	private List<String> java;
	private List<BuildTool> build;
	private List<String> oghamDependencies;
	
	public List<List<OghamDependency>> getExpandedOghamDependencies() {
		List<List<OghamDependency>> deps = new ArrayList<>();
		for(String dep : oghamDependencies) {
			List<OghamDependency> oghamDeps = new ArrayList<>();
			deps.add(oghamDeps);
			if(!dep.isEmpty()) {
				for(String d : dep.split("\\+")) {
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
}
