package fr.sii.ogham.test.classpath.springboot;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
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
