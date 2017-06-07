package fr.sii.ogham.test.classpath.springboot;

import static fr.sii.ogham.test.classpath.springboot.IdentifierGenerator.generateIdentifier;
import static fr.sii.ogham.test.classpath.springboot.IdentifierGenerator.isIdentifierForJavaVersion;
import static fr.sii.ogham.test.classpath.springboot.SpringBootDependency.CONFIGURATION_PROCESSOR;
import static fr.sii.ogham.test.classpath.springboot.SpringBootDependency.DEVTOOLS;
import static fr.sii.ogham.test.classpath.springboot.SpringBootDependency.LOMBOK;
import static java.util.Arrays.asList;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
	public static final List<SpringBootDependency> STANDARD_BOOT_DEPS = asList(CONFIGURATION_PROCESSOR, DEVTOOLS, LOMBOK);

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
		run(args.getNonOptionArgs().get(0), args.getOptionValues("override")!=null);
	}

	public void run(String parentFolderPath, boolean override) throws IOException, InterruptedException, ExecutionException, XmlPullParserException, ProjectInitializationException, AddDependencyException {
		Path parentFolder = Paths.get(parentFolderPath);
		if(isSkip(override, parentFolder)) {
			log.info("Skipping creation of projects because projects already exist");
			System.exit(0);
		}
		Files.createDirectories(parentFolder);
		FileUtils.cleanDirectory(parentFolder.toFile());
		List<String> modules = createProjectsParallel(parentFolder);
		for(JavaVersion javaVersion : springMatrixProperties.getJavaVersions()) {
			log.info("Creating root project for {}", javaVersion);
			createRootPom(parentFolder.resolve(javaVersion.name()), filter(modules, javaVersion));
		}
		log.info("{} projects created", modules.size());
		System.exit(0);
	}

	private boolean isSkip(boolean override, Path parentFolder) {
		return !override && parentFolder.toFile().exists() && hasContent(parentFolder);
	}

	private boolean hasContent(Path parentFolder) {
		File f = parentFolder.toFile();
		return f.isDirectory() && f.list().length>0;
	}

	private List<String> filter(List<String> modules, JavaVersion javaVersion) {
		List<String> filteredModules = new ArrayList<>();
		for(String module : modules) {
			if(isIdentifierForJavaVersion(module, javaVersion)) {
				filteredModules.add(module);
			}
		}
		return filteredModules;
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

	private List<String> createProjectsParallel(final Path parentFolder) throws InterruptedException, ExecutionException {
		List<Future<String>> futures = new ArrayList<>();
		CompletionService<String> service = new ExecutorCompletionService<>(Executors.newFixedThreadPool(8));
		List<SpringBootProjectParams> expandedMatrix = generateSringBootMatrix();
		for (final SpringBootProjectParams params : expandedMatrix) {
			service.submit(new Callable<String>() {
				@Override
				public String call() throws Exception {
					return createProject(parentFolder, params);
				}
			});
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

	private List<String> createProjects(final Path parentFolder) throws InterruptedException, ExecutionException, ProjectInitializationException, AddDependencyException, IOException {
		List<SpringBootProjectParams> expandedMatrix = generateSringBootMatrix();
		List<String> modules = new ArrayList<>();
		for (final SpringBootProjectParams params : expandedMatrix) {
			modules.add(createProject(parentFolder, params));
		}
		return modules;
	}

	private String createProject(Path parentFolder, SpringBootProjectParams params) throws ProjectInitializationException, AddDependencyException, IOException {
		String identifier = generateIdentifier(params, STANDARD_BOOT_DEPS);
		log.info("Creating project {}", identifier);
		Project project = projectInitializer.initialize(parentFolder.resolve(params.getJavaVersion().name()), identifier, params);
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
					for (List<SpringBootDependency> springDeps : springMatrixProperties.getExpandedSpringBootDependencies()) {
						for (Dependency oghamDeps : getOghamDependencies(springMatrixProperties.getOghamDependencies())) {
							expanded.add(new SpringBootProjectParams(javaVersion, buildTool, bootVersion, getDependencies(springDeps), asList(oghamDeps)));
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

	private List<Dependency> getOghamDependencies(List<OghamDependency> oghamDependencies) {
		List<Dependency> deps = new ArrayList<>();
		for(OghamDependency od : oghamDependencies) {
			deps.add(od.toDependency(oghamProperties.getOghamVersion()));
		}
		return deps;
	}

}
