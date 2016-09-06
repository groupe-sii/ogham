package fr.sii.ogham.template.thymeleaf.adapter;

import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.ogham.core.resource.resolver.RelativeResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;

/**
 * Adapter that converts general
 * {@link fr.sii.ogham.core.resource.resolver.FileResolver} into Thymeleaf
 * specific {@link FileTemplateResolver}.
 * 
 * @author Aurélien Baudet
 *
 */
public class FileResolverAdapter extends AbstractSimpleThymeleafResolverAdapter implements ThymeleafResolverAdapter {

	@Override
	public boolean supports(ResourceResolver resolver) {
		return resolver instanceof fr.sii.ogham.core.resource.resolver.FileResolver
				|| (resolver instanceof RelativeResolver && ((RelativeResolver) resolver).getDelegate() instanceof fr.sii.ogham.core.resource.resolver.FileResolver);
	}

	@Override
	public ITemplateResolver adapt(ResourceResolver resolver) {
		FileTemplateResolver templateResolver = new FileTemplateResolver();
		templateResolver.setPrefix(getParentPath());
		templateResolver.setSuffix(getExtension());
		return templateResolver;
	}

}
