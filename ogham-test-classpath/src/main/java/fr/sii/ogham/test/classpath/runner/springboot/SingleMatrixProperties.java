package fr.sii.ogham.test.classpath.runner.springboot;

import static fr.sii.ogham.test.classpath.matrix.MatrixUtils.expand;

import java.util.ArrayList;
import java.util.List;

import fr.sii.ogham.test.classpath.runner.common.CommonMatrixProperties;

import lombok.Data;

@Data
public class SingleMatrixProperties extends CommonMatrixProperties {
	private List<String> springBootVersion;
	private List<String> springBootDependencies;
	private String identifierSuffix = "";

	public List<List<SpringBootDependency>> getExpandedSpringBootDependencies() {
		List<List<SpringBootDependency>> deps = new ArrayList<>();
		for(String dep : springBootDependencies) {
			List<SpringBootDependency> bootDeps = new ArrayList<>();
			deps.add(bootDeps);
			if(!dep.isEmpty()) {
				for(String d : expand(dep)) {
					bootDeps.add(SpringBootDependency.fromModule(d));
				}
			}
		}
		return deps;
	}

}
