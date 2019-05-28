package fr.sii.ogham.test.classpath.runner.springboot;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix="runner")
public class RunnerProperties {
	private boolean parallel = true;
	private int numThreads = 8;
}
