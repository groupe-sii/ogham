package fr.sii.ogham.test.classpath.runner.standalone;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import fr.sii.ogham.test.classpath.core.Project;
import fr.sii.ogham.test.classpath.core.ProjectInitializer;
import fr.sii.ogham.test.classpath.core.exception.ProjectInitializationException;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TemplatedProjectInitializer implements ProjectInitializer<StandaloneProjectParams> {
	private final Configuration freemarkerConfig;

	@Override
	public Project<StandaloneProjectParams> initialize(Path parentFolder, String identifier, StandaloneProjectParams variables) throws ProjectInitializationException {
		try {
			String resourceFolder = "/standalone/" + variables.getBuildTool().name().toLowerCase();
			Path generatedProjectPath = parentFolder.resolve(identifier);
			// copy templated project
			copy(resourceFolder, generatedProjectPath);
			// evaluate templates
			write(resourceFolder + "/pom.xml.ftl", new TemplateModel(new GeneratedProject(identifier, identifier)), generatedProjectPath.resolve("pom.xml"));
			return new Project<>(generatedProjectPath, variables);
		} catch (IOException | TemplateException e) {
			log.error("Failed to initialize project {}: {}", identifier, e.getMessage());
			throw new ProjectInitializationException("Failed to initialize project " + identifier, e);
		}
	}

	private void copy(String resourceFolder, Path generatedProjectPath) throws IOException {
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = resolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resourceFolder + "/**");
		for (Resource resource : resources) {
			if (resource.exists() && resource.isReadable() && resource.contentLength() > 0) {
				URL url = resource.getURL();
				String urlString = url.toExternalForm();
				String targetName = urlString.substring(urlString.indexOf(resourceFolder) + resourceFolder.length() + 1);
				Path destination = generatedProjectPath.resolve(targetName);
				Files.createDirectories(destination.getParent());
				Files.copy(resource.getInputStream(), destination);
			}
		}
	}

	private void write(String template, Object model, Path out) throws IOException, TemplateException {
		try (FileWriter writer = new FileWriter(out.toFile())) {
			freemarkerConfig.getTemplate(template).process(model, writer);
		}
	}

	@Data
	public static class TemplateModel {
		private final GeneratedProject project;
	}

	@Data
	public static class GeneratedProject {
		private final String name;
		private final String artifactId;
	}
}
