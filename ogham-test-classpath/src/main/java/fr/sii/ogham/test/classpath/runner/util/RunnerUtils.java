package fr.sii.ogham.test.classpath.runner.util;

import static fr.sii.ogham.test.classpath.runner.common.IdentifierGenerator.isIdentifierForJavaVersion;
import static java.nio.file.attribute.PosixFilePermission.GROUP_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.GROUP_READ;
import static java.nio.file.attribute.PosixFilePermission.OTHERS_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OTHERS_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import fr.sii.ogham.test.classpath.core.JavaVersion;
import fr.sii.ogham.test.classpath.runner.common.IdentifierGenerator;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RunnerUtils {

	public static boolean isSkip(boolean override, Path folder) {
		return !override && folder.toFile().exists() && hasContent(folder);
	}

	public static void addModules(Path parentFolder, List<JavaVersion> javaVersions, List<String> modules) throws IOException, XmlPullParserException {
		for(JavaVersion javaVersion : javaVersions) {
			Path javaVersionPath = parentFolder.resolve(javaVersion.getDirectoryName());
			log.info("Creating root project for {} into {}", javaVersion, javaVersionPath);
			addRootPom(javaVersionPath);
			// Group generated modules and split into several sub-modules.
			// This is necessary since Travis CI fails due to too long job run.
			for (ModuleGroup submodule : group(filter(modules, javaVersion))) {
				Path submodulePath = javaVersionPath.resolve(submodule.getName());
				addRootPom(submodulePath, submodule.getName());
				addModulesToPom(submodulePath, submodule.getModules());
				addMavenWrapper(submodulePath);
				// add to java version root pom
				addModulesToPom(javaVersionPath, asList(submodule.getName()));
			}
		}
	}
	
	public static void addMavenWrapper(Path parentFolder, List<JavaVersion> javaVersions) throws IOException {
		for(JavaVersion javaVersion : javaVersions) {
			Path javaVersionPath = parentFolder.resolve(javaVersion.getDirectoryName());
			addMavenWrapper(javaVersionPath);
		}
	}

	private static boolean hasContent(Path folder) {
		File f = folder.toFile();
		if (!f.isDirectory()) {
			return false;
		}
		if (f.list() == null) {
			return false;
		}
		return f.list().length > 0;
	}

	
	private static List<ModuleGroup> group(List<String> modules) {
		return modules.stream()
				.collect(groupingBy(IdentifierGenerator::getGroupName))
				.entrySet().stream()
				.map(e -> new ModuleGroup(e.getKey(), e.getValue()))
				.collect(toList());
	}

	private static List<String> filter(List<String> modules, JavaVersion javaVersion) {
		List<String> filteredModules = new ArrayList<>();
		for(String module : modules) {
			if(isIdentifierForJavaVersion(module, javaVersion)) {
				filteredModules.add(module);
			}
		}
		return filteredModules;
	}
	
	private static void addRootPom(Path parentFolder) throws IOException {
		Path rootPom = parentFolder.resolve("pom.xml");
		if(rootPom.toFile().exists()) {
			return;
		}
		parentFolder.toFile().mkdirs();
		Files.copy(RunnerUtils.class.getResourceAsStream("/root-pom.xml"), rootPom);
	}

	private static void addRootPom(Path parentFolder, String name) throws IOException, XmlPullParserException {
		addRootPom(parentFolder);
		Path rootPom = parentFolder.resolve("pom.xml");
		Model pom = read(rootPom);
		pom.setArtifactId(name);
		write(rootPom, pom);
	}

	private static void addModulesToPom(Path parentFolder, List<String> modules) throws IOException, XmlPullParserException {
		Path rootPom = parentFolder.resolve("pom.xml");
		Model pom = read(rootPom);
		for (String module : modules) {
			if(!pom.getModules().contains(module)) {
				pom.addModule(module);
			}
		}
		write(rootPom, pom);
	}

	private static void addMavenWrapper(Path parentFolder) throws IOException {
		if(parentFolder.resolve("mvnw").toFile().exists()) {
			return;
		}
		Files.copy(RunnerUtils.class.getResourceAsStream("/mvnwrapper-for-projects/mvnw"), parentFolder.resolve("mvnw"));
		Files.setPosixFilePermissions(parentFolder.resolve("mvnw"), new HashSet<>(asList(GROUP_READ, GROUP_EXECUTE, OTHERS_READ, OTHERS_EXECUTE, OWNER_READ, OWNER_EXECUTE)));
		Files.copy(RunnerUtils.class.getResourceAsStream("/mvnwrapper-for-projects/mvnw.cmd"), parentFolder.resolve("mvnw.cmd"));
		parentFolder.resolve(".mvn/wrapper").toFile().mkdirs();
		Files.copy(RunnerUtils.class.getResourceAsStream("/mvnwrapper-for-projects/.mvn/wrapper/maven-wrapper.jar"), parentFolder.resolve(".mvn/wrapper/maven-wrapper.jar"));
		Files.copy(RunnerUtils.class.getResourceAsStream("/mvnwrapper-for-projects/.mvn/wrapper/maven-wrapper.properties"), parentFolder.resolve(".mvn/wrapper/maven-wrapper.properties"));
	}

	private static Model read(Path rootPom) throws IOException, XmlPullParserException {
		try (FileReader fileReader = new FileReader(rootPom.toFile())) {
			MavenXpp3Reader reader = new MavenXpp3Reader();
			return reader.read(fileReader);
		}
	}

	private static void write(Path rootPom, Model model) throws IOException {
		MavenXpp3Writer writer = new MavenXpp3Writer();
		writer.write(new FileWriter(rootPom.toFile()), model);
	}
	
	
	@Data
	private static class ModuleGroup {
		private final String name;
		private final List<String> modules;
	}

	private RunnerUtils() {
		super();
	}
}
