package fr.sii.ogham.template.thymeleaf.v3;

import java.util.Map;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolution;

import fr.sii.ogham.core.resource.path.ResolvedPath;
import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.resource.path.UnresolvedPath;
import fr.sii.ogham.core.resource.resolver.FirstSupportingResourceResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.template.exception.NoResolverAdapterException;
import fr.sii.ogham.template.exception.ResolverAdapterNotFoundException;
import fr.sii.ogham.template.thymeleaf.common.adapter.FirstSupportingResolverAdapter;
import fr.sii.ogham.template.thymeleaf.common.exception.TemplateResolutionException;

/**
 * <p>
 * Decorator resolver that is able to manage {@link ResourcePath}.
 * </p>
 * <p>
 * It delegates to a {@link FirstSupportingResourceResolver} the link between
 * path, {@link ResourcePath} and {@link ResourceResolver}. each lookup to a
 * dedicated {@link ResourceResolver}.
 * </p>
 * <p>
 * It delegates to a {@link FirstSupportingResolverAdapter} the link between
 * {@link ResourceResolver} and the {@link ITemplateResolver} implementation to
 * use with the given path.
 * </p>
 * 
 * @author Cyril Dejonghe
 * @see FirstSupportingResourceResolver
 * @see FirstSupportingResolverAdapter
 *
 */
public class ThymeLeafV3FirstSupportingTemplateResolver implements ITemplateResolver {

	private FirstSupportingResourceResolver resolver;
	private FirstSupportingResolverAdapter resolverAdapter;

	public ThymeLeafV3FirstSupportingTemplateResolver(FirstSupportingResourceResolver resolver, FirstSupportingResolverAdapter resolverAdapter) {
		super();
		this.resolver = resolver;
		this.resolverAdapter = resolverAdapter;
	}

	@Override
	public String getName() {
		return "ThymeLeafFirstSupportingTemplateResolver";
	}

	@Override
	public Integer getOrder() {
		return 0;
	}

	@Override
	public TemplateResolution resolveTemplate(IEngineConfiguration configuration, String ownerTemplate, String template, Map<String, Object> templateResolutionAttributes) {
		try {
			ResourceResolver supportingResolver = resolver.getSupportingResolver(new UnresolvedPath(template));
			ITemplateResolver templateResolver = resolverAdapter.adapt(supportingResolver);
			ResolvedPath resourcePath = supportingResolver.resolve(new UnresolvedPath(template));
			String resolvedPath = resourcePath.getResolvedPath();
			TemplateResolution resolution = templateResolver.resolveTemplate(configuration, ownerTemplate, resolvedPath, templateResolutionAttributes);
			if(!templateExists(resolution)) {
				throw new TemplateResolutionException("Failed to find template "+template+" ("+resolvedPath+")", template, resourcePath);
			}
			return resolution;
		} catch (NoResolverAdapterException e) {
			throw new ResolverAdapterNotFoundException("Unable to resolve template cause no adapter supporting template name '" + template + "' was found.", e);

		}
	}

	private static boolean templateExists(TemplateResolution resolution) {
		// Thymeleaf can check existence of template but only if option is set.
		// If set and the template doesn't exist, resolution will be null.
		if(resolution == null) {
			return false;
		}
		// If option is not set, then check existence manually to be consistent
		// with other template engines
		if(!resolution.isTemplateResourceExistenceVerified()) {
			return resolution.getTemplateResource().exists();
		}
		return false;
	}

}
