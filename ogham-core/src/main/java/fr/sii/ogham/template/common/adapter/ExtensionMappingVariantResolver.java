package fr.sii.ogham.template.common.adapter;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.message.capability.HasVariant;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.message.content.Variant;
import fr.sii.ogham.core.resource.path.ResolvedPath;
import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.resource.path.UnresolvedPath;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.template.exception.UnknownVariantException;
import fr.sii.ogham.template.exception.VariantResolutionException;

/**
 * Simple implementation that maps a variant instance to an extension.
 * 
 * @author Aurélien Baudet
 *
 */
public class ExtensionMappingVariantResolver implements VariantResolver, CanProvidePossiblePaths {
	private static final Logger LOG = LoggerFactory.getLogger(ExtensionMappingVariantResolver.class);

	private final ResourceResolver resourceResolver;
	private final Map<Variant, List<String>> mapping;

	public ExtensionMappingVariantResolver(ResourceResolver resourceResolver) {
		this(resourceResolver, new HashMap<Variant, List<String>>());
	}

	public ExtensionMappingVariantResolver(ResourceResolver resourceResolver, Map<Variant, List<String>> mapping) {
		super();
		this.resourceResolver = resourceResolver;
		this.mapping = mapping;
	}

	@Override
	public ResourcePath getRealPath(TemplateContent template) throws VariantResolutionException {
		String originalPath = template.getPath().getOriginalPath();
		if (!(template instanceof HasVariant)) {
			return resourceResolver.resolve(new UnresolvedPath(originalPath));
		}

		Variant variant = ((HasVariant) template).getVariant();
		List<String> extensions = mapping.get(variant);
		if (extensions == null) {
			throw new UnknownVariantException("Failed to resolve template due to unknown variant/extension", template.getPath(), template.getContext(), variant);
		}

		return resolvePath(template, extensions);
	}

	@Override
	public boolean variantExists(TemplateContent template) {
		if (!(template instanceof HasVariant)) {
			return false;
		}

		List<String> extensions = mapping.get(((HasVariant) template).getVariant());
		if (extensions == null) {
			return false;
		}

		return resolvePath(template, extensions) != null;
	}

	@Override
	public List<ResourcePath> getPossiblePaths(TemplateContent template) {
		if (!(template instanceof HasVariant)) {
			return emptyList();
		}

		Variant variant = ((HasVariant) template).getVariant();
		List<String> extensions = mapping.get(variant);
		if (extensions == null) {
			return emptyList();
		}

		// @formatter:off
		return extensions.stream()
				.map(extension -> resolveVariantPath(template.getPath().getOriginalPath(), extension))
				.filter(Objects::nonNull)
				.collect(toList());
		// @formatter:on
	}

	/**
	 * Register a mapping between a variant and an extension.
	 * 
	 * <p>
	 * If a variant is already registered, the new extension is appended to the
	 * list of extensions associated to the variant. The registration order of
	 * extensions for a variant is important.
	 * 
	 * @param variant
	 *            the variant
	 * @param extension
	 *            the extension to associate with the variant
	 * @return this instance for fluent chaining
	 */
	public ExtensionMappingVariantResolver register(Variant variant, String extension) {
		List<String> extensions = mapping.computeIfAbsent(variant, k -> new ArrayList<>());
		String normalized = extension.startsWith(".") ? extension : ("." + extension);
		extensions.add(normalized);
		return this;
	}

	private ResourcePath resolvePath(TemplateContent template, List<String> extensions) {
		ResourcePath templatePath = template.getPath();
		for (String extension : extensions) {
			try {
				ResourcePath path = resolveVariantPath(template.getPath().getOriginalPath(), extension);
				resourceResolver.getResource(path);
				return path;
			} catch (ResourceResolutionException e) {
				LOG.trace("template {}.{} not found", templatePath, extension, e);
			}
		}
		return null;
	}

	private ResourcePath resolveVariantPath(String originalPath, String extension) {
		// if extension already explicitly set, try without adding the extension
		// provided by variant
		ResolvedPath path = useExplicitExtensionIfSameAsVariant(originalPath, extension);
		if (path != null) {
			return path;
		}
		return resourceResolver.resolve(new UnresolvedPath(originalPath + extension));
	}

	private ResolvedPath useExplicitExtensionIfSameAsVariant(String originalPath, String extension) {
		if (originalPath.endsWith(extension)) {
			return resourceResolver.resolve(new UnresolvedPath(originalPath));
		}
		return null;
	}

}
