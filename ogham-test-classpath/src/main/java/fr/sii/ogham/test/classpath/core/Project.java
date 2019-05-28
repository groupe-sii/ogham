package fr.sii.ogham.test.classpath.core;

import java.nio.file.Path;

import lombok.Data;

@Data
public class Project<P> {
	private final Path path;
	private final P variables;
}
