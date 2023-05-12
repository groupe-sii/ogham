package fr.sii.ogham.test.classpath.runner.springboot;

import fr.sii.ogham.test.classpath.core.Project;
import fr.sii.ogham.test.classpath.core.ProjectInitializer;
import fr.sii.ogham.test.classpath.core.dependency.Dependency;
import fr.sii.ogham.test.classpath.core.dependency.DependencyAdder;
import fr.sii.ogham.test.classpath.core.dependency.Scope;
import fr.sii.ogham.test.classpath.core.exception.*;
import fr.sii.ogham.test.classpath.core.facet.Facet;
import fr.sii.ogham.test.classpath.core.facet.FacetAdder;
import fr.sii.ogham.test.classpath.core.packaging.PackagedAppNamer;
import fr.sii.ogham.test.classpath.core.property.PropertyAdder;
import fr.sii.ogham.test.classpath.core.repository.RepositoryAdder;
import fr.sii.ogham.test.classpath.ogham.OghamProperties;
import fr.sii.ogham.test.classpath.runner.common.SingleProjectCreationException;
import fr.sii.ogham.test.classpath.runner.common.SingleProjectCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static fr.sii.ogham.test.classpath.runner.common.IdentifierGenerator.generateIdentifier;
import static fr.sii.ogham.test.classpath.runner.common.IdentifierGenerator.getGroupName;
import static fr.sii.ogham.test.classpath.runner.util.RunnerUtils.isSkip;
import static fr.sii.ogham.test.classpath.runner.util.SourceUtils.copy;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

@Service
@Slf4j
public class SpringBootSingleProjectCreator implements SingleProjectCreator<SpringBootProjectParams, SpringBootDependency> {
	private final ProjectInitializer<SpringBootProjectParams> projectInitializer;
	private final DependencyAdder dependencyAdder;
	private final DependencyAdder dependencyManagementAdder;
	private final PropertyAdder propertyAdder;
	private final RepositoryAdder repositoryAdder;
	private final FacetAdder facetAdder;
	private final PackagedAppNamer appNamer;
	private final OghamProperties oghamProperties;

	public SpringBootSingleProjectCreator(ProjectInitializer<SpringBootProjectParams> projectInitializer,
										  @Qualifier("dependencyAdder") DependencyAdder dependencyAdder,
										  @Qualifier("dependencyManagementAdder") DependencyAdder dependencyManagementAdder,
										  PropertyAdder propertyAdder,
										  RepositoryAdder repositoryAdder,
										  FacetAdder facetAdder,
										  PackagedAppNamer appNamer,
										  OghamProperties oghamProperties) {
		this.projectInitializer = projectInitializer;
		this.dependencyAdder = dependencyAdder;
		this.dependencyManagementAdder = dependencyManagementAdder;
		this.propertyAdder = propertyAdder;
		this.repositoryAdder = repositoryAdder;
		this.facetAdder = facetAdder;
		this.appNamer = appNamer;
		this.oghamProperties = oghamProperties;
	}

	public String createProject(Path parentFolder, boolean override, SpringBootProjectParams params, List<SpringBootDependency> exclude) throws SingleProjectCreationException {
		String identifier = generateIdentifier(params, exclude);
		Path moduleParentFolder = parentFolder.resolve(params.getJavaVersion().name()).resolve(getGroupName(identifier));
		if (isSkip(override, moduleParentFolder.resolve(identifier))) {
			log.info("Skipping creation of project {} because projects already exist", identifier);
			return identifier;
		}
		log.info("Creating project {}", identifier);
		try {
			Project<SpringBootProjectParams> project = projectInitializer.initialize(moduleParentFolder, identifier, params);
			// copy source code
			copy("/src/templates/common", project.getPath());
			copy("/src/templates/spring-boot", project.getPath());
			// configure some properties
			propertyAdder.addProperties(project, params.getBuildProperties());
			// add additional dependencies as a developer would do
			dependencyAdder.addDependencies(project, toResolvedDependencies(params.getOghamDependencies()));
			// add/update code for testing
			dependencyAdder.addDependencies(project, classpathTestRuntime());
			// add additional dependencies
			dependencyAdder.addDependencies(project, params.getAdditionalDependencies());
			// add custom dependency management
			dependencyManagementAdder.addDependencies(project, params.getDependencyManagementDependencies());
			// add custom repositories
			repositoryAdder.addRepositories(project, params.getRepositories());
			facetAdder.addFacet(project, mergeFacets(params));
			appNamer.setPackagedAppName(project, "app");
			return identifier;
		} catch (AddDependencyException | IOException | ProjectInitializationException | AddFacetException |
				 PackagedAppNameException | AddPropertyException | AddRepositoryException e) {
			log.error("Failed to generate project {}: {}", identifier, e.getMessage());
			throw new SingleProjectCreationException("Failed to generate project " + identifier, e);
		}
	}

	private List<Dependency> toResolvedDependencies(List<OghamResolvedDependency> deps) {
		return deps.stream().map(OghamResolvedDependency::getResolvedDependency).collect(toList());
	}
	
	private List<Dependency> classpathTestRuntime() {
		return asList(
				new Dependency("fr.sii.ogham.internal", "ogham-test-classpath-runtime", oghamProperties.getOghamVersion()),
				new Dependency("fr.sii.ogham", "ogham-test-utils", oghamProperties.getOghamVersion(), Scope.TEST)
			);
	}

	private List<Facet> mergeFacets(SpringBootProjectParams params) {
		Stream<Facet> oghamFacets = params.getOghamDependencies().stream().flatMap(dep -> dep.getOghamDependency().getFacets().stream());
		Stream<Facet> springFacets = params.getSpringBootDependencies().stream().flatMap(dep -> dep.getFacets().stream());
		return concat(oghamFacets, springFacets).distinct().collect(toList());
	}
}