package fr.sii.ogham.core.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;

/**
 * <p>
 * Resource that is able to handle string path prefixed by a lookup string. The
 * lookup is case sensitive and must end with a ':'. It must not contain another
 * ':' character.
 * </p>
 * <p>
 * For example, a path could be "classpath:/email/hello.pdf". The lookup is
 * "classpath:".
 * </p>
 * <p>
 * The lookup can also be empty. The template path could then be
 * "/email/hello.pdf".
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public class LookupResource implements NamedResource {
	/**
	 * The path that may contain a lookup
	 */
	private ResourcePath path;

	/**
	 * The name of the attachment
	 */
	private String name;

	/**
	 * Initialize the resource with the provided path to the resource content.
	 * The path may contain a lookup. The name is used for naming the resource.
	 * 
	 * @param path
	 *            the path to the resource (may contain a lookup)
	 * @param name
	 *            the name to display for the resource
	 */
	public LookupResource(ResourcePath path, String name) {
		super();
		this.path = path;
		this.name = name;
	}

	/**
	 * Initialize the resource with the provided path to the resource content.
	 * The path may contain a lookup. The name of the resource is automatically
	 * extracted from the provided path.
	 * 
	 * @param path
	 *            the path to the resource (may contain a lookup)
	 */
	public LookupResource(ResourcePath path) {
		this(path, extractName(path));
	}

	public ResourcePath getPath() {
		return path;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		throw new UnsupportedOperationException("It doesn't directly point to the resource. It needs the underlying real resource associated to the lookup to be able to provide the stream");
	}

	@Override
	public String getName() {
		return name;
	}

	private static String extractName(ResourcePath resolvedPath) {
		String path = resolvedPath.getOriginalPath();
		String name = new File(path).getName();
		int colonIdx = path.indexOf(':');
		name = colonIdx >= 0 ? name.substring(colonIdx + 1) : name;
		return name;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(name, path).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("name", "path").isEqual();
	}
}
