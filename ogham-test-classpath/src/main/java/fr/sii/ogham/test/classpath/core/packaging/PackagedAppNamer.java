package fr.sii.ogham.test.classpath.core.packaging;

import fr.sii.ogham.test.classpath.core.Project;
import fr.sii.ogham.test.classpath.core.exception.PackagedAppNameException;

public interface PackagedAppNamer {
	public void setPackagedAppName(Project<?> project, String newName) throws PackagedAppNameException;
}
