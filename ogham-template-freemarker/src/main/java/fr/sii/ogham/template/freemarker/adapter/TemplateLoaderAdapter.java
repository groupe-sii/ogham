package fr.sii.ogham.template.freemarker.adapter;

import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.template.exception.NoResolverAdapterException;
import fr.sii.ogham.template.exception.ResolverAdapterConfigurationException;
import fr.sii.ogham.template.freemarker.TemplateLoaderOptions;
import freemarker.cache.TemplateLoader;

/**
 * Adapter that provides the FreeMarker specific {@link TemplateLoader} from the general {@link ResourceResolver}.
 * 
 * @author Cyril Dejonghe
 *
 */
public interface TemplateLoaderAdapter {
	/**
	 * Is the adapter able to handle the general resolver.
	 * 
	 * @param resolver
	 *            the resolver to check
	 * @return true if the adapter can handle the resolver, false otherwise
	 */
	boolean supports(ResourceResolver resolver);

	/**
	 * Adapts the general template resolver into the specific template loader.
	 * 
	 * @param resolver
	 *            the general resolver
	 * @return the specific {@link TemplateLoader}
	 * @throws NoResolverAdapterException
	 *             when no adapter could handle the resolver
	 * @throws ResolverAdapterConfigurationException
	 *             when a adapter was found but its configuration was invalid
	 */
	TemplateLoader adapt(ResourceResolver resolver) throws NoResolverAdapterException, ResolverAdapterConfigurationException;

	/**
	 * Sets the options for the adapted {@link TemplateLoaderOptions}
	 * 
	 * @param options
	 *            template loader options
	 */
	void setOptions(TemplateLoaderOptions options);

}
