package fr.sii.ogham.core.resource;

import java.io.IOException;
import java.io.InputStream;

import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;
import fr.sii.ogham.core.util.LookupUtils;

/**
 * <p>
 * Resource that is able to handle string path prefixed by a lookup string. The
 * lookup prefix is case sensitive and must end with a ':'. It must not contain
 * another ':' character.
 * </p>
 * <p>
 * For example, a path could be "classpath:/email/hello.pdf". The lookup prefix
 * is "classpath:".
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
	private static final char WINDOWS_SEPARATOR = '\\';
	private static final char UNIX_SEPARATOR = '/';

	/**
	 * The path that may contain a lookup prefix
	 */
	private String path;

	/**
	 * The name of the attachment
	 */
	private String name;

	/**
	 * Initialize the resource with the provided path to the resource content.
	 * The path may contain a lookup prefix. The name is used for naming the
	 * resource.
	 * 
	 * @param path
	 *            the path to the resource (may contain a lookup prefix)
	 * @param name
	 *            the name to display for the resource
	 */
	public LookupResource(String path, String name) {
		super();
		this.path = path;
		this.name = name;
	}

	/**
	 * Initialize the resource with the provided path to the resource content.
	 * The path may contain a lookup prefix. The name of the resource is
	 * automatically extracted from the provided path.
	 * 
	 * @param path
	 *            the path to the resource (may contain a lookup prefix)
	 */
	public LookupResource(String path) {
		this(path, extractName(path));
	}

	public String getPath() {
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

	private static String extractName(String path) {
		String name;
		int lastSeparatorIdx = Math.max(path.lastIndexOf(UNIX_SEPARATOR), path.lastIndexOf(WINDOWS_SEPARATOR));
		if (lastSeparatorIdx >= 0) {
			name = path.substring(lastSeparatorIdx + 1);
		} else {
			int colonIdx = path.indexOf(LookupUtils.DELIMITER);
			name = colonIdx > 0 ? path.substring(colonIdx + 1) : path;
		}
		return name;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(name).append(path).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("name", "path").isEqual();
	}
}
