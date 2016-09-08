package fr.sii.ogham.template.freemarker.adapter;

import java.io.IOException;

import fr.sii.ogham.core.resource.resolver.FileResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.template.exception.ResolverAdapterConfigurationException;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;

/**
 * Adapter that converts general {@link FileResolver} into Freemarker specific {@link FileTemplateLoader}.
 * 
 * @author Cyril Dejonghe
 *
 */
public class FileResolverAdapter extends AbstractFreemarkerTemplateLoaderOptionsAdapter implements TemplateLoaderAdapter {

	@Override
	public boolean supports(ResourceResolver resolver) {
		return resolver.getActualResourceResolver() instanceof fr.sii.ogham.core.resource.resolver.FileResolver;
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
