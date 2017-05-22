package fr.sii.ogham.test.classpath.core.dependency;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Scope {
	COMPILE("compile"),
	PROVIDED("provided"),
	RUNTIME("runtime"),
	TEST("test"),
	SYSTEM("system");
	
	private final String value;
}
