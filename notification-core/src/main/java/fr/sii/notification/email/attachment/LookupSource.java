package fr.sii.notification.email.attachment;

import fr.sii.notification.core.util.EqualsBuilder;
import fr.sii.notification.core.util.HashCodeBuilder;

/**
 * <p>
 * Source that is able to handle string path prefixed by a lookup string. The
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
public class LookupSource implements Source {

	/**
	 * The path that may contain a lookup prefix
	 */
	private String path;

	/**
	 * The name of the attachment
	 */
	private String name;

	/**
	 * Initialize the source with the provided path to the attachment content.
	 * The path may contain a lookup prefix. The name is used for naming the
	 * attachment when sending the email.
	 * 
	 * @param path
	 *            the path to the attachment (may contain a lookup prefix)
	 * @param name
	 *            the name to display for the attachment
	 */
	public LookupSource(String path, String name) {
		super();
		this.path = path;
		this.name = name;
	}

	/**
	 * Initialize the source with the provided path to the attachment content.
	 * The path may contain a lookup prefix. The name of the attachment is
	 * automatically extracted from the provided path.
	 * 
	 * @param path
	 *            the path to the attachment (may contain a lookup prefix)
	 */
	public LookupSource(String path) {
		this(path, extractName(path));
	}

	public String getPath() {
		return path;
	}

	@Override
	public String getName() {
		return name;
	}

	private static String extractName(String path) {
		String name;
		int lastSlashIdx = path.lastIndexOf("/");
		if (lastSlashIdx >= 0) {
			name = path.substring(lastSlashIdx + 1);
		} else {
			int colonIdx = path.indexOf(":");
			name = colonIdx > 0 ? path.substring(colonIdx + 1) : path;
		}
		return name;
	}

	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().appendSuper(super.hashCode()).append(name).append(path).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendSuper(super.equals(obj)).append("name", "path").equals();
	}
}
