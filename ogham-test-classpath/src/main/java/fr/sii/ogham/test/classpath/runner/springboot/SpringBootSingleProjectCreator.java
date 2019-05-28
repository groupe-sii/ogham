package fr.sii.ogham.test.classpath.runner.springboot;

import static fr.sii.ogham.test.classpath.runner.springboot.IdentifierGenerator.generateIdentifier;
import static fr.sii.ogham.test.classpath.runner.util.RunnerUtils.isSkip;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.stereotype.Service;

import fr.sii.ogham.test.classpath.core.Project;
import fr.sii.ogham.test.classpath.core.ProjectInitializer;
import fr.sii.ogham.test.classpath.core.dependency.DependencyAdder;
import fr.sii.ogham.test.classpath.core.exception.AddDependencyException;
import fr.sii.ogham.test.classpath.core.exception.ProjectInitializationException;
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

	public String createProject(Path parentFolder, boolean override, SpringBootProjectParams params, List<SpringBootDependency> exclude) throws SingleProjectCreationException {
		String identifier = generateIdentifier(params, exclude);
		if (isSkip(override, parentFolder.resolve(params.getJavaVersion().name()).resolve(identifier))) {
			log.info("Skipping creation of project {} because projects already exist", identifier);
			return identifier;
		}
		log.info("Creating project {}", identifier);
		try {
			Project<SpringBootProjectParams> project = projectInitializer.initialize(parentFolder.resolve(params.getJavaVersion().name()), identifier, params);
			Path testResourcesFolder = project.getPath().resolve("src/test/resources");
			Files.createDirectories(testResourcesFolder);
			Files.copy(getClass().getResourceAsStream("/spring-boot/application-for-projects.properties"), testResourcesFolder.resolve("application.properties"));
			Files.copy(getClass().getResourceAsStream("/spring-boot/logback-for-projects.xml"), testResourcesFolder.resolve("logback-test.xml"));
			dependencyAdder.addDependencies(project, params.getOghamDependencies());
			return identifier;
		} catch (AddDependencyException | IOException | ProjectInitializationException e) {
			log.error("Failed to generate project {}: {}", identifier, e.getMessage());
			throw new SingleProjectCreationException("Failed to generate project " + identifier, e);
		}
	}
}
