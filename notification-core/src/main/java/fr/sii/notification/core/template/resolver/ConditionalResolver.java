package fr.sii.notification.core.template.resolver;

public interface ConditionalResolver extends TemplateResolver {
	public boolean supports(String templateName);
}
