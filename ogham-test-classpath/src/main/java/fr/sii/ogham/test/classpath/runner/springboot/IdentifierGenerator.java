package fr.sii.ogham.test.classpath.runner.springboot;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import fr.sii.ogham.test.classpath.core.JavaVersion;
import fr.sii.ogham.test.classpath.ogham.OghamDependency;
import fr.sii.ogham.test.classpath.runner.standalone.StandaloneProjectParams;

public class IdentifierGenerator {
	public static String generateIdentifier(SpringBootProjectParams params, List<SpringBootDependency> exclude) {
		// @formatter;off
		return params.getJavaVersion().getNormalizedName() + "." 
				+ params.getBuildTool().name().toLowerCase() + "." 
				+ "boot-" + params.getSpringBootVersion()+"."
				+ toBootDepsString(params.getSpringBootDependencies(), exclude) + "."
				+ toOghamBootDepsString(params.getOghamDependencies());
		// @formatter:on
	}
	
	public static String generateIdentifier(StandaloneProjectParams params) {
		// @formatter;off
		return params.getJavaVersion().getNormalizedName() + "." 
				+ params.getBuildTool().name().toLowerCase() + "." 
				+ toOghamDepsString(params.getOghamDependencies());
		// @formatter:on
	}

	public static boolean isIdentifierForJavaVersion(String identifier, JavaVersion javaVersion) {
		return identifier.startsWith(javaVersion.getNormalizedName());
	}

	public static String getGroupName(String identifier) {
		if (identifier.contains(".boot-")) {
			return identifier.replaceAll("^.+boot-(\\d+[.]\\d+)[.]\\d+[.][^.]+.*$", "spring-boot-$1.x");
		}
		return "standalone";
	}

	private static String toOghamBootDepsString(List<OghamResolvedDependency> deps) {
		List<String> depsStr = new ArrayList<>();
		for(OghamResolvedDependency dep : deps) {
			depsStr.add(dep.getResolvedDependency().getArtifactId().replace("ogham-spring-boot-", ""));
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

	private static String toOghamDepsString(List<OghamDependency> deps) {
		List<String> depsStr = new ArrayList<>();
		for(OghamDependency dep : deps) {
			depsStr.add(dep.getArtifactId().replace("ogham-", "").replace("spring-boot-", ""));
		}
		return StringUtils.join(depsStr, "_");
	}
	
	private IdentifierGenerator() {
		super();
	}
}
