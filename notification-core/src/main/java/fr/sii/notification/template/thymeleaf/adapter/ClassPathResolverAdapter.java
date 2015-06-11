package fr.sii.notification.template.thymeleaf.adapter;

import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.notification.core.resource.resolver.ClassPathResolver;
import fr.sii.notification.core.resource.resolver.RelativeResolver;
import fr.sii.notification.core.resource.resolver.ResourceResolver;

/**
 * Adapter that converts general {@link ClassPathResolver} into
 * Thymeleaf specific {@link ClassLoaderTemplateResolver}.
 * 
 * @author Aurélien Baudet
 *
 */
public class ClassPathResolverAdapter implements ThymeleafResolverAdapter {
	@Override
	public boolean supports(ResourceResolver resolver) {
		return resolver instanceof ClassPathResolver
				|| (resolver instanceof RelativeResolver && ((RelativeResolver) resolver).getDelegate() instanceof ClassPathResolver);
	}

	@Override
	public ITemplateResolver adapt(ResourceResolver resolver) {
		// TODO: manage all other options
		return new FixClassLoaderTemplateResolver();
	}

}
