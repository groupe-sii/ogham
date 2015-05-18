package fr.sii.notification.template.thymeleaf.adapter;

import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.notification.core.template.resolver.ClassPathTemplateResolver;
import fr.sii.notification.core.template.resolver.RelativeTemplateResolver;
import fr.sii.notification.core.template.resolver.TemplateResolver;

public class ClassPathResolverAdapter implements ThymeleafResolverAdapter {
	@Override
	public boolean supports(TemplateResolver resolver) {
		return resolver instanceof ClassPathTemplateResolver || 
				(resolver instanceof RelativeTemplateResolver && ((RelativeTemplateResolver) resolver).getDelegate() instanceof ClassPathTemplateResolver);
	}

	@Override
	public ITemplateResolver adapt(TemplateResolver resolver) {
		// TODO: manage all other options
		FixClassLoaderTemplateResolver thymeleafResolver = new FixClassLoaderTemplateResolver();
//		if(resolver instanceof RelativeTemplateResolver) {
//			RelativeTemplateResolver relativeTemplateResolver = (RelativeTemplateResolver) resolver;
//			String prefix = relativeTemplateResolver.getPrefix();
//			thymeleafResolver.setPrefix(prefix.startsWith("/") ? prefix.substring(1) : prefix);
//			thymeleafResolver.setSuffix(relativeTemplateResolver.getSuffix());
//		}
		return thymeleafResolver;
	}

}
