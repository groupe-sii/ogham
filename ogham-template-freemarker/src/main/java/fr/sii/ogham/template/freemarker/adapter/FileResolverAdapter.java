package fr.sii.ogham.template.freemarker.adapter;

import java.io.IOException;

import fr.sii.ogham.core.resource.resolver.DelegateResourceResolver;
import fr.sii.ogham.core.resource.resolver.FileResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.template.exception.ResolverAdapterConfigurationException;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;

/**
 * Adapter that converts general {@link FileResolver} into FreeMarker specific {@link FileTemplateLoader}.
 * 
 * @author Cyril Dejonghe
 *
 */
public class FileResolverAdapter extends AbstractFreeMarkerTemplateLoaderOptionsAdapter implements TemplateLoaderAdapter {

	@Override
	public boolean supports(ResourceResolver resolver) {
		ResourceResolver actualResolver = resolver instanceof DelegateResourceResolver ? ((DelegateResourceResolver) resolver).getActualResourceResolver() : resolver;
		return actualResolver instanceof fr.sii.ogham.core.resource.resolver.FileResolver;
	}

	@Override
	public TemplateLoader adapt(ResourceResolver resolver) throws ResolverAdapterConfigurationException {
		TemplateLoader templateLoader;
		try {
			templateLoader = new FileTemplateLoader(null, true);
		} catch (IOException e) {
			throw new ResolverAdapterConfigurationException("Invalid configuration for " + FileTemplateLoader.class.getSimpleName(), resolver, e);
		}

		return templateLoader;
	}

}
