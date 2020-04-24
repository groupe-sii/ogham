package fr.sii.ogham.template.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;

/**
 * Specialized exception that is thrown by template engine integration (can't
 * use a checked exception) in order to wrap a {@link ResolverAdapterException}.
 * 
 * Ogham provides {@link ResourceResolver}s to resolve a resource from a
 * {@link ResourcePath}. However, template engines also have their resource
 * resolution algorithms. Therefore, Ogham provides bridges between Ogham
 * resource management and template engine resource management. These bridges
 * are adapters and need to be known by Ogham and registered in template engine
 * resource management. Therefore, if a resource resolver implementation defined
 * in Ogham doesn't have its equivalent registered in template engine resource
 * management, this exception is raised.
 * 
 * 
 * @author Cyril Dejonghe
 *
 * @see ResolverAdapterException
 * @see ResolverAdapterConfigurationException
 * @see NoResolverAdapterException
 */
@SuppressWarnings({ "squid:MaximumInheritanceDepth" }) // Object, Throwable,
														// Exception and
														// RuntimeException are
														// counted but this is
														// stupid
public class ResolverAdapterNotFoundException extends TemplateRuntimeException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public ResolverAdapterNotFoundException(String message) {
		super(message);
	}

	public ResolverAdapterNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
