package fr.sii.ogham.template.thymeleaf.adapter;

import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.ogham.core.resource.resolver.DelegateResourceResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;

/**
 * Adapter that converts general
 * {@link fr.sii.ogham.core.resource.resolver.FileResolver} into Thymeleaf
 * specific {@link FileTemplateResolver}.
 * 
 * @author Aurélien Baudet
 *
 */
public class FileResolverAdapter extends AbstractTemplateResolverOptionsAdapter implements TemplateResolverAdapter {

	@Override
	public boolean supports(ResourceResolver resolver) {
		ResourceResolver actualResolver = resolver instanceof DelegateResourceResolver ? ((DelegateResourceResolver) resolver).getActualResourceResolver() : resolver;
		return actualResolver instanceof fr.sii.ogham.core.resource.resolver.FileResolver;
	}

	@Override
	public ITemplateResolver adapt(ResourceResolver resolver) {
		FileTemplateResolver templateResolver = new FileTemplateResolver();
		applyOptions(templateResolver);
		return templateResolver;
	}

}
