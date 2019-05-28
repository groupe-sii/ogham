package fr.sii.ogham.test.classpath.runner.common;

import java.nio.file.Path;
import java.util.List;

public interface ProjectsCreator<P, D> {
	List<String> createProjects(Path parentFolder, boolean override, List<P> expandedMatrix, List<D> exclude) throws SingleProjectCreationException;
}
