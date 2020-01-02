package fr.sii.ogham.template.freemarker;

import java.io.IOException;
import java.io.Reader;

import fr.sii.ogham.core.resource.path.ResolvedResourcePath;
import fr.sii.ogham.core.resource.path.UnresolvedPath;
import fr.sii.ogham.core.resource.resolver.FirstSupportingResourceResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.template.exception.NoResolverAdapterException;
import fr.sii.ogham.template.exception.ResolverAdapterConfigurationException;
import fr.sii.ogham.template.exception.ResolverAdapterException;
import fr.sii.ogham.template.exception.ResolverAdapterNotFoundException;
import fr.sii.ogham.template.freemarker.adapter.FirstSupportingResolverAdapter;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;

/**
 * <p>
 * Decorator resolver that is able to manage {@link ResolvedResourcePath}.
 * </p>
 * <p>
 * It delegates to a {@link FirstSupportingResourceResolver} the link between path, {@link ResolvedResourcePath} and {@link ResourceResolver}. each lookup to a
 * dedicated {@link ResourceResolver}.
 * </p>
 * <p>
 * It delegates to a {@link FirstSupportingResolverAdapter} the link between {@link ResourceResolver} and the {@link TemplateLoader} implementation to use with
 * the given path.
 * </p>
 * 
 * @author Cyril Dejonghe
 * @see FirstSupportingResourceResolver
 * @see FirstSupportingResolverAdapter
 * @see MultiTemplateLoader
 *
 */
public class FreeMarkerFirstSupportingTemplateLoader implements TemplateLoader {

	private FirstSupportingResourceResolver resolver;
	private FirstSupportingResolverAdapter resolverAdapter;

	public FreeMarkerFirstSupportingTemplateLoader(FirstSupportingResourceResolver resolver, FirstSupportingResolverAdapter resolverAdapter) {
		super();
		this.resolver = resolver;
		this.resolverAdapter = resolverAdapter;
	}

	@Override
	public Object findTemplateSource(String unresolvedTemplateName) throws IOException {
		ResourceResolver supportingResolver = resolver.getSupportingResolver(new UnresolvedPath(unresolvedTemplateName));
		TemplateLoader templateLoader;
		try {
			templateLoader = resolverAdapter.adapt(supportingResolver);
			String resolvedPath = supportingResolver.resolve(new UnresolvedPath(unresolvedTemplateName)).getResolvedPath();
			Object source = templateLoader.findTemplateSource(resolvedPath);
			return source == null ? null : new AdaptedSource(source, templateLoader);

		} catch (NoResolverAdapterException e) {
			throw new ResolverAdapterNotFoundException("Unable to find template source cause no adapter supporting template name '" + unresolvedTemplateName + "' was found. ", e);
		} catch (ResolverAdapterConfigurationException e) {
			throw new ResolverAdapterNotFoundException("Unable to find template source because of invalid adapter configuration for template name '" + unresolvedTemplateName + "'. ", e);
		} catch (ResolverAdapterException e) {
			throw new IOException("Unable to find template source because of adapter failure for template name '" + unresolvedTemplateName + "'. ", e);
		}
	}

	@Override
	public long getLastModified(Object templateSource) {
		return ((AdaptedSource) templateSource).getLastModified();
	}

	@Override
	public Reader getReader(Object templateSource, String encoding) throws IOException {
		return ((AdaptedSource) templateSource).getReader(encoding);
	}

	@Override
	public void closeTemplateSource(Object templateSource) throws IOException {
		((AdaptedSource) templateSource).close();
	}

	/**
	 * @see MultiTemplateLoader.MultiSource
	 */
	static final class AdaptedSource {

		private final Object source;
		private final TemplateLoader loader;

		AdaptedSource(Object source, TemplateLoader loader) {
			this.source = source;
			this.loader = loader;
		}

		long getLastModified() {
			return loader.getLastModified(source);
		}

		Reader getReader(String encoding) throws IOException {
			return loader.getReader(source, encoding);
		}

		void close() throws IOException {
			loader.closeTemplateSource(source);
		}

		Object getWrappedSource() {
			return source;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof AdaptedSource) {
				AdaptedSource m = (AdaptedSource) o;
				return m.loader.equals(loader) && m.source.equals(source);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return loader.hashCode() + 31 * source.hashCode();
		}

		@Override
		public String toString() {
			return source.toString();
		}
	}
}
