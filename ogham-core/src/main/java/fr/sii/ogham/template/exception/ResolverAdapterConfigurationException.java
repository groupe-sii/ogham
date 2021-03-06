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
 * Specialized exception when the template engine has an adapter but it is
 * misconfigured.
 * 
 * @author Aurélien Baudet
 *
 */
public class ResolverAdapterConfigurationException extends ResolverAdapterException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public ResolverAdapterConfigurationException(String message, ResourceResolver resolver, Throwable cause) {
		super(message, resolver, cause);
	}

	public ResolverAdapterConfigurationException(String message, ResourceResolver resolver) {
		super(message, resolver);
	}

	public ResolverAdapterConfigurationException(ResourceResolver resolver, Throwable cause) {
		super(resolver, cause);
	}
}
