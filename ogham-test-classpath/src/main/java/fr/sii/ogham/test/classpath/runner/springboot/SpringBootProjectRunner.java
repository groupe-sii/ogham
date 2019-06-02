package fr.sii.ogham.test.classpath.runner.springboot;

import static fr.sii.ogham.test.classpath.runner.springboot.SpringBootDependency.CONFIGURATION_PROCESSOR;
import static fr.sii.ogham.test.classpath.runner.springboot.SpringBootDependency.DEVTOOLS;
import static fr.sii.ogham.test.classpath.runner.springboot.SpringBootDependency.LOMBOK;
import static fr.sii.ogham.test.classpath.runner.util.RunnerUtils.addMavenWrapper;
import static fr.sii.ogham.test.classpath.runner.util.RunnerUtils.addModules;
import static java.util.Arrays.asList;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import fr.sii.ogham.test.classpath.core.BuildTool;
import fr.sii.ogham.test.classpath.core.JavaVersion;
import fr.sii.ogham.test.classpath.core.dependency.Dependency;
import fr.sii.ogham.test.classpath.core.exception.AddDependencyException;
import fr.sii.ogham.test.classpath.core.exception.ProjectInitializationException;
import fr.sii.ogham.test.classpath.ogham.OghamDependency;
import fr.sii.ogham.test.classpath.ogham.OghamProperties;
import fr.sii.ogham.test.classpath.runner.common.ProjectsCreator;
import fr.sii.ogham.test.classpath.runner.common.SingleProjectCreationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(1)
public class SpringBootProjectRunner implements ApplicationRunner {
	private static final List<SpringBootDependency> STANDARD_BOOT_DEPS = asList(CONFIGURATION_PROCESSOR, DEVTOOLS, LOMBOK);

	@Autowired
	SpringMatrixProperties springMatrixProperties;

	@Autowired
	OghamProperties oghamProperties;

	@Autowired
	ProjectsCreator<SpringBootProjectParams, SpringBootDependency> projectsCreator;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		run(args.getNonOptionArgs().get(0), args.getOptionValues("override")!=null);
	}

	@SuppressWarnings("javadoc")
	public void run(String parentFolderPath, boolean override) throws IOException, InterruptedException, ExecutionException, XmlPullParserException, ProjectInitializationException, AddDependencyException, SingleProjectCreationException {
		log.info("Generating Spring Boot projects...");
		Path parentFolder = Paths.get(parentFolderPath);
		List<String> modules = projectsCreator.createProjects(parentFolder, override, generateSringBootMatrix(), STANDARD_BOOT_DEPS);
		addModules(parentFolder, springMatrixProperties.getJavaVersions(), modules);
		addMavenWrapper(parentFolder, springMatrixProperties.getJavaVersions());
		log.info("{} Spring Boot projects created", modules.size());
	}

	private List<SpringBootProjectParams> generateSringBootMatrix() {
		List<SpringBootProjectParams> expanded = new ArrayList<>();
		for (JavaVersion javaVersion : springMatrixProperties.getJavaVersions()) {
			for (BuildTool buildTool : springMatrixProperties.getBuild()) {
				for (String bootVersion : springMatrixProperties.getSpringBootVersion()) {
					for (List<SpringBootDependency> springDeps : springMatrixProperties.getExpandedSpringBootDependencies()) {
						for (Dependency oghamDeps : getOghamDependencies(springMatrixProperties.getOghamDependencies())) {
							expanded.add(new SpringBootProjectParams(javaVersion, buildTool, bootVersion, getDependencies(springDeps), asList(oghamDeps)));
						}
					}
				}
			}
		}
		return expanded;
	}

	private List<SpringBootDependency> getDependencies(List<SpringBootDependency> deps) {
		List<SpringBootDependency> dependencies = new ArrayList<>();
		dependencies.addAll(STANDARD_BOOT_DEPS);
		dependencies.addAll(deps);
		return dependencies;
	}

	private List<Dependency> getOghamDependencies(List<OghamDependency> oghamDependencies) {
		List<Dependency> deps = new ArrayList<>();
		for(OghamDependency od : oghamDependencies) {
			deps.add(od.toDependency(oghamProperties.getOghamVersion()));
		}
		return deps;
	}

}
