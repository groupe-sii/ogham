package fr.sii.ogham.template.freemarker;

import java.io.IOException;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolverRegistry;
import fr.sii.ogham.template.exception.ResolverAdapterException;
import fr.sii.ogham.template.freemarker.adapter.StringContentTemplateLoader;
import fr.sii.ogham.template.freemarker.adapter.TemplateLoaderAdapter;
import freemarker.cache.TemplateLoader;
import freemarker.cache.TemplateLookupContext;
import freemarker.cache.TemplateLookupResult;
import freemarker.cache.TemplateLookupStrategy;

/**
 * Ogham special {@link TemplateLookupStrategy} that is used in order to skip
 * {@link Locale} resolution for templates that are provided directly by a
 * string and not by a path.
 * 
 * @see StringContentTemplateLoader
 * @author Aurélien Baudet
 *
 */
public class SkipLocaleForStringContentTemplateLookupStrategy extends TemplateLookupStrategy {
	private static final Logger LOG = LoggerFactory.getLogger(SkipLocaleForStringContentTemplateLookupStrategy.class);

	private final TemplateLookupStrategy delegate;
	private final ResourceResolverRegistry resolverRegistry;
	private final TemplateLoaderAdapter adapter;

	public SkipLocaleForStringContentTemplateLookupStrategy(TemplateLookupStrategy delegate, ResourceResolverRegistry resolverRegistry, TemplateLoaderAdapter adapter) {
		super();
		this.delegate = delegate;
		this.resolverRegistry = resolverRegistry;
		this.adapter = adapter;
	}

	@Override
	public TemplateLookupResult lookup(TemplateLookupContext ctx) throws IOException {
		try {
			ResourceResolver matchingResolver = resolverRegistry.getSupportingResolver(ctx.getTemplateName());
			// no match, delegate to let delegate decide
			if (matchingResolver == null) {
				return delegate.lookup(ctx);
			}
			TemplateLoader matchingAdapter = adapter.adapt(matchingResolver);
			// if it is a template content (directly a string)
			// => skip locale resolution
			if (matchingAdapter instanceof StringContentTemplateLoader) {
				return ctx.lookupWithLocalizedThenAcquisitionStrategy(ctx.getTemplateName(), null);
			}
			// standard Freemarker behavior
			return delegate.lookup(ctx);
		} catch (ResolverAdapterException e) {
			LOG.debug("Failed to determine which Freemarker adapter to use for template name " + ctx.getTemplateName(), e);
			return delegate.lookup(ctx);
		}
	}

}