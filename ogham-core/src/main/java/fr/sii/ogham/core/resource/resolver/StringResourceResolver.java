package fr.sii.ogham.core.resource.resolver;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.List;

import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.core.resource.SimpleResource;
import fr.sii.ogham.core.resource.path.ResolvedPath;
import fr.sii.ogham.core.resource.path.ResolvedString;
import fr.sii.ogham.core.resource.path.ResourcePath;

/**
 * Resource resolver that just wraps the resource string into a {@link Resource}
 * 
 * @author Aurélien Baudet
 *
 */
public class StringResourceResolver extends AbstractPrefixedLookupPathResolver {

	public StringResourceResolver(List<String> lookups) {
		super(lookups);
	}

	public StringResourceResolver(String... lookups) {
		super(lookups);
	}

	@Override
	protected Resource getResource(ResolvedPath resourcePath) throws ResourceResolutionException {
		// no lookup used
		if(resourcePath instanceof ResolvedString) {
			return new SimpleResource(((ResolvedString) resourcePath).getContent().getBytes(UTF_8));
		}
		// when lookup is used
		return new SimpleResource(resourcePath.getResolvedPath().getBytes(UTF_8));
	}

	@Override
	public boolean supports(ResourcePath path) {
		return path instanceof ResolvedString || super.supports(path);
	}

	@Override
	public ResolvedPath resolve(ResourcePath path) {
		// no lookup used
		if (path instanceof ResolvedString) {
			return (ResolvedString) path;
		}
		// when lookup is used
		return super.resolve(path);
	}

}
