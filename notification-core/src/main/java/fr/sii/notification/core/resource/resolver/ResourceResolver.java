package fr.sii.notification.core.resource.resolver;

import fr.sii.notification.core.exception.resource.ResourceResolutionException;
import fr.sii.notification.core.resource.Resource;

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
	 *            the path of the resource (or its name)
	 * @return the found resource
	 * @throws ResourceResolutionException
	 *             when the resource couldn't be found
	 */
	public Resource getResource(String path) throws ResourceResolutionException;
}
