package fr.sii.notification.template.thymeleaf.adapter;

import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.notification.core.template.resolver.TemplateResolver;
import fr.sii.notification.template.exception.NoResolverAdapter;

public interface ThymeleafResolverAdapter {
	public boolean supports(TemplateResolver resolver);
	
	public ITemplateResolver adapt(TemplateResolver resolver) throws NoResolverAdapter;
}
