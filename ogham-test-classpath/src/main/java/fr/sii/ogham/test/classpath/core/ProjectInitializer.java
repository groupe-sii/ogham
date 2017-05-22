package fr.sii.ogham.test.classpath.core;

import java.nio.file.Path;

import fr.sii.ogham.test.classpath.core.exception.ProjectInitializationException;

public interface ProjectInitializer {
	Project initialize(Path parentFolder, String identifier, ProjectVariables variables) throws ProjectInitializationException;
}
