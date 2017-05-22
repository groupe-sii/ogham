package fr.sii.ogham.test.classpath.core.dependency;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Dependency {
	private String groupId;
	private String artifactId;
	private String version;
	private Scope scope;
	
	public Dependency(String groupId, String artifactId, String version) {
		this(groupId, artifactId, version, Scope.COMPILE);
	}
}
