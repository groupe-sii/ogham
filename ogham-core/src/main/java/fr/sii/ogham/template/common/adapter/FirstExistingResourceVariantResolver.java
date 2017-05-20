package fr.sii.ogham.template.common.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.core.message.capability.HasVariant;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.template.exception.VariantResolutionException;

/**
 * Try every possible path for the variant until one path points to an existing
 * resource.
 * 
 * @author Aurélien Baudet
 *
 */
public class FirstExistingResourceVariantResolver implements VariantResolver {
	private List<VariantResolver> delegates;
	private VariantResolver defaultResolver;

	public FirstExistingResourceVariantResolver(VariantResolver defaultResolver, VariantResolver... delegates) {
		this(defaultResolver, new ArrayList<>(Arrays.asList(delegates)));
	}

	public FirstExistingResourceVariantResolver(VariantResolver defaultResolver, List<VariantResolver> delegates) {
		super();
		this.defaultResolver = defaultResolver;
		this.delegates = delegates;
	}

	@Override
	public String getRealPath(TemplateContent template) throws VariantResolutionException {
		if (template instanceof HasVariant) {
			for (VariantResolver delegate : delegates) {
				if(delegate.variantExists(template)) {
					return delegate.getRealPath(template);
				}
			}
			return defaultResolver.getRealPath(template);
		} else {
			return template.getPath();
		}
	}

	@Override
	public boolean variantExists(TemplateContent template) {
		if (template instanceof HasVariant) {
			for (VariantResolver delegate : delegates) {
				if(delegate.variantExists(template)) {
					return true;
				}
			}
			return defaultResolver.variantExists(template);
		} else {
			return false;
		}
	}

	public FirstExistingResourceVariantResolver addVariantResolver(VariantResolver variantResolver) {
		delegates.add(variantResolver);
		return this;
	}
}
