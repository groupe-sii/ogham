package fr.sii.ogham.test.classpath.springboot;

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
}
