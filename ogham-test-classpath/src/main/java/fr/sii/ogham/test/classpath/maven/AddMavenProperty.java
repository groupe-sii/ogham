package fr.sii.ogham.test.classpath.maven;

import fr.sii.ogham.test.classpath.core.Project;
import fr.sii.ogham.test.classpath.core.dependency.Dependency;
import fr.sii.ogham.test.classpath.core.exception.AddPropertyException;
import fr.sii.ogham.test.classpath.core.property.Property;
import fr.sii.ogham.test.classpath.core.property.PropertyAdder;
import lombok.Data;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Data
@Service
public class AddMavenProperty implements PropertyAdder {
    @Override
    public void addProperties(Project<?> project, List<Property> properties) throws AddPropertyException {
        Model model = read(project);
        for(Property prop : properties) {
            model.addProperty(prop.getKey(), prop.getValue());
        }
        write(project, model);
    }


    private Model read(Project<?> project) throws AddPropertyException {
        try(FileReader fileReader = new FileReader(project.getPath().resolve("pom.xml").toFile())) {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            return reader.read(fileReader);
        } catch (IOException | XmlPullParserException e) {
            throw new AddPropertyException("Failed to read pom.xml", e);
        }
    }

    private void write(Project<?> project, Model model) throws AddPropertyException {
        try {
            MavenXpp3Writer writer = new MavenXpp3Writer();
            writer.write(new FileWriter(project.getPath().resolve("pom.xml").toFile()), model);
        } catch(IOException e) {
            throw new AddPropertyException("Failed to write pom.xml", e);
        }
    }
}
