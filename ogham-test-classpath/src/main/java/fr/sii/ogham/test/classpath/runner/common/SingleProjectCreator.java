package fr.sii.ogham.test.classpath.runner.common;

import java.nio.file.Path;
import java.util.List;

public interface SingleProjectCreator<P, D> {
	public String createProject(Path parentFolder, boolean override, P params, List<D> exclude) throws SingleProjectCreationException;
}
