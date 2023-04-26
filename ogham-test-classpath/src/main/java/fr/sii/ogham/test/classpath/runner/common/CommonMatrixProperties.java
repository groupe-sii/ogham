package fr.sii.ogham.test.classpath.runner.common;

import fr.sii.ogham.test.classpath.core.BuildTool;
import fr.sii.ogham.test.classpath.core.JavaVersion;
import fr.sii.ogham.test.classpath.core.dependency.Dependency;
import fr.sii.ogham.test.classpath.core.property.Property;
import fr.sii.ogham.test.classpath.core.repository.Repository;
import fr.sii.ogham.test.classpath.ogham.OghamDependency;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static fr.sii.ogham.test.classpath.matrix.MatrixUtils.expand;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Data
public class CommonMatrixProperties {
    private List<String> java;
    private List<BuildTool> build;
    private Map<String, String> buildToolProperties = new LinkedHashMap<>();
    private List<String> additionalDependencies;
    private List<String> dependencyManagement;
    private List<String> oghamDependencies;
    private List<String> repositories;

    public List<List<OghamDependency>> getExpandedOghamDependencies() {
        List<List<OghamDependency>> deps = new ArrayList<>();
        for(String dep : oghamDependencies) {
            List<OghamDependency> oghamDeps = new ArrayList<>();
            deps.add(oghamDeps);
            if(!dep.isEmpty()) {
                for(String d : expand(dep)) {
                    oghamDeps.add(OghamDependency.fromArtifactName(d));
                }
            }
        }
        return deps;
    }

    public List<JavaVersion> getJavaVersions() {
        List<JavaVersion> javaVersions = new ArrayList<>();
        for(String version : java) {
            javaVersions.add(JavaVersion.fromVersion(version));
        }
        return javaVersions;
    }

    public List<Dependency> getAdditionalDependencies() {
        if (additionalDependencies == null) {
            return emptyList();
        }
        return additionalDependencies.stream()
                .map(Dependency::from)
                .collect(toList());
    }

    public List<Dependency> getDependencyManagementDependencies() {
        if (dependencyManagement == null) {
            return emptyList();
        }
        return dependencyManagement.stream()
                .map(Dependency::from)
                .collect(toList());
    }

    public List<Property> getBuildProperties() {
        return buildToolProperties.entrySet().stream()
                .map((e) -> new Property(e.getKey(), e.getValue()))
                .collect(toList());
    }

    public List<Repository> getRepositories() {
        if (repositories == null) {
            return emptyList();
        }
        return repositories.stream()
                .map(Repository::new)
                .collect(toList());
    }
}
