package fr.sii.ogham.test.classpath.runner.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class SourceUtils {
	public static void copy(String resourceFolder, Path generatedProjectPath) throws IOException {
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = resolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resourceFolder + "/**");
		for (Resource resource : resources) {
			if (resource.exists() && resource.isReadable() && resource.contentLength() > 0) {
				URL url = resource.getURL();
				String urlString = url.toExternalForm();
				String targetName = urlString.substring(urlString.indexOf(resourceFolder) + resourceFolder.length() + 1);
				Path destination = generatedProjectPath.resolve(targetName);
				Files.createDirectories(destination.getParent());
				try (InputStream source = resource.getInputStream()) {
					Files.copy(source, destination);
				}
			}
		}
	}


	private SourceUtils() {
		super();
	}
}
