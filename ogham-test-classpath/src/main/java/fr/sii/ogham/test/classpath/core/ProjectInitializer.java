package fr.sii.ogham.test.classpath.core;

import java.nio.file.Path;

import fr.sii.ogham.test.classpath.core.exception.ProjectInitializationException;

public interface ProjectInitializer<P> {
	Project<P> initialize(Path parentFolder, String identifier, P variables) throws ProjectInitializationException;
}
