package fr.sii.ogham.test.classpath.springboot;

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
	private List<SpringBootDependency> springBootDependencies;
	private List<OghamDependency> oghamDependencies;
	
	public List<JavaVersion> getJavaVersions() {
		List<JavaVersion> javaVersions = new ArrayList<>();
		for(String version : java) {
			javaVersions.add(JavaVersion.valueOf("JAVA"+version.replace("1.", "_")));
		}
		return javaVersions;
	}
}
