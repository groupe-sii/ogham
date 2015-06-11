package fr.sii.notification.template.thymeleaf.adapter;

import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.notification.core.resource.resolver.RelativeResolver;
import fr.sii.notification.core.resource.resolver.ResourceResolver;

/**
 * Adapter that converts general
 * {@link fr.sii.notification.core.resource.resolver.FileResolver} into
 * Thymeleaf specific {@link FileTemplateResolver}.
 * 
 * @author Aurélien Baudet
 *
 */
public class FileResolverAdapter implements ThymeleafResolverAdapter {

	@Override
	public boolean supports(ResourceResolver resolver) {
		return resolver instanceof fr.sii.notification.core.resource.resolver.FileResolver
				|| (resolver instanceof RelativeResolver && ((RelativeResolver) resolver).getDelegate() instanceof fr.sii.notification.core.resource.resolver.FileResolver);
	}

	@Override
	public ITemplateResolver adapt(ResourceResolver resolver) {
		// TODO: manage all other options
		return new FileTemplateResolver();
	}

}
