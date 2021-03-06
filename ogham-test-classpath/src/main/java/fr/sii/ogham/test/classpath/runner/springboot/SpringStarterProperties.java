package fr.sii.ogham.test.classpath.runner.springboot;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Data
@Component
@Validated
@ConfigurationProperties("spring.initializer")
public class SpringStarterProperties {
	@NotNull
	@NotEmpty
	private String url;
}
