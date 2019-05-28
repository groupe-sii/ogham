package fr.sii.ogham.test.classpath.runner.springboot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SpringBootDependency {
	LOMBOK("lombok"),
	CONFIGURATION_PROCESSOR("configuration-processor"),
	WEB("web"),
	THYMELEAF("thymeleaf"),
	FREEMARKER("freemarker"),
	MAIL("mail"),
	DEVTOOLS("devtools");
	
	private final String module;

	public static SpringBootDependency fromModule(String moduleName) {
		for(SpringBootDependency d : SpringBootDependency.values()) {
			if(d.getModule().equals(moduleName)) {
				return d;
			}
		}
		throw new IllegalArgumentException("No matching SpringBootDependency for "+moduleName);
	}
}
