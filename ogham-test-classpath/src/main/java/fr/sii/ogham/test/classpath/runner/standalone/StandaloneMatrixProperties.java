package fr.sii.ogham.test.classpath.runner.standalone;

import static fr.sii.ogham.test.classpath.matrix.MatrixUtils.expand;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.sii.ogham.test.classpath.core.property.Property;
import fr.sii.ogham.test.classpath.runner.common.CommonMatrixProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import fr.sii.ogham.test.classpath.core.BuildTool;
import fr.sii.ogham.test.classpath.core.JavaVersion;
import fr.sii.ogham.test.classpath.core.dependency.Dependency;
import fr.sii.ogham.test.classpath.ogham.OghamDependency;
import lombok.Data;

@Data
public class StandaloneMatrixProperties extends CommonMatrixProperties {

}
