package fr.sii.ogham.test.classpath.maven;

import fr.sii.ogham.test.classpath.core.Project;
import fr.sii.ogham.test.classpath.core.dependency.Dependency;
import fr.sii.ogham.test.classpath.core.dependency.DependencyAdder;
import fr.sii.ogham.test.classpath.core.exception.AddDependencyException;
import lombok.Data;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static fr.sii.ogham.test.classpath.maven.MavenDependencyUtil.*;

@Data
@Service
@Qualifier("dependencyManagementAdder")
public class AddMavenDependencyManagementDependency implements DependencyAdder {
	
	public void addDependencies(Project<?> project, List<Dependency> dependencies) throws AddDependencyException {
		addDependencies(project, dependencies, false);
	}

	@Override
	public void addDependencies(Project<?> project, List<Dependency> dependencies, boolean skipSameDepWithDifferentScope) throws AddDependencyException {
		Model model = read(project);
		for(Dependency dep : dependencies) {
			if (alreadyContainsExactSameDependency(model, dep)) {
				continue;
			}
			if (skipSameDepWithDifferentScope && alreadyContainsDependencyWitDifferentScope(model, dep)) {
				continue;
			}
			getDependencyManagement(model).addDependency(convert(dep));
		}
		write(project, model);
	}

	private static DependencyManagement getDependencyManagement(Model model) {
		DependencyManagement dependencyManagement = model.getDependencyManagement();
		if (dependencyManagement == null) {
			dependencyManagement = new DependencyManagement();
			model.setDependencyManagement(dependencyManagement);
		}
		return dependencyManagement;
	}

	private Model read(Project<?> project) throws AddDependencyException {
		try(FileReader fileReader = new FileReader(project.getPath().resolve("pom.xml").toFile())) {
			MavenXpp3Reader reader = new MavenXpp3Reader();
			return reader.read(fileReader);
		} catch (IOException | XmlPullParserException e) {
			throw new AddDependencyException("Failed to read pom.xml", e);
		}
	}

	private void write(Project<?> project, Model model) throws AddDependencyException {
		try {
			MavenXpp3Writer writer = new MavenXpp3Writer();
			writer.write(new FileWriter(project.getPath().resolve("pom.xml").toFile()), model);
		} catch(IOException e) {
			throw new AddDependencyException("Failed to write pom.xml", e);
		}
	}

	private boolean alreadyContainsExactSameDependency(Model model, Dependency dep) {
		return model.getDependencies().stream().anyMatch(mavenDep -> isSameDependency(mavenDep, dep));
	}

	private boolean alreadyContainsDependencyWitDifferentScope(Model model, Dependency dep) {
		return model.getDependencies().stream().anyMatch(mavenDep -> isSameDependencyIgnoringScope(mavenDep, dep));
	}
}
