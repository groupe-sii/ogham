package fr.sii.ogham.test.classpath.springboot;

import static fr.sii.ogham.test.classpath.matrix.MatrixUtils.expand;
import static fr.sii.ogham.test.classpath.springboot.SpringBootDependency.CONFIGURATION_PROCESSOR;
import static fr.sii.ogham.test.classpath.springboot.SpringBootDependency.DEVTOOLS;
import static fr.sii.ogham.test.classpath.springboot.SpringBootDependency.LOMBOK;
import static fr.sii.ogham.test.classpath.springboot.SpringBootDependency.WEB;
import static java.util.Arrays.asList;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import fr.sii.ogham.test.classpath.core.BuildTool;
import fr.sii.ogham.test.classpath.core.JavaVersion;
import fr.sii.ogham.test.classpath.core.Project;
import fr.sii.ogham.test.classpath.core.ProjectInitializer;
import fr.sii.ogham.test.classpath.core.dependency.Dependency;
import fr.sii.ogham.test.classpath.core.dependency.DependencyAdder;
import fr.sii.ogham.test.classpath.core.exception.AddDependencyException;
import fr.sii.ogham.test.classpath.core.exception.ProjectInitializationException;
import fr.sii.ogham.test.classpath.ogham.OghamDependency;
import fr.sii.ogham.test.classpath.ogham.OghamProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SpringBootProjectRunner implements ApplicationRunner {
	private static final List<SpringBootDependency> STANDARD_BOOT_DEPS = asList(CONFIGURATION_PROCESSOR, DEVTOOLS, LOMBOK, WEB);

	@Autowired
	ProjectInitializer projectInitializer;

	@Autowired
	DependencyAdder dependencyAdder;

	@Autowired
	SpringMatrixProperties springMatrixProperties;

	@Autowired
	OghamProperties oghamProperties;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		Path parentFolder = Paths.get(args.getNonOptionArgs().get(0));
		Files.createDirectories(parentFolder);
		FileUtils.cleanDirectory(parentFolder.toFile());
		List<String> modules = createProjects(parentFolder);
		log.info("Creating root project");
		createRootPom(parentFolder, modules);
		log.info("Projects created");
		System.exit(0);
	}

	private void createRootPom(Path parentFolder, List<String> modules) throws IOException, XmlPullParserException {
		Path rootPom = parentFolder.resolve("pom.xml");
		Files.copy(getClass().getResourceAsStream("/root-pom.xml"), rootPom);
		Model pom = read(rootPom);
		for (String module : modules) {
			pom.addModule(module);
		}
		write(rootPom, pom);
	}

	private List<String> createProjects(Path parentFolder) throws InterruptedException, ExecutionException {
		List<Future<String>> futures = new ArrayList<>();
		CompletionService<String> service = new ExecutorCompletionService<>(Executors.newFixedThreadPool(8));
		List<SpringBootProjectParams> expandedMatrix = generateSringBootMatrix();
		for (SpringBootProjectParams params : expandedMatrix) {
			service.submit(() -> createProject(parentFolder, params));
		}
		for(int i=0 ; i<expandedMatrix.size() ; i++) {
			futures.add(service.take());
		}
		List<String> modules = new ArrayList<>();
		for(Future<String> future : futures) {
			modules.add(future.get());
		}
		return modules;
	}

	private String createProject(Path parentFolder, SpringBootProjectParams params) throws ProjectInitializationException, AddDependencyException, IOException {
		String identifier = generateIdentifier(params);
		log.info("Creating project {}", identifier);
		Project project = projectInitializer.initialize(parentFolder, identifier, params);
		Path testResourcesFolder = project.getPath().resolve("src/test/resources");
		Files.createDirectories(testResourcesFolder);
		Files.copy(getClass().getResourceAsStream("/application-for-projects.properties"), testResourcesFolder.resolve("application.properties"));
		Files.copy(getClass().getResourceAsStream("/logback-for-projects.xml"), testResourcesFolder.resolve("logback-test.xml"));
		dependencyAdder.addDependencies(project, params.getOghamDependencies());
		return identifier;
	}

	private Model read(Path rootPom) throws IOException, XmlPullParserException {
		try (FileReader fileReader = new FileReader(rootPom.toFile())) {
			MavenXpp3Reader reader = new MavenXpp3Reader();
			return reader.read(fileReader);
		}
	}

	private void write(Path rootPom, Model model) throws IOException {
		MavenXpp3Writer writer = new MavenXpp3Writer();
		writer.write(new FileWriter(rootPom.toFile()), model);
	}

	private List<SpringBootProjectParams> generateSringBootMatrix() {
		List<SpringBootProjectParams> expanded = new ArrayList<>();
		for (JavaVersion javaVersion : springMatrixProperties.getJavaVersions()) {
			for (BuildTool buildTool : springMatrixProperties.getBuild()) {
				for (String bootVersion : springMatrixProperties.getSpringBootVersion()) {
					for (List<SpringBootDependency> springDeps : expand(springMatrixProperties.getSpringBootDependencies())) {
						for (List<Dependency> oghamDeps : expandOghamDependencies(springMatrixProperties.getOghamDependencies())) {
							expanded.add(new SpringBootProjectParams(javaVersion, buildTool, bootVersion, getDependencies(springDeps), oghamDeps));
						}
					}
				}
			}
		}
		return expanded;
	}

	private List<SpringBootDependency> getDependencies(List<SpringBootDependency> deps) {
		List<SpringBootDependency> dependencies = new ArrayList<>();
		dependencies.addAll(STANDARD_BOOT_DEPS);
		dependencies.addAll(deps);
		return dependencies;
	}

	private List<List<Dependency>> expandOghamDependencies(List<OghamDependency> oghamDependencies) {
		List<List<OghamDependency>> expanded = expand(oghamDependencies);
		List<List<Dependency>> deps = new ArrayList<>();
		for(List<OghamDependency> ods : expanded) {
			ArrayList<Dependency> d = new ArrayList<>();
			deps.add(d);
			for(OghamDependency od : ods) {
				d.add(od.toDependency(oghamProperties.getOghamVersion()));
			}
		}
		return deps;
	}

	private String generateIdentifier(SpringBootProjectParams params) {
		// @formatter;off
		return params.getJavaVersion().name().toLowerCase().replace("_", "") + "." 
				+ params.getBuildTool().name().toLowerCase() + "." 
				+ "boot-" + params.getSpringBootVersion()+"."
				+ params.getSpringBootDependencies().stream().filter(d -> !STANDARD_BOOT_DEPS.contains(d)).map(SpringBootDependency::getModule).collect(Collectors.joining("_")) + "."
				+ params.getOghamDependencies().stream().map(Dependency::getArtifactId).map(a -> a.replace("ogham-spring-boot-", "")).collect(Collectors.joining("_"));
		// @formatter:on
	}

}
