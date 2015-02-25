package fr.sii.notification.core.template.resolver;

import fr.sii.notification.core.template.SimpleTemplate;
import fr.sii.notification.core.template.Template;

public class ClassPathTemplateResolver implements TemplateResolver {
	@Override
	public Template getTemplate(String templatePath) {
		return new SimpleTemplate(getClass().getResourceAsStream(templatePath));
	}

}
