package fr.sii.ogham.test.classpath.runner.common;

import fr.sii.ogham.test.classpath.runner.springboot.SingleMatrixProperties;
import fr.sii.ogham.test.classpath.runner.springboot.SpringMatrixProperties;
import fr.sii.ogham.test.classpath.runner.standalone.StandaloneMatrixProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

import static java.util.Collections.emptyList;

@Data
@Component
@ConfigurationProperties("classpath-tests")
public class NamedMatricesProperties {
    private Map<String, SingleMatrixProperties> springMatrices;
    private Map<String, StandaloneMatrixProperties> standaloneMatrices;

    public boolean hasSpringMatrices() {
        return !getSpringMatrix().getMatrix().isEmpty();
    }

    public SpringMatrixProperties getSpringMatrix() {
        if (springMatrices == null) {
            return new SpringMatrixProperties(emptyList(), null);
        }
        return new SpringMatrixProperties(new ArrayList<>(springMatrices.values()), null);
    }

    public boolean hasStandaloneMatrices() {
        return getStandaloneMatrix() != null;
    }

    public StandaloneMatrixProperties getStandaloneMatrix() {
        if (standaloneMatrices == null) {
            return null;
        }
        // TODO: handle several different matrices ?
        return standaloneMatrices.values().stream()
                .findFirst()
                .orElse(null);
    }
}
