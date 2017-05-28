package fr.sii.ogham.spring.template;

import org.springframework.boot.context.properties.ConfigurationProperties;

import fr.sii.ogham.spring.common.PrefixSuffixProperties;

@ConfigurationProperties("ogham.template")
public class OghamCommonTemplateProperties extends PrefixSuffixProperties {

}
