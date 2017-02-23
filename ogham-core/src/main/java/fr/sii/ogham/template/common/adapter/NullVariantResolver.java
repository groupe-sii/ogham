package fr.sii.ogham.template.common.adapter;

import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.template.exception.VariantResolutionException;

public class NullVariantResolver implements VariantResolver {

	@Override
	public String getRealPath(TemplateContent template) throws VariantResolutionException {
		return null;
	}

}
