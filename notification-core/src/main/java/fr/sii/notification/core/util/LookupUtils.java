package fr.sii.notification.core.util;

import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Utility class for handling resource searching using a lookup.
 * </p>
 * <p>
 * The lookup is a prefix that contains at least one ':' character. The lookup
 * prefix is case sensitive. For example, if the path is
 * <code>"classpath:/foo/bar.txt"</code> then the lookup prefix is
 * <code>"classpath:"</code>. If the path is <code>"foo:bar:/foobar.txt"</code>
 * then the lookup prefix is <code>"foo:bar:"</code>.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public final class LookupUtils {
	private static final Logger LOG = LoggerFactory.getLogger(LookupUtils.class);

	/**
	 * Lookup delimiter
	 */
	public static final String DELIMITER = ":";

	/**
	 * <p>
	 * Search for the entry that is able to handle the lookup provided in the
	 * path. The lookup is a prefix that contains at least one ':' character.
	 * The lookup prefix is case sensitive. For example, if the path is
	 * <code>"classpath:/foo/bar.txt"</code> then the lookup prefix is
	 * <code>"classpath:"</code>. If the path is
	 * <code>"foo:bar:/foobar.txt"</code> then the lookup prefix is
	 * <code>"foo:bar:"</code>.
	 * </p>
	 * <p>
	 * If the path starts with one of the non empty provided lookup, then the
	 * corresponding entry is returned.
	 * </p>
	 * <p>
	 * If the path doesn't contain any lookup (not ':' character) and if the
	 * mapping contains empty lookup, then the entry with the empty lookup is
	 * returned.
	 * </p>
	 * <p>
	 * If the path contains a ':' character but none of the lookup provided in
	 * the mapping can't handle it then null is returned.
	 * </p>
	 * For example, the mapping contains the following lookups:
	 * <ul>
	 * <li><code>"classpath"</code></li>
	 * <li><code>"file"</code></li>
	 * <li><code>""</code></li>
	 * </ul>
	 * <p>
	 * If the provided path is <code>"classpath:/foo/bar.txt"</code> then the
	 * classpath entry is returned.
	 * </p>
	 * <p>
	 * If the provided path is <code>"/foo/bar.txt"</code> then the empty entry
	 * is returned.
	 * </p>
	 * <p>
	 * If the provided path is <code>"unknown:/foo/bar.txt"</code> then the
	 * <code>null</code> is returned.
	 * </p>
	 * 
	 * @param mapping
	 *            the mapping that contains the lookups (without ':' character)
	 * @param path
	 *            the path used to get the associated lookup entry
	 * @param <T>
	 *            the type for entries
	 * @return the lookup entry that can handle the path or null if none can
	 *         handle it
	 */
	public static <T> Entry<String, T> getLookupEntry(Map<String, T> mapping, String path) {
		// store the empty lookup entry if any
		Entry<String, T> emptyLookupEntry = null;
		for (Entry<String, T> entry : mapping.entrySet()) {
			// do not handle the empty lookup entry now, keep it for later
			if (entry.getKey().isEmpty()) {
				emptyLookupEntry = entry;
			} else if (path.startsWith(entry.getKey() + DELIMITER)) {
				// if a lookup is found and it's not the default => stop now and
				// return it
				LOG.trace("Lookup {} found for resource path {}", entry.getKey(), path);
				return entry;
			}
		}
		// if the path doesn't contain ':' character, then we can use empty
		// lookup entry as default lookup
		// if the path contains ':' character but the lookup is unknown in the
		// mapping then it means that the lookup is either not registered or the
		// lookup can't be handled
		return path.contains(DELIMITER) ? null : emptyLookupEntry;
	}

	/**
	 * <p>
	 * Search for the lookup prefix that is able to handle the lookup provided
	 * in the path. The lookup is a prefix that contains at least one ':'
	 * character. The lookup prefix is case sensitive. For example, if the path
	 * is <code>"classpath:/foo/bar.txt"</code> then the lookup prefix is
	 * <code>"classpath:"</code>. If the path is
	 * <code>"foo:bar:/foobar.txt"</code> then the lookup prefix is
	 * <code>"foo:bar:"</code>.
	 * </p>
	 * <p>
	 * If the path starts with one of the non empty provided lookup, then the
	 * corresponding lookup prefix is returned.
	 * </p>
	 * <p>
	 * If the path doesn't contain any lookup (not ':' character) and if the
	 * mapping contains empty lookup, then the empty string is returned.
	 * </p>
	 * <p>
	 * If the path contains a ':' character but none of the lookup provided in
	 * the mapping can't handle it then null is returned.
	 * </p>
	 * For example, the mapping contains the following lookups:
	 * <ul>
	 * <li><code>"classpath"</code></li>
	 * <li><code>"file"</code></li>
	 * <li><code>""</code></li>
	 * </ul>
	 * <p>
	 * If the provided path is <code>"classpath:/foo/bar.txt"</code> then
	 * <code>"classpath"</code> is returned.
	 * </p>
	 * <p>
	 * If the provided path is <code>"/foo/bar.txt"</code> then <code>""</code>
	 * is returned.
	 * </p>
	 * <p>
	 * If the provided path is <code>"unknown:/foo/bar.txt"</code> then
	 * <code>null</code> is returned.
	 * </p>
	 * 
	 * @param mapping
	 *            the mapping that contains the lookups (without ':' character)
	 * @param path
	 *            the path used to get the associated lookup prefix
	 * @return the lookup prefix that can handle the path or null if none can
	 *         handle it
	 */
	public static String getLookupType(Map<String, ?> mapping, String path) {
		Entry<String, ?> entry = getLookupEntry(mapping, path);
		return entry == null ? null : entry.getKey();
	}

	/**
	 * <p>
	 * Search for the resolver associated to the lookup that is able to handle
	 * the lookup provided in the path. The lookup is a prefix that contains at
	 * least one ':' character. The lookup prefix is case sensitive. For
	 * example, if the path is <code>"classpath:/foo/bar.txt"</code> then the
	 * lookup prefix is <code>"classpath:"</code>. If the path is
	 * <code>"foo:bar:/foobar.txt"</code> then the lookup prefix is
	 * <code>"foo:bar:"</code>.
	 * </p>
	 * <p>
	 * If the path starts with one of the non empty provided lookup, then the
	 * corresponding resolver is returned.
	 * </p>
	 * <p>
	 * If the path doesn't contain any lookup (not ':' character) and if the
	 * mapping contains empty lookup, then the resolver associated to empty
	 * lookup is returned.
	 * </p>
	 * <p>
	 * If the path contains a ':' character but none of the lookup provided in
	 * the mapping can't handle it then null is returned.
	 * </p>
	 * For example, the mapping contains the following lookups:
	 * <ul>
	 * <li><code>"classpath"</code></li>
	 * <li><code>"file"</code></li>
	 * <li><code>""</code></li>
	 * </ul>
	 * <p>
	 * If the provided path is <code>"classpath:/foo/bar.txt"</code> then the
	 * resolver associated to <code>"classpath"</code> is returned.
	 * </p>
	 * <p>
	 * If the provided path is <code>"/foo/bar.txt"</code> then the resolver
	 * associated to <code>""</code> is returned.
	 * </p>
	 * <p>
	 * If the provided path is <code>"unknown:/foo/bar.txt"</code> then
	 * <code>null</code> is returned.
	 * </p>
	 * 
	 * @param mapping
	 *            the mapping that contains the lookups (without ':' character)
	 * @param path
	 *            the path used to get the associated lookup resolver
	 * @param <T>
	 *            the type of the resolver
	 * @return the lookup resolver that can handle the path or null if none can
	 *         handle it
	 */
	public static <T> T getResolver(Map<String, T> mapping, String path) {
		Entry<String, T> entry = getLookupEntry(mapping, path);
		return entry == null ? null : entry.getValue();
	}

	/**
	 * <p>
	 * Get the path without the lookup prefix. The lookup is a prefix that
	 * contains at least one ':' character. The lookup prefix is case sensitive.
	 * For example, if the path is <code>"classpath:/foo/bar.txt"</code> then
	 * the lookup prefix is <code>"classpath:"</code>. If the path is
	 * <code>"foo:bar:/foobar.txt"</code> then the lookup prefix is
	 * <code>"foo:bar:"</code>.
	 * </p>
	 * For example, the mapping contains the following lookups:
	 * <ul>
	 * <li><code>"classpath"</code></li>
	 * <li><code>"file"</code></li>
	 * <li><code>""</code></li>
	 * </ul>
	 * <p>
	 * If the provided path is <code>"classpath:/foo/bar.txt"</code> then
	 * <code>"/foo/bar.txt"</code> is returned.
	 * </p>
	 * <p>
	 * If the provided path is <code>"/foo/bar.txt"</code> then
	 * <code>"/foo/bar.txt"</code> is returned.
	 * </p>
	 * <p>
	 * If the provided path is <code>"unknown:/foo/bar.txt"</code> then
	 * <code>"unknown:/foo/bar.txt"</code> is returned.
	 * </p>
	 * 
	 * @param mapping
	 *            the mapping that contains the lookups (without ':' character)
	 * @param path
	 *            the path that may contain lookup prefix
	 * @return the path without lookup
	 */
	public static String getRealPath(Map<String, ?> mapping, String path) {
		Entry<String, ?> entry = getLookupEntry(mapping, path);
		return entry == null ? path : getRealPath(entry.getKey(), path);
	}

	/**
	 * <p>
	 * Get the path without the lookup prefix. The lookup is a prefix that
	 * contains at least one ':' character. The lookup prefix is case sensitive.
	 * For example, if the path is <code>"classpath:/foo/bar.txt"</code> then
	 * the lookup prefix is <code>"classpath:"</code>. If the path is
	 * <code>"foo:bar:/foobar.txt"</code> then the lookup prefix is
	 * <code>"foo:bar:"</code>.
	 * </p>
	 * <p>
	 * The lookup may either be null, an empty string or a string ending with
	 * ':'.
	 * </p>
	 * 
	 * @param lookup
	 *            the lookup prefix to remove from the path
	 * @param path
	 *            the path that may contain lookup prefix
	 * @return the path without lookup
	 */
	public static String getRealPath(String lookup, String path) {
		return lookup == null || lookup.isEmpty() ? path : path.substring(lookup.length() + 1);
	}

	private LookupUtils() {
		super();
	}
}
