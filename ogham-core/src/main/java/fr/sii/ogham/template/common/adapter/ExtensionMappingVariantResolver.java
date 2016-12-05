package fr.sii.ogham.template.common.adapter;

import java.util.HashMap;
import java.util.Map;

import fr.sii.ogham.core.message.capability.HasVariant;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.message.content.Variant;
import fr.sii.ogham.template.exception.UnknownVariantException;
import fr.sii.ogham.template.exception.VariantResolutionException;

/**
 * Simple implementation that maps a variant instance to an extension.
 * 
 * @author Aurélien Baudet
 *
 */
public class ExtensionMappingVariantResolver implements VariantResolver {

	private final Map<Variant, String> mapping;

	public ExtensionMappingVariantResolver() {
		this(new HashMap<Variant, String>());
	}

	public ExtensionMappingVariantResolver(Map<Variant, String> mapping) {
		super();
		this.mapping = mapping;
	}

	@Override
	public String getRealPath(TemplateContent template) throws VariantResolutionException {
		if(template instanceof HasVariant) {
			String extension = mapping.get(((HasVariant) template).getVariant());
			if (extension == null) {
				throw new UnknownVariantException("Failed to resolve template with thymeleaf due to unknown variant/extension", template.getPath(), template.getContext(), ((HasVariant) template).getVariant());
			}
			return template.getPath() + extension;
		}
		return template.getPath();
	}

	public ExtensionMappingVariantResolver register(Variant variant, String extension) {
		mapping.put(variant, extension.startsWith(".") ? extension : ("." + extension));
		return this;
	}

}
