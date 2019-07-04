package fr.aba.ogham.documentation.generator.properties;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Data
@Validated
@Component
@ConfigurationProperties("generator.ogham")
public class OghamProperties {
	@NotNull
	private String latestReleaseVersion;
	@NotNull
	private String futureDevVersion;
}
