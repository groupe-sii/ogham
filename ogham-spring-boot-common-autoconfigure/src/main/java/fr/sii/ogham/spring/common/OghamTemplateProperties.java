package fr.sii.ogham.spring.common;

public interface OghamTemplateProperties {
	MessageSpecificFreemarkerProperties getFreemarker();
	MessageSpecificThymeleafProperties getThymeleaf();
	MessageSpecificTemplateProperties getTemplate();
}
