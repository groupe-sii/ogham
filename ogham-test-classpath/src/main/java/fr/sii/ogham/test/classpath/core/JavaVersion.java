package fr.sii.ogham.test.classpath.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JavaVersion {
	JAVA_13("13", 13),
	JAVA_12("12", 12),
	JAVA_11("11", 11),
	JAVA_10("10", 10),
	JAVA_9("9", 9),
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

	public static JavaVersion fromVersion(String version) {
		for(JavaVersion v : JavaVersion.values()) {
			if(v.getVersion().equals(version)) {
				return v;
			}
		}
		throw new IllegalArgumentException("No matching JavaVersion for "+version);
	}
}
