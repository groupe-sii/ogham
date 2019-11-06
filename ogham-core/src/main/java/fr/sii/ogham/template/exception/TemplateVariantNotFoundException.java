package fr.sii.ogham.template.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import java.util.List;
import java.util.stream.Collectors;

import fr.sii.ogham.core.message.content.Variant;
import fr.sii.ogham.core.resource.path.ResolvedPath;
import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.template.context.Context;

public class TemplateVariantNotFoundException extends VariantResolutionException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final List<ResourcePath> testedPaths;

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
}
