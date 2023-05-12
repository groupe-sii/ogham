package fr.sii.ogham.test.classpath.runner.springboot;

import java.util.List;

import fr.sii.ogham.test.classpath.core.BuildTool;
import fr.sii.ogham.test.classpath.core.JavaVersion;
import fr.sii.ogham.test.classpath.core.ProjectVariables;
import fr.sii.ogham.test.classpath.core.dependency.Dependency;
import fr.sii.ogham.test.classpath.core.property.Property;
import fr.sii.ogham.test.classpath.core.repository.Repository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpringBootProjectParams implements ProjectVariables {
	private JavaVersion javaVersion;
	private BuildTool buildTool;
	private String springBootVersion;
	@Singular("addDependency")
	private List<SpringBootDependency> springBootDependencies;
	private List<OghamResolvedDependency> oghamDependencies;
	private List<Dependency> additionalDependencies;
	private List<Property> buildProperties;
	private List<Dependency> dependencyManagementDependencies;
	private List<Repository> repositories;
	private String identifierSuffix;
}
