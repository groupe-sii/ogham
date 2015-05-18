package fr.sii.notification.template.thymeleaf.adapter;

import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.notification.core.template.resolver.RelativeTemplateResolver;
import fr.sii.notification.core.template.resolver.TemplateResolver;

public class FileResolverAdapter implements ThymeleafResolverAdapter {

	@Override
	public boolean supports(TemplateResolver resolver) {
		return resolver instanceof fr.sii.notification.core.template.resolver.FileTemplateResolver || 
				(resolver instanceof RelativeTemplateResolver && ((RelativeTemplateResolver) resolver).getDelegate() instanceof fr.sii.notification.core.template.resolver.FileTemplateResolver);
	}

	@Override
	public ITemplateResolver adapt(TemplateResolver resolver) {
		// TODO: manage all other options
		FileTemplateResolver thymeleafResolver = new FileTemplateResolver();
//		if(resolver instanceof RelativeTemplateResolver) {
//			RelativeTemplateResolver relativeTemplateResolver = (RelativeTemplateResolver) resolver;
//			thymeleafResolver.setPrefix(relativeTemplateResolver.getPrefix());
//			thymeleafResolver.setSuffix(relativeTemplateResolver.getSuffix());
//		}
		return thymeleafResolver;
	}

}
