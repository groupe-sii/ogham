package fr.aba.ogham.documentation.generator.properties;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Data
@Validated
@Component
@ConfigurationProperties("generator.doc.source")
public class DocumentationSourceProperties {
	@NotNull
	private Path rootDirectory;
	private Path asciidocDirectory = Paths.get("src/docs/asciidoc/");
	private Path resourcesDirectory = Paths.get("src/docs/resources/");
	private Path imagesDirectory = Paths.get("src/docs/resources/images/");
	private Path readmeSource = Paths.get("general/readme.adoc");
	private Path readmeOutput = Paths.get("README.adoc");
	private Path variables = Paths.get("variables.adoc");
}
