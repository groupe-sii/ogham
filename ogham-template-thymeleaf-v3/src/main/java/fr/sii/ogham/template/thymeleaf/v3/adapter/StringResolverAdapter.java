package fr.sii.ogham.template.thymeleaf.v3.adapter;

import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import fr.sii.ogham.core.resource.resolver.DelegateResourceResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.template.thymeleaf.common.adapter.AbstractTemplateResolverOptionsAdapter;
import fr.sii.ogham.template.thymeleaf.common.adapter.TemplateResolverOptionsApplier;

/**
 * Adapter that converts general
 * {@link fr.sii.ogham.core.resource.resolver.StringResourceResolver} into
 * Thymeleaf specific {@link StringTemplateResolver}.
 * 
 * @author Aurélien Baudet
 *
 */
public class StringResolverAdapter extends AbstractTemplateResolverOptionsAdapter {

	public StringResolverAdapter(TemplateResolverOptionsApplier optionsSetter) {
		super(optionsSetter);
	}

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
