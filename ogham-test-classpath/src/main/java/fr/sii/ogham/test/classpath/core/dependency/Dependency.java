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
	private String type;

	public Dependency(String groupId, String artifactId, String version) {
		this(groupId, artifactId, version, Scope.COMPILE);
	}

	public Dependency(String groupId, String artifactId, String version, Scope scope) {
		this(groupId, artifactId, version, scope, null);
	}

	public static Dependency from(String fullDep) {
		String[] parts = fullDep.split("[:]");
		if (parts.length < 2) {
			throw new IllegalArgumentException("Invalid dependency of form '<groupId>:<artifactId>[:<version>][:<scope>][:type]'. Some part is missing: " + fullDep);
		}
		String groupId = parts[0];
		String artifactId = parts[1];
		String version = parts.length > 2 ? nullIfEmpty(parts[2]) : null;
		Scope scope = parts.length > 3 ? Scope.valueOf(parts[3].toUpperCase()) : Scope.COMPILE;
		String type = parts.length > 4 ? nullIfEmpty(parts[4]) : null;
		return new Dependency(groupId, artifactId, version, scope, type);
	}

	private static String nullIfEmpty(String str) {
		if (str == null || str.isEmpty()) {
			return null;
		}
		return str;
	}
}
