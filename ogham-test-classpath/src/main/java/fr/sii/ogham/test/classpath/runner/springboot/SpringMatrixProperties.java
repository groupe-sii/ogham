package fr.sii.ogham.test.classpath.runner.springboot;

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
@ConfigurationProperties("spring-matrix")
public class SpringMatrixProperties {
	private List<String> java;
	private List<BuildTool> build;
	private List<String> springBootVersion;
	private List<String> springBootDependencies;
	private List<OghamDependency> oghamDependencies;
	
	public List<List<SpringBootDependency>> getExpandedSpringBootDependencies() {
		List<List<SpringBootDependency>> deps = new ArrayList<>();
		for(String dep : springBootDependencies) {
			List<SpringBootDependency> bootDeps = new ArrayList<>();
			deps.add(bootDeps);
			if(!dep.isEmpty()) {
				for(String d : dep.split("\\+")) {
					bootDeps.add(SpringBootDependency.fromModule(d));
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
