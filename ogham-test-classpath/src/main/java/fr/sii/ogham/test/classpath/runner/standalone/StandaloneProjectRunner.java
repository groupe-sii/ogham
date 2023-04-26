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

import fr.sii.ogham.test.classpath.runner.common.NamedMatricesProperties;
import fr.sii.ogham.test.classpath.runner.exception.SkipRunException;
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
	NamedMatricesProperties matricesProperties;

	@Autowired
	ProjectsCreator<StandaloneProjectParams, OghamDependency> projectsCreator;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		try {
			checkShouldRun();
			run(args.getNonOptionArgs().get(0), args.getOptionValues("override")!=null);
		} catch(SkipRunException e) {
			log.info(e.getMessage());
		} catch(Exception e) {
			log.error("Failed to create standalone projects", e);
			System.exit(1);
		}
	}

	private void checkShouldRun() throws SkipRunException {
		if (!matricesProperties.hasStandaloneMatrices()) {
			throw new SkipRunException("No standalone matrix configured. Skipping");
		}
	}

	public void run(String parentFolderPath, boolean override) throws IOException, InterruptedException, ExecutionException, XmlPullParserException, ProjectInitializationException, AddDependencyException, SingleProjectCreationException {
		Path parentFolder = Paths.get(parentFolderPath);
		log.info("Generating standalone projects...");
		List<String> modules = projectsCreator.createProjects(parentFolder, override, generateStandaloneMatrix(), emptyList());
		addModules(parentFolder, getStandaloneMatrix().getJavaVersions(), modules);
		addMavenWrapper(parentFolder, getStandaloneMatrix().getJavaVersions());
		log.info("{} standalone projects created", modules.size());
	}


	private List<StandaloneProjectParams> generateStandaloneMatrix() {
		List<StandaloneProjectParams> expanded = new ArrayList<>();
		StandaloneMatrixProperties matrix = getStandaloneMatrix();
		for (JavaVersion javaVersion : matrix.getJavaVersions()) {
			for (BuildTool buildTool : matrix.getBuild()) {
				for (List<OghamDependency> oghamDeps : matrix.getExpandedOghamDependencies()) {
					expanded.add(new StandaloneProjectParams(
							javaVersion,
							buildTool,
							oghamDeps,
							matrix.getAdditionalDependencies(),
							matrix.getBuildProperties(),
							matrix.getRepositories()));
				}
			}
		}
		return expanded;
	}

	private StandaloneMatrixProperties getStandaloneMatrix() {
		return matricesProperties.getStandaloneMatrix();
	}
}
