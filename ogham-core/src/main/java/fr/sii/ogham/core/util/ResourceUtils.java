package fr.sii.ogham.core.util;

import java.nio.file.Path;
import java.util.StringJoiner;

/**
 * Utility class to handle resources
 * 
 * @author Aurélien Baudet
 *
 */
public final class ResourceUtils {
	/**
	 * Convert a path that is dependent on OS to a resource path (using slashes).
	 * 
	 * @param path the path to convert
	 * @return the converted path
	 */
	public static String toResourcePath(Path path) {
		StringJoiner joiner = new StringJoiner("/", path.startsWith("/") ? "/" : "", path.endsWith("/") ? "/" : "");
		path.forEach(p -> joiner.add(p.toString()));
		return joiner.toString();
	}

	private ResourceUtils() {
		super();
	}
}
