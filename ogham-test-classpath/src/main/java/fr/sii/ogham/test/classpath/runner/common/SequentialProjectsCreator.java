package fr.sii.ogham.test.classpath.runner.common;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SequentialProjectsCreator<P, D> implements ProjectsCreator<P, D> {
	private final SingleProjectCreator<P, D> projectCreator;

	@Override
	public List<String> createProjects(Path parentFolder, boolean override, List<P> expandedMatrix, List<D> exclude) throws SingleProjectCreationException {
		List<String> modules = new ArrayList<>();
		for (final P params : expandedMatrix) {
			modules.add(projectCreator.createProject(parentFolder, override, params, exclude));
		}
		return modules;
	}

}
