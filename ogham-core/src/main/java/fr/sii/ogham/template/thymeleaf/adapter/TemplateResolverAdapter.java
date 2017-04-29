package fr.sii.ogham.template.thymeleaf.adapter;

import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.template.exception.NoResolverAdapterException;
import fr.sii.ogham.template.thymeleaf.TemplateResolverOptions;

/**
 * Adapter that provides the Thymeleaf specific {@link ITemplateResolver} from
 * the general {@link ResourceResolver}.
 * 
 * @author Aurélien Baudet
 *
 */
public interface TemplateResolverAdapter {
	/**
	 * Is the adapter can handle the general resolver.
	 * 
	 * @param resolver
	 *            the resolver to check if the implementation can handle it
	 * @return true if the adapter can handle the resolver, false otherwise
	 */
	boolean supports(ResourceResolver resolver);

	/**
	 * Adapts the general template resolver into the Thymeleaf specific
	 * resolver.
	 * 
	 * @param resolver
	 *            the general resolver
	 * @return the Thymeleaf specific resolver
	 * @throws NoResolverAdapterException
	 *             when no resolver could handle the resolver
	 */
	ITemplateResolver adapt(ResourceResolver resolver) throws NoResolverAdapterException;

	/**
	 * Sets the options for the adapted {@link ITemplateResolver}.
	 * 
	 * @param options
	 *            the resolver options
	 */
	void setOptions(TemplateResolverOptions options);

}
