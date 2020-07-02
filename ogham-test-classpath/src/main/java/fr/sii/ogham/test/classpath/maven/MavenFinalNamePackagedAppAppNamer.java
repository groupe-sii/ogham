package fr.sii.ogham.test.classpath.maven;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.stereotype.Service;

import fr.sii.ogham.test.classpath.core.Project;
import fr.sii.ogham.test.classpath.core.exception.PackagedAppNameException;
import fr.sii.ogham.test.classpath.core.packaging.PackagedAppNamer;
import lombok.Data;

@Service
@Data
public class MavenFinalNamePackagedAppAppNamer implements PackagedAppNamer {

	@Override
	public void setPackagedAppName(Project<?> project, String newName) throws PackagedAppNameException {
		Model model = read(project);
		model.getBuild().setFinalName(newName);
		write(project, model);
	}

	private Model read(Project<?> project) throws PackagedAppNameException {
		try(FileReader fileReader = new FileReader(project.getPath().resolve("pom.xml").toFile())) {
			MavenXpp3Reader reader = new MavenXpp3Reader();
			return reader.read(fileReader);
		} catch (IOException | XmlPullParserException e) {
			throw new PackagedAppNameException("Failed to read pom.xml", e);
		}
	}

	private void write(Project<?> project, Model model) throws PackagedAppNameException {
		try {
			MavenXpp3Writer writer = new MavenXpp3Writer();
			writer.write(new FileWriter(project.getPath().resolve("pom.xml").toFile()), model);
		} catch(IOException e) {
			throw new PackagedAppNameException("Failed to write pom.xml", e);
		}
	}

}
