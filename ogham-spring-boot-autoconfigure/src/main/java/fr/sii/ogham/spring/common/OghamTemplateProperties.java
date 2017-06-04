package fr.sii.ogham.spring.common;

public interface OghamTemplateProperties {
	OghamResolutionProperties getFreemarker();
	OghamResolutionProperties getThymeleaf();
	OghamResolutionProperties getTemplate();
}
