package fr.sii.ogham.test.classpath.core;

import java.nio.file.Path;

import lombok.Data;

@Data
public class Project {
	private final Path path;
	private final ProjectVariables variables;
}
