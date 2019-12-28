package fr.sii.ogham.core.resource.resolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.core.resource.path.ResolvedPath;
import fr.sii.ogham.core.resource.path.ResolvedResourcePath;
import fr.sii.ogham.core.resource.path.ResourcePath;

/**
 * <p>
 * Decorator resource resolver that use parent path and extension for resource
 * resolution.
 * </p>
 * <p>
 * For example, the parent path values "email/user/" and the extension is
 * ".html". The resource name is "hello". This resource resolver appends the
 * parent path, the resource name and the extension generating the path
 * "email/user/hello.html".
 * </p>
 * <p>
 * Once the path is generated, then this implementation delegates the real
 * resource resolution to another implementation.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public class RelativeResolver implements DelegateResourceResolver {
	private static final Logger LOG = LoggerFactory.getLogger(RelativeResolver.class);

	/**
	 * The parent path to add to the resource name (or path)
	 */
	private String parentPath;

	/**
	 * The suffix to add to the resource name (or path)
	 */
	private String extension;

	/**
	 * The delegate resolver that will do the real resource resolution
	 */
	private RelativisableResourceResolver delegate;

	/**
	 * Initialize the resolver with the mandatory delegate and a parent path. No
	 * extension will be appended to the resource path.
	 * 
	 * @param delegate
	 *            the resolver that will do the real resource resolution
	 * @param parentPath
	 *            a string to add before the resource path
	 */
	public RelativeResolver(RelativisableResourceResolver delegate, String parentPath) {
		this(delegate, parentPath, "");
	}

	/**
	 * Initialize the resolver with the mandatory delegate, a parent path and a
	 * extension.
	 * 
	 * @param delegate
	 *            the resolver that will do the real resource resolution
	 * @param parentPath
	 *            a string to add before the resource path
	 * @param extension
	 *            a string to add after the resource path
	 */
	public RelativeResolver(RelativisableResourceResolver delegate, String parentPath, String extension) {
		super();
		this.parentPath = parentPath == null ? "" : parentPath;
		this.extension = extension == null ? "" : extension;
		this.delegate = delegate;
	}

	@Override
	public Resource getResource(ResourcePath path) throws ResourceResolutionException {
		if (delegate.isAbsolute(path)) {
			LOG.trace("Absolute resource path {} => do not add parentPath/extension", path);
			return delegate.getResource(path);
		} else {
			LOG.debug("Adding parentPath '{}' and extension '{}' to the resource path {}", parentPath, extension, path);
			return delegate.getResource(delegate.resolve(path, parentPath, extension));
		}
	}

	@Override
	public boolean supports(ResourcePath path) {
		return delegate.supports(path);
	}

	@Override
	public ResolvedPath resolve(ResourcePath path) {
		ResolvedPath resourcePath = delegate.resolve(path);
		boolean absolute = resourcePath.getResolvedPath().startsWith("/");
		if (absolute) {
			LOG.trace("Absolute resource path {} => do not add parentPath/extension", path);
			return resourcePath;
		}
		LOG.debug("Adding parentPath ({}) and extension ({}) to the resource path {}", parentPath, extension, path);
		return new ResolvedResourcePath(path, resourcePath.getLookup(), parentPath + resourcePath.getResolvedPath() + extension);
	}

	@Override
	public ResourceResolver getActualResourceResolver() {
		return delegate instanceof DelegateResourceResolver ? ((RelativeResolver) delegate).getActualResourceResolver() : delegate;
	}

	@Override
	public String toString() {
		return "RelativeResolver [parentPath=" + parentPath + ", extension=" + extension + ", delegate=" + delegate + "]";
	}
}
