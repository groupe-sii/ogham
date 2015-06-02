package fr.sii.notification.template.thymeleaf.adapter;

import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.notification.core.template.resolver.RelativeTemplateResolver;
import fr.sii.notification.core.template.resolver.TemplateResolver;

/**
 * Adapter that converts general
 * {@link fr.sii.notification.core.template.resolver.StringTemplateResolver} into
 * Thymeleaf specific {@link StringTemplateResolver}.
 * 
 * @author Aurélien Baudet
 *
 */
public class StringResolverAdapter implements ThymeleafResolverAdapter {

	@Override
	public boolean supports(TemplateResolver resolver) {
		return resolver instanceof fr.sii.notification.core.template.resolver.StringTemplateResolver
				|| (resolver instanceof RelativeTemplateResolver && ((RelativeTemplateResolver) resolver).getDelegate() instanceof fr.sii.notification.core.template.resolver.StringTemplateResolver);
	}

	@Override
	public ITemplateResolver adapt(TemplateResolver resolver) {
		// TODO: manage all other options
		return new StringTemplateResolver();
	}

}
