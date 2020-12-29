package fr.aba.ogham.documentation.generator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import fr.aba.ogham.documentation.generator.helper.IncludeHelper;
import fr.aba.ogham.documentation.generator.helper.ReadHelper;
import fr.aba.ogham.documentation.generator.helper.RewriteHelper;
import fr.aba.ogham.documentation.generator.helper.VariablesHelper;
import fr.aba.ogham.documentation.generator.helper.VariablesHelper.Variables;
import fr.aba.ogham.documentation.generator.properties.DocumentationSourceProperties;
import fr.aba.ogham.documentation.generator.properties.GithubProperties;
import fr.aba.ogham.documentation.generator.properties.OghamProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class GenerateReadme implements ApplicationRunner {
	private static final String INFO = "////\nDo no edit this file, it is automatically generated. Sources are in src/docs/asciidoc.\n////\n\n";
	
	private final VariablesHelper variablesHelper;
	private final ReadHelper reader;
	private final IncludeHelper merger;
	private final RewriteHelper rewriter;
	private final GithubProperties githubProperties;
	private final DocumentationSourceProperties documentationProperties;
	private final OghamProperties oghamProperties;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		if(args.containsOption("readme")) {
			generate();
		}
	}

	private void generate() throws IOException {
		Path rootDirectory = documentationProperties.getRootDirectory();
		Path asciidocDirectory = rootDirectory.resolve(documentationProperties.getAsciidocDirectory());
		Path imagesDirectory = rootDirectory.resolve(documentationProperties.getImagesDirectory());
		Path sourceFile = asciidocDirectory.resolve(documentationProperties.getReadmeSource());
		Path variablesFile = asciidocDirectory.resolve(documentationProperties.getVariables());
		// load and override variables
		Variables variables = variablesHelper.loadVariables(variablesFile);
		variables.add("docdir", asciidocDirectory.toString());
		variables.add("sourcedir", rootDirectory.toString());
		variables.add("images-dir", rootDirectory.relativize(imagesDirectory).toString());
		variables.add("sourcedir-url", githubProperties.getCodeBaseUrl()+githubProperties.getCurrentBranch());
		variables.add("site-url", githubProperties.getSiteUrl());
		variables.add("ogham-version", oghamProperties.getCurrentVersion());
		variables.add("git-branch", githubProperties.getCurrentBranch());
		// load content, merge includes and rewrite some parts
		String content = reader.getContent(sourceFile);
		String out = merger.include(asciidocDirectory, content, variables, 0);
		out = variables.evaluate(out);
		out = variables.evaluate(rewriter.rewrite(out));
		out = INFO + out;
		// write content
		Path outFile = rootDirectory.resolve(documentationProperties.getReadmeOutput());
		try(BufferedWriter writer = Files.newBufferedWriter(outFile, StandardCharsets.UTF_8)) {
			writer.write(out);
			writer.flush();
		}
		log.info("Readme generated in {}", outFile);
	}

}
