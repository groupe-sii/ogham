package fr.sii.ogham.test.classpath.runner.springboot;

import static java.util.Arrays.asList;

import java.util.List;

import fr.sii.ogham.test.classpath.core.facet.Facet;
import lombok.Getter;

@Getter
public enum SpringBootDependency {
	LOMBOK("lombok"),
	CONFIGURATION_PROCESSOR("configuration-processor"),
	WEB("web"),
	THYMELEAF("thymeleaf", Facet.SPRING_THYMELEAF),
	FREEMARKER("freemarker", Facet.SPRING_FREEMARKER),
	MAIL("mail", Facet.SPRING_MAIL),
	DEVTOOLS("devtools");
	
	private final String module;
	private final Facet[] facets;

	public List<Facet> getFacets() {
		return asList(facets);
	}

	public static SpringBootDependency fromModule(String moduleName) {
		for(SpringBootDependency d : SpringBootDependency.values()) {
			if(d.getModule().equals(moduleName)) {
				return d;
			}
		}
		throw new IllegalArgumentException("No matching SpringBootDependency for "+moduleName);
	}

	private SpringBootDependency(String module, Facet... facets) {
		this.module = module;
		this.facets = facets;
	}
}
