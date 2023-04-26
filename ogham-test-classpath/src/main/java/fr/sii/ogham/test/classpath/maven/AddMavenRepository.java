package fr.sii.ogham.test.classpath.maven;

import fr.sii.ogham.test.classpath.core.Project;
import fr.sii.ogham.test.classpath.core.exception.AddRepositoryException;
import fr.sii.ogham.test.classpath.core.repository.Repository;
import fr.sii.ogham.test.classpath.core.repository.RepositoryAdder;
import lombok.Data;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

@Data
@Service
public class AddMavenRepository implements RepositoryAdder {
    @Override
    public void addRepositories(Project<?> project, List<Repository> repositories) throws AddRepositoryException {
        Model model = read(project);
        for(Repository repository : repositories) {
            model.addRepository(convert(repository));
        }
        write(project, model);
    }

    private org.apache.maven.model.Repository convert(Repository repository) {
        org.apache.maven.model.Repository repo = new org.apache.maven.model.Repository();
        repo.setId(generateIdFromUrl(repository.getUrl()));
        repo.setUrl(repository.getUrl());
        return repo;
    }

    private static String generateIdFromUrl(String url) {
        try {
            return new URL(url).getHost();
        } catch (MalformedURLException e) {
            return UUID.randomUUID().toString();
        }
    }


    private Model read(Project<?> project) throws AddRepositoryException {
        try(FileReader fileReader = new FileReader(project.getPath().resolve("pom.xml").toFile())) {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            return reader.read(fileReader);
        } catch (IOException | XmlPullParserException e) {
            throw new AddRepositoryException("Failed to read pom.xml", e);
        }
    }

    private void write(Project<?> project, Model model) throws AddRepositoryException {
        try {
            MavenXpp3Writer writer = new MavenXpp3Writer();
            writer.write(new FileWriter(project.getPath().resolve("pom.xml").toFile()), model);
        } catch(IOException e) {
            throw new AddRepositoryException("Failed to write pom.xml", e);
        }
    }
}
