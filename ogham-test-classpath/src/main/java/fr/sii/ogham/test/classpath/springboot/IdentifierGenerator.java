package fr.sii.ogham.test.classpath.springboot;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import fr.sii.ogham.test.classpath.core.JavaVersion;
import fr.sii.ogham.test.classpath.core.dependency.Dependency;

public class IdentifierGenerator {
	public static String generateIdentifier(SpringBootProjectParams params, List<SpringBootDependency> exclude) {
		// @formatter;off
		return params.getJavaVersion().getNormalizedName() + "." 
				+ params.getBuildTool().name().toLowerCase() + "." 
				+ "boot-" + params.getSpringBootVersion()+"."
				+ toBootDepsString(params.getSpringBootDependencies(), exclude) + "."
				+ toOghamDepsString(params.getOghamDependencies());
		// @formatter:on
	}
	
	public static boolean isIdentifierForJavaVersion(String identifier, JavaVersion javaVersion) {
		return identifier.startsWith(javaVersion.getNormalizedName());
	}

	private static String toOghamDepsString(List<Dependency> deps) {
		List<String> depsStr = new ArrayList<>();
		for(Dependency dep : deps) {
			depsStr.add(dep.getArtifactId().replace("ogham-spring-boot-", ""));
		}
		return StringUtils.join(depsStr, "_");
	}

	private static String toBootDepsString(List<SpringBootDependency> deps, List<SpringBootDependency> exclude) {
		List<String> depsStr = new ArrayList<>();
		for(SpringBootDependency dep : deps) {
			if(!exclude.contains(dep)) {
				depsStr.add(dep.getModule());
			}
		}
		return StringUtils.join(depsStr, "_");
	}

}
