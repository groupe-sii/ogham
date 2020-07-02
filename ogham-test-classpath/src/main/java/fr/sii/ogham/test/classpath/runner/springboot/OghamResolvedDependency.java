package fr.sii.ogham.test.classpath.runner.springboot;

import fr.sii.ogham.test.classpath.core.dependency.Dependency;
import fr.sii.ogham.test.classpath.ogham.OghamDependency;
import lombok.Data;

@Data
public class OghamResolvedDependency {
	private final OghamDependency oghamDependency;
	private final Dependency resolvedDependency;
}
