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
	
	public static Dependency from(String fullDep) {
		String[] parts = fullDep.split("[:]");
		if (parts.length < 3) {
			throw new IllegalArgumentException("Invalid dependency of form '<groupId>:<artifactId>:<version>'. Some part is missing: " + fullDep);
		}
		String groupId = parts[0];
		String artifactId = parts[1];
		String version = parts[2];
		Scope scope = parts.length > 3 ? Scope.valueOf(parts[3].toUpperCase()) : Scope.COMPILE;
		return new Dependency(groupId, artifactId, version, scope);
	}
}
