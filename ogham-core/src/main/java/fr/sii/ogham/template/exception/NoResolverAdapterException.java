package fr.sii.ogham.template.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;

/**
 * Ogham provides {@link ResourceResolver}s to resolve a resource from a
 * {@link ResourcePath}. However, template engines also have their resource
 * resolution algorithms. Therefore, Ogham provides adapters between Ogham
 * resource management and template engine resource management. When template
 * engine tries to resolve a template/resource, it uses one of the adapters.
 * 
 * Specialized exception when the template engine has no adapter able to handle
 * the resource.
 * 
 * @author Aurélien Baudet
 *
 */
public class NoResolverAdapterException extends ResolverAdapterException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public NoResolverAdapterException(String message, ResourceResolver resolver, Throwable cause) {
		super(message, resolver, cause);
	}

	public NoResolverAdapterException(String message, ResourceResolver resolver) {
		super(message, resolver);
	}

	public NoResolverAdapterException(ResourceResolver resolver, Throwable cause) {
		super(resolver, cause);
	}
}
