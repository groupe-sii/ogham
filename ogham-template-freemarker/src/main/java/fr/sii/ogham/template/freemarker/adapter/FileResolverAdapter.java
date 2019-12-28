package fr.sii.ogham.template.freemarker.adapter;

import java.io.File;
import java.io.IOException;

import fr.sii.ogham.core.resource.resolver.DelegateResourceResolver;
import fr.sii.ogham.core.resource.resolver.FileResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.template.exception.ResolverAdapterConfigurationException;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;

/**
 * Adapter that converts general {@link FileResolver} into FreeMarker specific
 * {@link FileTemplateLoader}.
 * 
 * @author Cyril Dejonghe
 *
 */
public class FileResolverAdapter extends AbstractFreeMarkerTemplateLoaderOptionsAdapter {
	private final File baseDir;
	
	public FileResolverAdapter() {
		this(new File("/"));
	}

	public FileResolverAdapter(File baseDir) {
		super();
		this.baseDir = baseDir;
	}

	@Override
	public boolean supports(ResourceResolver resolver) {
		ResourceResolver actualResolver = resolver instanceof DelegateResourceResolver ? ((DelegateResourceResolver) resolver).getActualResourceResolver() : resolver;
		return actualResolver instanceof FileResolver;
	}

	@Override
	public TemplateLoader adapt(ResourceResolver resolver) throws ResolverAdapterConfigurationException {
		try {
			return new FileTemplateLoader(baseDir, true);
		} catch (IOException e) {
			throw new ResolverAdapterConfigurationException("Invalid configuration for " + FileTemplateLoader.class.getSimpleName(), resolver, e);
		}
	}

}
