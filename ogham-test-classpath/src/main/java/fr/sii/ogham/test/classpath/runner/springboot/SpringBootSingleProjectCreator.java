package fr.sii.ogham.test.classpath.runner.springboot;

import static fr.sii.ogham.test.classpath.runner.springboot.IdentifierGenerator.generateIdentifier;
import static fr.sii.ogham.test.classpath.runner.util.RunnerUtils.isSkip;
import static fr.sii.ogham.test.classpath.runner.util.SourceUtils.copy;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import fr.sii.ogham.test.classpath.core.Project;
import fr.sii.ogham.test.classpath.core.ProjectInitializer;
import fr.sii.ogham.test.classpath.core.dependency.Dependency;
import fr.sii.ogham.test.classpath.core.dependency.DependencyAdder;
import fr.sii.ogham.test.classpath.core.dependency.Scope;
import fr.sii.ogham.test.classpath.core.exception.AddDependencyException;
import fr.sii.ogham.test.classpath.core.exception.AddFacetException;
import fr.sii.ogham.test.classpath.core.exception.PackagedAppNameException;
import fr.sii.ogham.test.classpath.core.exception.ProjectInitializationException;
import fr.sii.ogham.test.classpath.core.facet.Facet;
import fr.sii.ogham.test.classpath.core.facet.FacetAdder;
import fr.sii.ogham.test.classpath.core.packaging.PackagedAppNamer;
import fr.sii.ogham.test.classpath.ogham.OghamProperties;
import fr.sii.ogham.test.classpath.runner.common.SingleProjectCreationException;
import fr.sii.ogham.test.classpath.runner.common.SingleProjectCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpringBootSingleProjectCreator implements SingleProjectCreator<SpringBootProjectParams, SpringBootDependency> {
	private final ProjectInitializer<SpringBootProjectParams> projectInitializer;
	private final DependencyAdder dependencyAdder;
	private final FacetAdder facetAdder;
	private final PackagedAppNamer appNamer;
	private final OghamProperties oghamProperties;

	public String createProject(Path parentFolder, boolean override, SpringBootProjectParams params, List<SpringBootDependency> exclude) throws SingleProjectCreationException {
		String identifier = generateIdentifier(params, exclude);
		if (isSkip(override, parentFolder.resolve(params.getJavaVersion().name()).resolve(identifier))) {
			log.info("Skipping creation of project {} because projects already exist", identifier);
			return identifier;
		}
		log.info("Creating project {}", identifier);
		try {
			Project<SpringBootProjectParams> project = projectInitializer.initialize(parentFolder.resolve(params.getJavaVersion().name()), identifier, params);
			// copy source code
			copy("/common", project.getPath());
			copy("/spring-boot/src", project.getPath().resolve("src"));
			// copy other resources
			Path testResourcesFolder = project.getPath().resolve("src/test/resources");
			Files.createDirectories(testResourcesFolder);
			Files.copy(getClass().getResourceAsStream("/spring-boot/application-for-projects.properties"), testResourcesFolder.resolve("application.properties"));
			Files.copy(getClass().getResourceAsStream("/spring-boot/logback-for-projects.xml"), testResourcesFolder.resolve("logback-test.xml"));
			// add additional dependencies as a developer would do
			dependencyAdder.addDependencies(project, toResolvedDependencies(params.getOghamDependencies()));
			// add/update code for testing
			dependencyAdder.addDependencies(project, classpathTestRuntime());
			// add additional dependencies
			dependencyAdder.addDependencies(project, params.getAdditionalDependencies());
			facetAdder.addFacet(project, mergeFacets(params));
			appNamer.setPackagedAppName(project, "app");
			return identifier;
		} catch (AddDependencyException | IOException | ProjectInitializationException | AddFacetException | PackagedAppNameException e) {
			log.error("Failed to generate project {}: {}", identifier, e.getMessage());
			throw new SingleProjectCreationException("Failed to generate project " + identifier, e);
		}
	}

	private List<Dependency> toResolvedDependencies(List<OghamResolvedDependency> deps) {
		return deps.stream().map(OghamResolvedDependency::getResolvedDependency).collect(toList());
	}
	
	private List<Dependency> classpathTestRuntime() {
		return asList(
				new Dependency("fr.sii.ogham", "ogham-test-classpath-runtime", oghamProperties.getOghamVersion()),
				new Dependency("fr.sii.ogham", "ogham-test-utils", oghamProperties.getOghamVersion(), Scope.TEST)
			);
	}

	private List<Facet> mergeFacets(SpringBootProjectParams params) {
		Stream<Facet> oghamFacets = params.getOghamDependencies().stream().flatMap(dep -> dep.getOghamDependency().getFacets().stream());
		Stream<Facet> springFacets = params.getSpringBootDependencies().stream().flatMap(dep -> dep.getFacets().stream());
		return concat(oghamFacets, springFacets).distinct().collect(toList());
	}
}