package fr.sii.ogham.template.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import java.util.List;
import java.util.stream.Collectors;

import fr.sii.ogham.core.message.capability.HasVariant;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.message.content.Variant;
import fr.sii.ogham.core.resource.path.ResolvedPath;
import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.template.context.Context;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class TemplateVariantNotFoundException extends VariantResolutionException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final transient List<ResourcePath> testedPaths;

	public TemplateVariantNotFoundException(String message, TemplateContent template, List<ResourcePath> testedPaths) {
		this(message, template.getPath(), template.getContext(), getVariant(template), testedPaths);
	}
	
	public TemplateVariantNotFoundException(String message, ResourcePath template, Context context, Variant variant, List<ResourcePath> testedPaths) {
		super(message, template, context, variant);
		this.testedPaths = testedPaths;
	}

	public List<ResourcePath> getTestedPaths() {
		return testedPaths;
	}
	
	public List<String> getResolvedPaths() {
		return testedPaths
				.stream()
				.map(p -> p instanceof ResolvedPath ? ((ResolvedPath) p).getResolvedPath() : p.getOriginalPath())
				.collect(Collectors.toList());
	}

	private static Variant getVariant(TemplateContent template) {
		if (template instanceof HasVariant) {
			return ((HasVariant) template).getVariant();
		}
		return null;
	}
}
