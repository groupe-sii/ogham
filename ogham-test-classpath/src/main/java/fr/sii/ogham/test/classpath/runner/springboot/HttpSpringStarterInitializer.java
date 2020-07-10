package fr.sii.ogham.test.classpath.runner.springboot;

import static org.springframework.http.HttpHeaders.USER_AGENT;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import fr.sii.ogham.test.classpath.core.Project;
import fr.sii.ogham.test.classpath.core.ProjectInitializer;
import fr.sii.ogham.test.classpath.core.exception.ProjectInitializationException;
import fr.sii.ogham.test.classpath.ogham.OghamProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

@Data
@Slf4j
public class HttpSpringStarterInitializer implements ProjectInitializer<SpringBootProjectParams> {
	private final RestTemplate restTemplate;
	private final SpringStarterProperties springStarterProperties;
	private final OghamProperties oghamProperties;

	@Override
	public Project<SpringBootProjectParams> initialize(Path parentFolder, String identifier, SpringBootProjectParams variables) throws ProjectInitializationException {
		URI url = generateUrl(identifier, variables);
		ResponseEntity<byte[]> response = getGeneratedProject(url);
		if(response.getStatusCode().is2xxSuccessful()) {
			try {
				return new Project<>(unzip(response.getBody(), identifier, parentFolder), variables);
			} catch(IOException | ZipException | RuntimeException e) {
				throw new ProjectInitializationException("Failed to initialize Spring Boot project while trying to unzip Spring starter zip", e);
			}
		}
		throw new ProjectInitializationException("Failed to download Spring starter zip");
	}

	private URI generateUrl(String identifier, SpringBootProjectParams variables) {
		// @formatter:off
		String url = springStarterProperties.getUrl()
				+ "?name={artifactId}"
				+ "&groupId=fr.sii"
				+ "&artifactId={artifactId}"
				+ "&version={version}"
				+ "&description="
				+ "&packageName=fr.sii.springboot.runtime.testing"
				+ "&type={type}"
				+ "&packaging=jar"
				+ "&javaVersion={javaVersion}"
				+ "&language=java"
				+ "&bootVersion={springBootVersion}";
		// @formatter:on
		for (SpringBootDependency dependency : variables.getSpringBootDependencies()) {
			url += "&dependencies=" + dependency.getModule();
		}
		UriTemplate template = new UriTemplate(url);
		return template.expand(identifier,
				identifier,
				oghamProperties.getOghamVersion(),
				variables.getBuildTool().getType(),
				variables.getJavaVersion().getVersion(),
				variables.getSpringBootVersion());
	}
	
	private ResponseEntity<byte[]> getGeneratedProject(URI url) throws ProjectInitializationException {
		try {
			log.debug("Starter resolved url: {}", url);
			RequestEntity<Void> request = RequestEntity.get(url)
											.header(USER_AGENT, "ogham/"+oghamProperties.getOghamVersion())
											.build();
			return restTemplate.exchange(request, byte[].class);
		} catch(RestClientException e) {
			throw new ProjectInitializationException("Request to download Spring Boot zip failed", e);
		}
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
