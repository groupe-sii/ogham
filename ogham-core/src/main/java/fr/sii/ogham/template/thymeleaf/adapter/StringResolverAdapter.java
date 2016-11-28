package fr.sii.ogham.template.thymeleaf.adapter;

import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.ogham.core.resource.resolver.DelegateResourceResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.template.thymeleaf.templateresolver.StringTemplateResolver;

/**
 * Adapter that converts general
 * {@link fr.sii.ogham.core.resource.resolver.StringResourceResolver} into
 * Thymeleaf specific {@link StringTemplateResolver}.
 * 
 * @author Aurélien Baudet
 *
 */
public class StringResolverAdapter extends AbstractTemplateResolverOptionsAdapter implements TemplateResolverAdapter {

	@Override
	public boolean supports(ResourceResolver resolver) {
		ResourceResolver actualResolver = resolver instanceof DelegateResourceResolver ? ((DelegateResourceResolver) resolver).getActualResourceResolver() : resolver;
		return actualResolver instanceof fr.sii.ogham.core.resource.resolver.StringResourceResolver;
	}

	@Override
	public ITemplateResolver adapt(ResourceResolver resolver) {
		StringTemplateResolver templateResolver = new StringTemplateResolver();
		applyOptions(templateResolver);
		return templateResolver;
	}

}
