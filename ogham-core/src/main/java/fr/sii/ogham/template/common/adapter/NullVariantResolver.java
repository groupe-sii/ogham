package fr.sii.ogham.template.common.adapter;

import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.resource.path.ResolvedPath;
import fr.sii.ogham.template.exception.VariantResolutionException;

public class NullVariantResolver implements VariantResolver {

	@Override
	public ResolvedPath getRealPath(TemplateContent template) throws VariantResolutionException {
		return null;
	}

	@Override
	public boolean variantExists(TemplateContent template) {
		return false;
	}

}
