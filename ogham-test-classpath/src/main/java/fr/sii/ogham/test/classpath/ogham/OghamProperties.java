package fr.sii.ogham.test.classpath.ogham;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Data
@Component
@ConfigurationProperties
@Validated
public class OghamProperties {
	@NotNull
	@NotEmpty
	private String oghamVersion;
}
