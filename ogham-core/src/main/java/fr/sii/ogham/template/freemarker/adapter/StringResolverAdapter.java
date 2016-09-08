package fr.sii.ogham.template.freemarker.adapter;

import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;

/**
 * Adapter that converts general {@link fr.sii.ogham.core.resource.resolver.StringResourceResolver} into FreeMarker specific {@link StringTemplateLoader}.
 * 
 * @author Cyril Dejonghe
 *
 */
public class StringResolverAdapter extends AbstractFreeMarkerTemplateLoaderOptionsAdapter implements TemplateLoaderAdapter {

	@Override
	public boolean supports(ResourceResolver resolver) {
		return resolver.getActualResourceResolver() instanceof fr.sii.ogham.core.resource.resolver.StringResourceResolver;

	}

	@Override
	public TemplateLoader adapt(ResourceResolver resolver) {
		StringTemplateLoader templateResolver = new StringTemplateLoader();
		return templateResolver;
	}

}
