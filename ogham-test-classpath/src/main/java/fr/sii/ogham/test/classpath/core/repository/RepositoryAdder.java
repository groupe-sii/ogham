package fr.sii.ogham.test.classpath.core.repository;

import fr.sii.ogham.test.classpath.core.Project;
import fr.sii.ogham.test.classpath.core.exception.AddRepositoryException;

import java.util.List;

public interface RepositoryAdder {
    void addRepositories(Project<?> project, List<Repository> repositories) throws AddRepositoryException;
}
