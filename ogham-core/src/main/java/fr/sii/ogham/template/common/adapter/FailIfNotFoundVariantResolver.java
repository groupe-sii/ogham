package fr.sii.ogham.template.common.adapter;

import fr.sii.ogham.core.message.capability.HasVariant;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.message.content.Variant;
import fr.sii.ogham.template.exception.VariantResolutionException;

public class FailIfNotFoundVariantResolver implements VariantResolver {

	@Override
	public String getRealPath(TemplateContent template) throws VariantResolutionException {
		if (!(template instanceof HasVariant)) {
			return template.getPath();
		}
		Variant variant = ((HasVariant) template).getVariant();
		throw new VariantResolutionException("Failed to resolve variant (" + variant + ")", template.getPath(), template.getContext(), variant);
	}

	/**
	 * Returns true to make it fail
	 */
	@Override
	public boolean variantExists(TemplateContent template) {
		return true;
	}

}
