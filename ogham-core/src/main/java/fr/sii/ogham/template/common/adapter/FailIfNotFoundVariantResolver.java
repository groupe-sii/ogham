package fr.sii.ogham.template.common.adapter;

import fr.sii.ogham.core.message.capability.HasVariant;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.message.content.Variant;
import fr.sii.ogham.template.exception.VariantResolutionException;

public class FailIfNotFoundVariantResolver implements VariantResolver {

	@Override
	public String getRealPath(TemplateContent template) throws VariantResolutionException {
		Variant variant = ((HasVariant) template).getVariant();
		throw new VariantResolutionException("Failed to resolve variant (" + variant + ")", template.getPath(), template.getContext(), variant);
	}

	@Override
	public boolean variantExists(TemplateContent template) {
		// TODO Auto-generated method stub
		return false;
	}

}
