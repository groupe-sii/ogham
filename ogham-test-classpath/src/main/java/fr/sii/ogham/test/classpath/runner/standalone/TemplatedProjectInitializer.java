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
import fr.sii.ogham.test.classpath.core.exception.ProjectInitializationException;
import fr.sii.ogham.test.classpath.core.facet.Facet;
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

	@Override
	public Project<StandaloneProjectParams> initialize(Path parentFolder, String identifier, StandaloneProjectParams variables) throws ProjectInitializationException {
		try {
			Path generatedProjectPath = parentFolder.resolve(identifier);
			String resourceFolder = "/standalone/" + variables.getBuildTool().name().toLowerCase();
			// copy templated project
			copy("/common", generatedProjectPath);
			copy("/standalone/src", generatedProjectPath.resolve("src"));
			copy(resourceFolder, generatedProjectPath);
			// evaluate templates
			write(resourceFolder + "/pom.xml.ftl", new TemplateModel(new GeneratedProject(identifier, identifier), oghamProperties.getOghamVersion(), generateActiveFacets(variables)), generatedProjectPath.resolve("pom.xml"));
			Project<StandaloneProjectParams> project = new Project<>(generatedProjectPath, variables);
			// add additional dependencies
			dependencyAdder.addDependencies(project, variables.getAdditionalDependencies());
			return project;
		} catch (IOException | TemplateException | AddDependencyException e) {
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
