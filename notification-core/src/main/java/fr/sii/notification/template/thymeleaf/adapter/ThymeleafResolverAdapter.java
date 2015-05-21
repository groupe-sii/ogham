package fr.sii.notification.template.thymeleaf.adapter;

import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.notification.core.template.resolver.TemplateResolver;
import fr.sii.notification.template.exception.NoResolverAdapterException;

/**
 * Adapter that provides the Thymeleaf specific resolver from the general
 * template resolver.
 * 
 * @author Aurélien Baudet
 *
 */
public interface ThymeleafResolverAdapter {
	/**
	 * Is the adapter can handle the general resolver.
	 * 
	 * @param resolver
	 *            the resolver to check if the implementation can handle it
	 * @return true if the adapter can handle the resolver, false otherwise
	 */
	public boolean supports(TemplateResolver resolver);

	/**
	 * Adapt the general template resolver into the Thymeleaf specific resolver.
	 * 
	 * @param resolver
	 *            the general resolver
	 * @return the Thymeleaf specific resolver
	 * @throws NoResolverAdapterException
	 *             when no resolver could handle the resolver
	 */
	public ITemplateResolver adapt(TemplateResolver resolver) throws NoResolverAdapterException;
}
