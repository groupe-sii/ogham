package fr.sii.ogham.test.classpath.maven;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.stereotype.Service;

import fr.sii.ogham.test.classpath.core.Project;
import fr.sii.ogham.test.classpath.core.dependency.Dependency;
import fr.sii.ogham.test.classpath.core.dependency.DependencyAdder;
import fr.sii.ogham.test.classpath.core.exception.AddDependencyException;
import lombok.Data;

@Service
@Data
public class AddMavenDependency implements DependencyAdder {
	
	public void addDependencies(Project<?> project, List<Dependency> dependencies) throws AddDependencyException {
		addDependencies(project, dependencies, false);
	}

	@Override
	public void addDependencies(Project<?> project, List<Dependency> dependencies, boolean skipSameDepWithDifferentScope) throws AddDependencyException {
		Model model = read(project);
		for(Dependency dep : dependencies) {
			if (skipSameDepWithDifferentScope && alreadyContainsDependency(model, dep)) {
				continue;
			}
			model.addDependency(convert(dep));
		}
		write(project, model);
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

	private org.apache.maven.model.Dependency convert(Dependency dep) {
		org.apache.maven.model.Dependency dependency = new org.apache.maven.model.Dependency();
		dependency.setGroupId(dep.getGroupId());
		dependency.setArtifactId(dep.getArtifactId());
		dependency.setVersion(dep.getVersion());
		dependency.setScope(dep.getScope().getValue());
		return dependency;
	}

	private boolean alreadyContainsDependency(Model model, Dependency dep) {
		return model.getDependencies().stream().anyMatch(mavenDep -> isSameDependency(mavenDep, dep));
	}
	
	private boolean isSameDependency(org.apache.maven.model.Dependency mavenDep, Dependency newDep) {
		return mavenDep.getArtifactId().equals(newDep.getArtifactId())
				&& mavenDep.getGroupId().equals(newDep.getGroupId())
				&& mavenDep.getVersion().equals(newDep.getVersion());
	}
}
