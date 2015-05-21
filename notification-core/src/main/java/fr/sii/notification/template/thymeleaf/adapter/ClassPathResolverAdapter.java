package fr.sii.notification.template.thymeleaf.adapter;

import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.notification.core.template.resolver.ClassPathTemplateResolver;
import fr.sii.notification.core.template.resolver.RelativeTemplateResolver;
import fr.sii.notification.core.template.resolver.TemplateResolver;

/**
 * Adapter that converts general {@link ClassPathTemplateResolver} into
 * Thymeleaf specific {@link ClassLoaderTemplateResolver}.
 * 
 * @author Aurélien Baudet
 *
 */
public class ClassPathResolverAdapter implements ThymeleafResolverAdapter {
	@Override
	public boolean supports(TemplateResolver resolver) {
		return resolver instanceof ClassPathTemplateResolver
				|| (resolver instanceof RelativeTemplateResolver && ((RelativeTemplateResolver) resolver).getDelegate() instanceof ClassPathTemplateResolver);
	}

	@Override
	public ITemplateResolver adapt(TemplateResolver resolver) {
		// TODO: manage all other options
		FixClassLoaderTemplateResolver thymeleafResolver = new FixClassLoaderTemplateResolver();
		return thymeleafResolver;
	}

}
