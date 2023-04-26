package fr.sii.ogham.test.classpath.runner.standalone;

import static fr.sii.ogham.test.classpath.runner.util.SourceUtils.copy;
import static java.util.stream.Collectors.joining;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import fr.sii.ogham.test.classpath.core.Project;
import fr.sii.ogham.test.classpath.core.ProjectInitializer;
import fr.sii.ogham.test.classpath.core.dependency.DependencyAdder;
import fr.sii.ogham.test.classpath.core.exception.AddDependencyException;
import fr.sii.ogham.test.classpath.core.exception.AddPropertyException;
import fr.sii.ogham.test.classpath.core.exception.AddRepositoryException;
import fr.sii.ogham.test.classpath.core.exception.ProjectInitializationException;
import fr.sii.ogham.test.classpath.core.facet.Facet;
import fr.sii.ogham.test.classpath.core.property.PropertyAdder;
import fr.sii.ogham.test.classpath.core.repository.RepositoryAdder;
import fr.sii.ogham.test.classpath.ogham.OghamProperties;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TemplatedProjectInitializer implements ProjectInitializer<StandaloneProjectParams> {
	private final Configuration freemarkerConfig;
	private final OghamProperties oghamProperties;
	private final DependencyAdder dependencyAdder;
	private final PropertyAdder propertyAdder;
	private final RepositoryAdder repositoryAdder;

	@Override
	public Project<StandaloneProjectParams> initialize(Path parentFolder, String identifier, StandaloneProjectParams variables) throws ProjectInitializationException {
		try {
			Path generatedProjectPath = parentFolder.resolve(identifier);
			// copy templated project
			copy("/src/templates/common", generatedProjectPath);
			copy("/src/templates/standalone", generatedProjectPath);
			// evaluate templates
			write("/src/templates/standalone/pom.xml.ftl", new TemplateModel(new GeneratedProject(identifier, identifier), oghamProperties.getOghamVersion(), generateActiveFacets(variables)), generatedProjectPath.resolve("pom.xml"));
			Project<StandaloneProjectParams> project = new Project<>(generatedProjectPath, variables);
			// configure some properties
			propertyAdder.addProperties(project, variables.getBuildProperties());
			// add additional dependencies
			dependencyAdder.addDependencies(project, variables.getAdditionalDependencies());
			// add custom repositories
			repositoryAdder.addRepositories(project, variables.getRepositories());
			return project;
		} catch (IOException | TemplateException | AddDependencyException | AddPropertyException |
				 AddRepositoryException e) {
			log.error("Failed to initialize project {}: {}", identifier, e.getMessage());
			throw new ProjectInitializationException("Failed to initialize project " + identifier, e);
		}
	}

	private String generateActiveFacets(StandaloneProjectParams variables) {
		return variables.getOghamDependencies().stream()
			.flatMap(dep -> dep.getFacets().stream())
			.distinct()
			.map(Facet::getFacetName)
			.collect(joining(","));
	}

	private void write(String template, Object model, Path out) throws IOException, TemplateException {
		try (FileWriter writer = new FileWriter(out.toFile())) {
			freemarkerConfig.getTemplate(template).process(model, writer);
		}
	}

	@Data
	public static class TemplateModel {
		private final GeneratedProject project;
		private final String oghamVersion;
		private final String activeFacets;
	}

	@Data
	public static class GeneratedProject {
		private final String name;
		private final String artifactId;
	}
}
