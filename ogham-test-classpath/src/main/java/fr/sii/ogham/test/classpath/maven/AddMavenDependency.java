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
	
	public void addDependencies(Project project, List<Dependency> dependencies) throws AddDependencyException {
		Model model = read(project);
		for(Dependency dep : dependencies) {
			model.addDependency(convert(dep));
		}
		write(project, model);
	}

	private Model read(Project project) throws AddDependencyException {
		try(FileReader fileReader = new FileReader(project.getPath().resolve("pom.xml").toFile())) {
			MavenXpp3Reader reader = new MavenXpp3Reader();
			return reader.read(fileReader);
		} catch (IOException | XmlPullParserException e) {
			throw new AddDependencyException("Failed to read pom.xml", e);
		}
	}

	private void write(Project project, Model model) throws AddDependencyException {
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
}
