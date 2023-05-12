package fr.sii.ogham.test.classpath.runner.springboot;

import fr.sii.ogham.test.classpath.core.BuildTool;
import fr.sii.ogham.test.classpath.core.JavaVersion;
import fr.sii.ogham.test.classpath.core.dependency.Dependency;
import fr.sii.ogham.test.classpath.core.exception.AddDependencyException;
import fr.sii.ogham.test.classpath.core.exception.ProjectInitializationException;
import fr.sii.ogham.test.classpath.ogham.OghamDependency;
import fr.sii.ogham.test.classpath.ogham.OghamProperties;
import fr.sii.ogham.test.classpath.runner.common.NamedMatricesProperties;
import fr.sii.ogham.test.classpath.runner.common.ProjectsCreator;
import fr.sii.ogham.test.classpath.runner.common.SingleProjectCreationException;
import fr.sii.ogham.test.classpath.runner.exception.SkipRunException;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static fr.sii.ogham.test.classpath.runner.springboot.SpringBootDependency.*;
import static fr.sii.ogham.test.classpath.runner.util.RunnerUtils.addMavenWrapper;
import static fr.sii.ogham.test.classpath.runner.util.RunnerUtils.addModules;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@Slf4j
@Component
@Order(1)
public class SpringBootProjectRunner implements ApplicationRunner {
	private static final List<SpringBootDependency> STANDARD_BOOT_DEPS = asList(CONFIGURATION_PROCESSOR, DEVTOOLS, LOMBOK);

	@Autowired
	NamedMatricesProperties matricesProperties;

	@Autowired
	OghamProperties oghamProperties;

	@Autowired
	ProjectsCreator<SpringBootProjectParams, SpringBootDependency> projectsCreator;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		try {
			checkShouldRun();
			run(args.getNonOptionArgs().get(0), args.getOptionValues("override")!=null);
		} catch(SkipRunException e) {
			log.info(e.getMessage());
		} catch (Exception e) {
			log.error("Failed to create Spring Boot projects", e);
			System.exit(1);
		}
	}

	private void checkShouldRun() throws SkipRunException {
		if (!matricesProperties.hasSpringMatrices()) {
			throw new SkipRunException("No Spring matrix configured. Skipping");
		}
	}

	@SuppressWarnings("javadoc")
	public void run(String parentFolderPath, boolean override) throws IOException, InterruptedException, ExecutionException, XmlPullParserException, ProjectInitializationException, AddDependencyException, SingleProjectCreationException {
		log.info("Generating Spring Boot projects...");
		Path parentFolder = Paths.get(parentFolderPath);
		List<String> modules = projectsCreator.createProjects(parentFolder, override, generateSpringBootMatrix(), STANDARD_BOOT_DEPS);
		List<JavaVersion> javaVersions = matricesProperties.getSpringMatrix().getDistinctJavaVersions();
		addModules(parentFolder, javaVersions, modules);
		addMavenWrapper(parentFolder, javaVersions);
		log.info("{} Spring Boot projects created", modules.size());
	}

	private List<SpringBootProjectParams> generateSpringBootMatrix() {
		List<SpringBootProjectParams> expanded = new ArrayList<>();
		for (SingleMatrixProperties matrix : matricesProperties.getSpringMatrix().getMatrix()) {
			for (JavaVersion javaVersion : matrix.getJavaVersions()) {
				for (BuildTool buildTool : matrix.getBuild()) {
					for (String bootVersion : matrix.getSpringBootVersion()) {
						for (List<SpringBootDependency> springDeps : matrix.getExpandedSpringBootDependencies()) {
							for (List<OghamResolvedDependency> oghamDeps : resolveExpandedOghamDependencies(matrix.getExpandedOghamDependencies())) {
								expanded.add(new SpringBootProjectParams(
										javaVersion,
										buildTool,
										bootVersion,
										getDependencies(springDeps),
										oghamDeps,
										merge(matrix.getAdditionalDependencies(), matricesProperties.getSpringMatrix().getAdditionalDependencies()),
										matrix.getBuildProperties(),
										matrix.getDependencyManagementDependencies(),
										matrix.getRepositories(),
										matrix.getIdentifierSuffix()));
							}
						}
					}
				}
			}
		}
		return expanded;
	}

	private List<Dependency> merge(List<Dependency> additionalMatrixSpecificDependencies, List<Dependency> additionalSharedDependencies) {
		List<Dependency> dependencies = new ArrayList<>();
		dependencies.addAll(additionalMatrixSpecificDependencies);
		dependencies.addAll(additionalSharedDependencies);
		return dependencies;
	}

	private List<SpringBootDependency> getDependencies(List<SpringBootDependency> deps) {
		List<SpringBootDependency> dependencies = new ArrayList<>();
		dependencies.addAll(STANDARD_BOOT_DEPS);
		dependencies.addAll(deps);
		return dependencies;
	}

	private List<List<OghamResolvedDependency>> resolveExpandedOghamDependencies(List<List<OghamDependency>> expandedOghamDependencies) {
		return expandedOghamDependencies.stream()
				.map(this::resolveOghamDependencies)
				.collect(toList());
	}
	
	private List<OghamResolvedDependency> resolveOghamDependencies(List<OghamDependency> oghamDependencies) {
		return oghamDependencies.stream()
				.map(dep -> new OghamResolvedDependency(dep, dep.toDependency(oghamProperties.getOghamVersion())))
				.collect(toList());
	}
}
