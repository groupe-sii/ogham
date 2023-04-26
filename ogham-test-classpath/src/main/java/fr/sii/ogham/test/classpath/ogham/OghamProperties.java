package fr.sii.ogham.test.classpath.ogham;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Component
@ConfigurationProperties
@Validated
public class OghamProperties {
	@NotNull
	@NotEmpty
	private String oghamVersion;
}
