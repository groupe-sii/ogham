package fr.sii.ogham.core.exception.resource;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;

/**
 * Ogham uses {@link ResourceResolver} to resolve a {@link Resource} from a
 * {@link ResourcePath}.
 * 
 * This is a specialized exception that indicates that no
 * {@link ResourceResolver} could handle the {@link ResourcePath}.
 * 
 * @author Aurélien Baudet
 *
 * @see NoResolverException
 */
public class NoResolverException extends ResourceResolutionException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public NoResolverException(String message, ResourcePath path, Throwable cause) {
		super(message, path, cause);
	}

	public NoResolverException(String message, ResourcePath path) {
		super(message, path);
	}

	public NoResolverException(ResourcePath path, Throwable cause) {
		super(path, cause);
	}
}
