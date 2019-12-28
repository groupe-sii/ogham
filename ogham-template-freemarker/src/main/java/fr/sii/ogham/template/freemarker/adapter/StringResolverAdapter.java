package fr.sii.ogham.template.freemarker.adapter;

import fr.sii.ogham.core.resource.resolver.DelegateResourceResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.resource.resolver.StringResourceResolver;
import freemarker.cache.TemplateLoader;

/**
 * Adapter that converts general
 * {@link fr.sii.ogham.core.resource.resolver.StringResourceResolver} into
 * FreeMarker specific {@link StringContentTemplateLoader}.
 * 
 * @author Cyril Dejonghe
 *
 */
public class StringResolverAdapter extends AbstractFreeMarkerTemplateLoaderOptionsAdapter {

	@Override
	public boolean supports(ResourceResolver resolver) {
		ResourceResolver actualResolver = resolver instanceof DelegateResourceResolver ? ((DelegateResourceResolver) resolver).getActualResourceResolver() : resolver;
		return actualResolver instanceof StringResourceResolver;
	}

	@Override
	public TemplateLoader adapt(ResourceResolver resolver) {
		return new StringContentTemplateLoader();
	}
	
}
