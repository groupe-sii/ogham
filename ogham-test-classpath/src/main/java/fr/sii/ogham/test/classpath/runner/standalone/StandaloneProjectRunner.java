package fr.sii.ogham.test.classpath.runner.standalone;

import static fr.sii.ogham.test.classpath.runner.util.RunnerUtils.addMavenWrapper;
import static fr.sii.ogham.test.classpath.runner.util.RunnerUtils.addModules;
import static java.util.Collections.emptyList;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import fr.sii.ogham.test.classpath.core.BuildTool;
import fr.sii.ogham.test.classpath.core.JavaVersion;
import fr.sii.ogham.test.classpath.core.exception.AddDependencyException;
import fr.sii.ogham.test.classpath.core.exception.ProjectInitializationException;
import fr.sii.ogham.test.classpath.ogham.OghamDependency;
import fr.sii.ogham.test.classpath.ogham.OghamProperties;
import fr.sii.ogham.test.classpath.runner.common.ProjectsCreator;
import fr.sii.ogham.test.classpath.runner.common.SingleProjectCreationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(2)
public class StandaloneProjectRunner implements ApplicationRunner {

	@Autowired
	OghamProperties oghamProperties;

	@Autowired
	StandaloneMatrixProperties standaloneMatrixProperties;

	@Autowired
	ProjectsCreator<StandaloneProjectParams, OghamDependency> projectsCreator;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		try {
			run(args.getNonOptionArgs().get(0), args.getOptionValues("override")!=null);
		} catch (Exception e) {
			log.error("Failed to create standalone projects", e);
			System.exit(1);
		}
	}
	
	public void run(String parentFolderPath, boolean override) throws IOException, InterruptedException, ExecutionException, XmlPullParserException, ProjectInitializationException, AddDependencyException, SingleProjectCreationException {
		Path parentFolder = Paths.get(parentFolderPath);
		log.info("Generating standalone projects...");
		List<String> modules = projectsCreator.createProjects(parentFolder, override, generateStandaloneMatrix(), emptyList());
		addModules(parentFolder, standaloneMatrixProperties.getJavaVersions(), modules);
		addMavenWrapper(parentFolder, standaloneMatrixProperties.getJavaVersions());
		log.info("{} standalone projects created", modules.size());
	}


	private List<StandaloneProjectParams> generateStandaloneMatrix() {
		List<StandaloneProjectParams> expanded = new ArrayList<>();
		for (JavaVersion javaVersion : standaloneMatrixProperties.getJavaVersions()) {
			for (BuildTool buildTool : standaloneMatrixProperties.getBuild()) {
				for (List<OghamDependency> oghamDeps : standaloneMatrixProperties.getExpandedOghamDependencies()) {
					expanded.add(new StandaloneProjectParams(javaVersion, buildTool, oghamDeps, standaloneMatrixProperties.getAdditionalDependencies()));
				}
			}
		}
		return expanded;
	}
}
