package fr.aba.ogham.documentation.generator.helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class ReadHelper {
	public String getContent(Path file) throws IOException {
		return Files.readAllLines(file).stream()
				.filter(this::skipHeaderLine)
				.collect(Collectors.joining("\n"));
	}
	
	private boolean skipHeaderLine(String line) {
		return !(line.startsWith(":relative-path:") || line.startsWith("include::{doc-base-dir}/variables.adoc[]"));
	}
}
