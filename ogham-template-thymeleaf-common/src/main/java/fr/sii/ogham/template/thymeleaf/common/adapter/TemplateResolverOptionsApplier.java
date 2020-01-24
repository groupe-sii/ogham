package fr.sii.ogham.template.thymeleaf.common.adapter;

import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.ogham.template.thymeleaf.common.TemplateResolverOptions;

/**
 * Class hierarchy has changed between Thymeleaf v2 and Thymeleaf v3. This
 * interface is used to make a facade for applying options to the template
 * resolver.
 * 
 * @author Aurélien Baudet
 *
 */
public interface TemplateResolverOptionsApplier {
	/**
	 * Apply the options on the resolver
	 * 
	 * @param resolver
	 *            the resolver to configure
	 * @param options
	 *            the options to apply
	 */
	void apply(ITemplateResolver resolver, TemplateResolverOptions options);
}
