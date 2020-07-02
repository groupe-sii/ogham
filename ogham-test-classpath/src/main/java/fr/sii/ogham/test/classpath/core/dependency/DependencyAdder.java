package fr.sii.ogham.test.classpath.core.dependency;

import java.util.List;

import fr.sii.ogham.test.classpath.core.Project;
import fr.sii.ogham.test.classpath.core.exception.AddDependencyException;

public interface DependencyAdder {
	void addDependencies(Project<?> project, List<Dependency> dependencies) throws AddDependencyException;

	void addDependencies(Project<?> project, List<Dependency> dependencies, boolean skipSameDepWithDifferentScope) throws AddDependencyException;
}
