package fr.sii.ogham.test.classpath.maven;

import static java.util.stream.Collectors.joining;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.stereotype.Service;

import fr.sii.ogham.test.classpath.core.Project;
import fr.sii.ogham.test.classpath.core.exception.AddFacetException;
import fr.sii.ogham.test.classpath.core.facet.Facet;
import fr.sii.ogham.test.classpath.core.facet.FacetAdder;
import lombok.Data;

@Data
@Service
public class AddFacetsThroughMavenTestPlugins implements FacetAdder {

	@Override
	public void addFacet(Project<?> project, List<Facet> facets) throws AddFacetException {
		Model model = read(project);
		model.getBuild().addPlugin(configurePlugin(new Plugin(), "maven-surefire-plugin", facets));
		model.getBuild().addPlugin(configurePlugin(new Plugin(), "maven-failsafe-plugin", facets));
		write(project, model);
	}


	private Plugin configurePlugin(Plugin surefire, String pluginName, List<Facet> facets) {
		surefire.setGroupId("org.apache.maven.plugins");
		surefire.setArtifactId(pluginName);
		surefire.setVersion("2.22.2");
		Xpp3Dom configuration = new Xpp3Dom("configuration");
		Xpp3Dom systemPropertiesVariables = new Xpp3Dom("systemPropertyVariables");
		Xpp3Dom activeFacets = new Xpp3Dom("activeFacets");
		configuration.addChild(systemPropertiesVariables);
		systemPropertiesVariables.addChild(activeFacets);
		activeFacets.setValue(facets.stream()
				.distinct()
				.map(Facet::getFacetName)
				.collect(joining(",")));
		surefire.setConfiguration(configuration);
		return surefire;
	}
	
	private Model read(Project<?> project) throws AddFacetException {
		try(FileReader fileReader = new FileReader(project.getPath().resolve("pom.xml").toFile())) {
			MavenXpp3Reader reader = new MavenXpp3Reader();
			return reader.read(fileReader);
		} catch (IOException | XmlPullParserException e) {
			throw new AddFacetException("Failed to read pom.xml", e);
		}
	}

	private void write(Project<?> project, Model model) throws AddFacetException {
		try {
			MavenXpp3Writer writer = new MavenXpp3Writer();
			writer.write(new FileWriter(project.getPath().resolve("pom.xml").toFile()), model);
		} catch(IOException e) {
			throw new AddFacetException("Failed to write pom.xml", e);
		}
	}
}
