package fr.sii.ogham.test.classpath.maven;

import fr.sii.ogham.test.classpath.core.dependency.Dependency;

public class MavenDependencyUtil {

    public static org.apache.maven.model.Dependency convert(Dependency dep) {
        org.apache.maven.model.Dependency dependency = new org.apache.maven.model.Dependency();
        dependency.setGroupId(dep.getGroupId());
        dependency.setArtifactId(dep.getArtifactId());
        dependency.setVersion(dep.getVersion());
        dependency.setScope(dep.getScope().getValue());
        dependency.setType(dep.getType());
        return dependency;
    }

    public static boolean isSameDependency(org.apache.maven.model.Dependency mavenDep, Dependency newDep) {
        return mavenDep.getArtifactId().equals(newDep.getArtifactId())
                && mavenDep.getGroupId().equals(newDep.getGroupId())
                && isSameVersion(mavenDep, newDep)
                && isSameType(mavenDep, newDep);
    }

    public static boolean isSameVersion(org.apache.maven.model.Dependency mavenDep, Dependency newDep) {
        if (mavenDep.getVersion()==null) {
            return newDep.getVersion()==null;
        }
        return mavenDep.getVersion().equals(newDep.getVersion());
    }

    public static boolean isSameType(org.apache.maven.model.Dependency mavenDep, Dependency newDep) {
        if (mavenDep.getType()==null) {
            return newDep.getType()==null;
        }
        return mavenDep.getType().equals(newDep.getType());
    }

}
