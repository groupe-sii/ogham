package fr.sii.ogham.test.classpath.runner.standalone;

import java.util.List;

import fr.sii.ogham.test.classpath.core.BuildTool;
import fr.sii.ogham.test.classpath.core.JavaVersion;
import fr.sii.ogham.test.classpath.core.ProjectVariables;
import fr.sii.ogham.test.classpath.core.dependency.Dependency;
import fr.sii.ogham.test.classpath.ogham.OghamDependency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StandaloneProjectParams implements ProjectVariables {
	private JavaVersion javaVersion;
	private BuildTool buildTool;
	@Singular("addDependency")
	private List<OghamDependency> oghamDependencies;
	private List<Dependency> additionalDependencies;
}
