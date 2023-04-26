package fr.sii.ogham.test.classpath.core.property;

import fr.sii.ogham.test.classpath.core.Project;
import fr.sii.ogham.test.classpath.core.exception.AddPropertyException;

import java.util.List;

public interface PropertyAdder {
    void addProperties(Project<?> project, List<Property> properties) throws AddPropertyException;
}
