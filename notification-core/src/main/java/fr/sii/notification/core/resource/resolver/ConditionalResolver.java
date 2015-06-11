package fr.sii.notification.core.resource.resolver;

/**
 * Extension of the basic resource resolution interface for adding conditional
 * use of the implementations. This extension adds a method that indicates to
 * the system that uses it if the implementation is able to handle the provided
 * resource path.
 * 
 * @author Aurélien Baudet
 *
 */
public interface ConditionalResolver extends ResourceResolver {
	/**
	 * Indicates if the resource path can be handled by this resource resolver
	 * or not.
	 * 
	 * @param path
	 *            the name or the path of the resource
	 * @return true if the resource path can be handled by this resource
	 *         resolver, false otherwise
	 */
	public boolean supports(String path);
}
