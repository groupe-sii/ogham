package fr.sii.ogham.template.common.adapter;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.List;

import fr.sii.ogham.core.message.capability.HasVariant;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.message.content.Variant;
import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.template.exception.TemplateVariantNotFoundException;
import fr.sii.ogham.template.exception.VariantResolutionException;

/**
 * Improved implementation that fails but also provide more information to the
 * end-user.
 * 
 * @author Aurélien Baudet
 *
 */
public class FailIfNotFoundWithTestedPathsVariantResolver implements VariantResolver {
	/**
	 * The list of previously used resolvers to retrieve the paths that didn't
	 * match
	 */
	private List<VariantResolver> delegates;

	public FailIfNotFoundWithTestedPathsVariantResolver() {
		this(new ArrayList<>());
	}

	public FailIfNotFoundWithTestedPathsVariantResolver(List<VariantResolver> delegates) {
		super();
		this.delegates = delegates;
	}

	@Override
	public ResourcePath getRealPath(TemplateContent template) throws VariantResolutionException {
		if (!(template instanceof HasVariant)) {
			return template.getPath();
		}
		List<ResourcePath> testedPaths = getTestedPaths(template);
		Variant variant = ((HasVariant) template).getVariant();
		throw new TemplateVariantNotFoundException("Failed to resolve variant (" + variant + ") for template " + template.getPath().getOriginalPath(), template, testedPaths);
	}

	private List<ResourcePath> getTestedPaths(TemplateContent template) {
		List<ResourcePath> testedPaths = new ArrayList<>();
		for (VariantResolver delegate : delegates) {
			testedPaths.addAll(getTestedPaths(template, delegate));
		}
		return testedPaths;
	}

	private List<ResourcePath> getTestedPaths(TemplateContent template, VariantResolver delegate) {
		// if information an be provided, use that information
		if (delegate instanceof CanProvidePossiblePaths) {
			return ((CanProvidePossiblePaths) delegate).getPossiblePaths(template);
		}
		// otherwise call again to give a path if possible
		ResourcePath testedPath = null;
		try {
			testedPath = delegate.getRealPath(template);
		} catch (VariantResolutionException e) {
			// skip because there is no useful information
		}
		return testedPath==null ? emptyList() : asList(testedPath);
	}

	/**
	 * Returns true to make it fail
	 */
	@Override
	public boolean variantExists(TemplateContent template) {
		return true;
	}

	public FailIfNotFoundWithTestedPathsVariantResolver addVariantResolver(VariantResolver variantResolver) {
		delegates.add(variantResolver);
		return this;
	}

}
