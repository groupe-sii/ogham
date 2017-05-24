package fr.sii.ogham.template.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.resource.resolver.ResourceResolver;

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
