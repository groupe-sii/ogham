package fr.sii.ogham.test.classpath.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JavaVersion {
	JAVA_8("1.8", 8),
	JAVA_7("1.7", 7);
	
	private final String version;
	private final int number;
	
	public String getDirectoryName() {
		return name();
	}
	
	public String getNormalizedName() {
		return name().toLowerCase().replace("_", "");
	}
}
