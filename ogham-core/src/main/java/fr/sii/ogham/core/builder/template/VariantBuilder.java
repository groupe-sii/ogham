package fr.sii.ogham.core.builder.template;

import fr.sii.ogham.core.message.content.Variant;
import fr.sii.ogham.template.common.adapter.VariantResolver;

public interface VariantBuilder<MYSELF> {
	MYSELF variant(Variant variant, String extension);

	VariantResolver buildVariant();
}
