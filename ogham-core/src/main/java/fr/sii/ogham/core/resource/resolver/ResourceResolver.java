package fr.sii.ogham.core.resource.resolver;

import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.core.resource.path.ResolvedPath;
import fr.sii.ogham.core.resource.path.ResourcePath;

/**
 * <p>
 * Interface for all resource resolvers. Resource resolvers are in charge of
 * finding resource from its name (or path). A resource can be stored at many
 * places. For example, a resource can be stored either on the file system, into
 * the classpath, on a distant URL or into a database...
 * </p>
 * <p>
 * Each implementation is able to handle one resolution mechanism. Any new
 * implementation can be defined for future storage source.
 * </p>
 * 
 * @author Aurélien Baudet
 * @see Resource
 */
public interface ResourceResolver {
	/**
	 * Find the resource using the resource path (or its name).
	 * 
	 * @param path
	 *            the path of the resource
	 * @return the found resource
	 * @throws ResourceResolutionException
	 *             when the resource couldn't be found
	 */
	Resource getResource(ResourcePath path) throws ResourceResolutionException;

	/**
	 * Indicates if the resource path can be handled by this resource resolver
	 * or not.
	 * 
	 * @param path
	 *            the name or the path of the resource
	 * @return true if the resource path can be handled by this resource
	 *         resolver, false otherwise
	 */
	boolean supports(ResourcePath path);

	/**
	 * Resolves an unresolved/string path into a {@link ResolvedPath}.
	 * 
	 * @param path
	 *            the path to the resource
	 * @return the resolved resource path or null if it does not support it.
	 */
	ResolvedPath resolve(ResourcePath path);
}
