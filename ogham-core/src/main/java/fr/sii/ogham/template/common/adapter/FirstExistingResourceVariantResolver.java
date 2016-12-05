package fr.sii.ogham.template.common.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.message.capability.HasVariant;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.message.content.Variant;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.template.exception.VariantResolutionException;

/**
 * Try every possible path for the variant until one path points to an existing
 * resource.
 * 
 * @author Aurélien Baudet
 *
 */
public class FirstExistingResourceVariantResolver implements VariantResolver {
	private ResourceResolver resolver;
	private List<VariantResolver> delegates;

	public FirstExistingResourceVariantResolver(ResourceResolver resolver, VariantResolver... delegates) {
		this(resolver, new ArrayList<>(Arrays.asList(delegates)));
	}

	public FirstExistingResourceVariantResolver(ResourceResolver resolver, List<VariantResolver> delegates) {
		super();
		this.resolver = resolver;
		this.delegates = delegates;
	}

	@Override
	public String getRealPath(TemplateContent template) throws VariantResolutionException {
		if (template instanceof HasVariant) {
			for (VariantResolver delegate : delegates) {
				try {
					String realPath = delegate.getRealPath(template);
					resolver.getResource(realPath);
					return realPath;
				} catch (ResourceResolutionException e) {
					// just skip the exception
				}
			}
			Variant variant = ((HasVariant) template).getVariant();
			throw new VariantResolutionException("Failed to resolve variant (" + variant + ")", template.getPath(), template.getContext(), variant);
		} else {
			return template.getPath();
		}
	}

}
