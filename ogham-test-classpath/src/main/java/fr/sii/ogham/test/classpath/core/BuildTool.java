package fr.sii.ogham.test.classpath.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BuildTool {
	MAVEN("maven-project"),
	GRADLE("gradle-project");
	
	private final String type;
}
