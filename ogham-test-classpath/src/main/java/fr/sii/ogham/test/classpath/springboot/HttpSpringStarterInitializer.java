package fr.sii.ogham.test.classpath.springboot;

import static org.springframework.http.HttpHeaders.USER_AGENT;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import fr.sii.ogham.test.classpath.core.Project;
import fr.sii.ogham.test.classpath.core.ProjectInitializer;
import fr.sii.ogham.test.classpath.core.ProjectVariables;
import fr.sii.ogham.test.classpath.core.exception.ProjectInitializationException;
import fr.sii.ogham.test.classpath.ogham.OghamProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

@Data
@Slf4j
public class HttpSpringStarterInitializer implements ProjectInitializer {
	private final RestTemplate restTemplate;
	private final SpringStarterProperties springStarterProperties;
	private final OghamProperties oghamProperties;

	@Override
	public Project initialize(Path parentFolder, String identifier, ProjectVariables variables) throws ProjectInitializationException {
		SpringBootProjectParams v = (SpringBootProjectParams) variables;

		// @formatter:off
		String url = springStarterProperties.getUrl()
				+ "?name={artifactId}"
				+ "&groupId=fr.sii"
				+ "&artifactId={artifactId}"
				+ "&version={version}"
				+ "&description="
				+ "&packageName=fr.sii.spring.boot.runtime.testing"
				+ "&type={type}"
				+ "&packaging=jar"
				+ "&javaVersion={javaVersion}"
				+ "&language=java"
				+ "&bootVersion={springBootVersion}";
		// @formatter:on
		for (SpringBootDependency dependency : v.getSpringBootDependencies()) {
			url += "&dependencies=" + dependency.getModule();
		}
		UriTemplate template = new UriTemplate(url);
		URI expanded = template.expand(identifier,
				identifier,
				oghamProperties.getOghamVersion(),
				v.getBuildTool().getType(),
				v.getJavaVersion().getVersion(),
				v.getSpringBootVersion());
		log.debug("Starter resolved url: {}", expanded);
		RequestEntity<Void> request = RequestEntity.get(expanded)
										.header(USER_AGENT, "ogham/"+oghamProperties.getOghamVersion())
										.build();
		ResponseEntity<byte[]> response = restTemplate.exchange(request, byte[].class);
		if(response.getStatusCode().is2xxSuccessful()) {
			try {
				return new Project(unzip(response.getBody(), identifier, parentFolder), variables);
			} catch(IOException | ZipException | RuntimeException e) {
				throw new ProjectInitializationException("Failed to initialize Spring Boot project while trying to unzip Spring starter zip", e);
			}
		}
		throw new ProjectInitializationException("Failed to download Spring starter zip");
	}

	private Path unzip(byte[] content, String identifier, Path parentFolder) throws IOException, ZipException {
		Path zipFile = Files.createTempFile("spring-starter", "zip");
		Files.write(zipFile, content);
		Path extractFolder = parentFolder.resolve(identifier);
		extractFolder.toFile().mkdirs();
		ZipFile zip = new ZipFile(zipFile.toFile());
		zip.extractAll(extractFolder.toString());
		return extractFolder;
	}

}
