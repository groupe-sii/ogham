package fr.sii.ogham.test.classpath.runner.standalone;

import static fr.sii.ogham.test.classpath.runner.common.IdentifierGenerator.generateIdentifier;
import static fr.sii.ogham.test.classpath.runner.common.IdentifierGenerator.getGroupName;
import static fr.sii.ogham.test.classpath.runner.util.RunnerUtils.isSkip;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import fr.sii.ogham.test.classpath.core.Project;
import fr.sii.ogham.test.classpath.core.ProjectInitializer;
import fr.sii.ogham.test.classpath.core.dependency.Dependency;
import fr.sii.ogham.test.classpath.core.dependency.DependencyAdder;
import fr.sii.ogham.test.classpath.core.exception.AddDependencyException;
import fr.sii.ogham.test.classpath.core.exception.ProjectInitializationException;
import fr.sii.ogham.test.classpath.ogham.OghamDependency;
import fr.sii.ogham.test.classpath.ogham.OghamProperties;
import fr.sii.ogham.test.classpath.runner.common.SingleProjectCreationException;
import fr.sii.ogham.test.classpath.runner.common.SingleProjectCreator;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StandaloneSingleProjectCreator implements SingleProjectCreator<StandaloneProjectParams, OghamDependency> {
	private final ProjectInitializer<StandaloneProjectParams> projectInitializer;
	private final DependencyAdder dependencyAdder;
	private final OghamProperties oghamProperties;

	public StandaloneSingleProjectCreator(ProjectInitializer<StandaloneProjectParams> projectInitializer,
										  @Qualifier("dependencyAdder") DependencyAdder dependencyAdder,
										  OghamProperties oghamProperties) {
		this.projectInitializer = projectInitializer;
		this.dependencyAdder = dependencyAdder;
		this.oghamProperties = oghamProperties;
	}

	public String createProject(Path parentFolder, boolean override, StandaloneProjectParams params, List<OghamDependency> exclude) throws SingleProjectCreationException {
		String identifier = generateIdentifier(params);
		Path moduleParentFolder = parentFolder.resolve(params.getJavaVersion().name()).resolve(getGroupName(identifier));
		if (isSkip(override, moduleParentFolder.resolve(identifier))) {
			log.info("Skipping creation of project {} because projects already exist", identifier);
			return identifier;
		}
		log.info("Creating project {}", identifier);
		try {
			Project<StandaloneProjectParams> project = projectInitializer.initialize(moduleParentFolder, identifier, params);
			dependencyAdder.addDependencies(project, toDependencies(params.getOghamDependencies()));
			return identifier;
		} catch (ProjectInitializationException | AddDependencyException e) {
			log.error("Failed to generate project {}: {}", identifier, e.getMessage());
			throw new SingleProjectCreationException("Failed to generate project " + identifier, e);
		}
	}

	private List<Dependency> toDependencies(List<OghamDependency> oghamDependencies) {
		List<Dependency> deps = new ArrayList<>();
		for(OghamDependency od : oghamDependencies) {
			deps.add(od.toDependency(oghamProperties.getOghamVersion()));
		}
		return deps;
	}
}
