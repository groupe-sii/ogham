package fr.aba.ogham.documentation.generator.properties;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Data
@Validated
@Component
@ConfigurationProperties("generator.github")
public class GithubProperties {
//	@NotNull
//	private String latestReleaseBranch;
	@NotNull
//	private String futureDevBranch = "master";
	private String currentBranch = "master";
//	private String badgesBranch = "master";
	private String codeBaseUrl = "https://github.com/groupe-sii/ogham/tree/";
	private String siteUrl = "https://groupe-sii.github.io/ogham";
}