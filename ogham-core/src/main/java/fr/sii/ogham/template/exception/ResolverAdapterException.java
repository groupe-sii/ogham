package fr.sii.ogham.template.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;

/**
 * Ogham provides {@link ResourceResolver}s to resolve a resource from a
 * {@link ResourcePath}. However, template engines also have their resource
 * resolution algorithms. Therefore, Ogham provides adapters between Ogham
 * resource management and template engine resource management. When template
 * engine tries to resolve a template/resource, it uses one of the adapters.
 * 
 * This is a general exception that has subclasses to indicate the reason why
 * the adapter has failed.
 * 
 * @author Aurélien Baudet
 *
 * @see ResolverAdapterConfigurationException
 * @see NoResolverAdapterException
 */
public class ResolverAdapterException extends MessagingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final transient ResourceResolver resolver;

	public ResolverAdapterException(String message, ResourceResolver resolver, Throwable cause) {
		super(message, cause);
		this.resolver = resolver;
	}

	public ResolverAdapterException(String message, ResourceResolver resolver) {
		super(message);
		this.resolver = resolver;
	}

	public ResolverAdapterException(ResourceResolver resolver, Throwable cause) {
		super(cause);
		this.resolver = resolver;
	}

	public ResourceResolver getResolver() {
		return resolver;
	}
}
