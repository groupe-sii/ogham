package fr.sii.ogham.core.resource.path;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import fr.sii.ogham.core.util.ResourceUtils;

/**
 * Resolve the given path against the base path by using the following rules:
 * 
 * <p>
 * If the <b>relative path</b> is not absolute (doesn't start with /) and the
 * <b>base path</b> ends with / then the <b>relative path</b> is <b>appended</b>
 * to the <b>base path</b>.
 * </p>
 * <p>
 * If the <b>relative path</b> is not absolute (doesn't start with /) and the
 * <b>base path</b> doesn't end with / then the <b>relative path</b>
 * <b>replaces</b> the last part of the <b>base path</b>.
 * </p>
 * <p>
 * If the <b>relative path</b> is absolute (starts with /) then the <b>relative
 * path</b> is is used.
 * </p>
 * <p>
 * If the <b>relative path</b> has lookup prefix (like "classpath:") then this
 * lookup prefix is used.
 * </p>
 * <p>
 * If the <b>relative path</b> has no lookup prefix then the lookup prefix of
 * <b>base path</b> is used (if any).
 * </p>
 * <p>
 * If the <b>relative path</b> has different lookup prefix than <b>base path</b>
 * lookup then relative path is considered like an absolute path.
 * </p>
 * 
 * <table>
 * <caption>Exemple of results</caption> <thead>
 * <tr>
 * <th>description</th>
 * <th>base path</th>
 * <th>relative path</th>
 * <th>result</th>
 * </tr>
 * </thead> <tbody>
 * <tr>
 * <td>resolve as child</td>
 * <td>classpath:/template/</td>
 * <td>images/foo.png</td>
 * <td>classpath:/template/images/foo.png</td>
 * </tr>
 * <tr>
 * <td>resolve as sibling</td>
 * <td>classpath:/template/register.html</td>
 * <td>images/foo.png</td>
 * <td>classpath:/template/images/foo.png</td>
 * </tr>
 * <tr>
 * <td>resolve as sibling</td>
 * <td>classpath:/template</td>
 * <td>images/foo.png</td>
 * <td>classpath:/images/foo.png</td>
 * </tr>
 * <tr>
 * <td>absolute path</td>
 * <td>/template/</td>
 * <td>/images/foo.png</td>
 * <td>/images/foo.png</td>
 * </tr>
 * <tr>
 * <td>absolute path (keep lookup)</td>
 * <td>classpath:/template/</td>
 * <td>/images/foo.png</td>
 * <td>classpath:/images/foo.png</td>
 * </tr>
 * <tr>
 * <td>different lookups</td>
 * <td>classpath:/template/</td>
 * <td>file:images/foo.png</td>
 * <td>file:images/foo.png</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * <p>
 * This implementation that requires the list of known lookups.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public class LookupAwareRelativePathResolver implements RelativePathResolver {
	private final List<LookupMetadata> lookupsMeta;

	/**
	 * Initializes with the list of known lookups (indexed by type).
	 * 
	 * @param lookupsIndexedByType
	 *            the lookups indexed by type
	 */
	public LookupAwareRelativePathResolver(Map<String, List<String>> lookupsIndexedByType) {
		super();
		this.lookupsMeta = new ArrayList<>();
		for (Entry<String, List<String>> entry : lookupsIndexedByType.entrySet()) {
			for (String lookup : entry.getValue()) {
				if (!lookup.isEmpty()) {
					lookupsMeta.add(new LookupMetadata(lookup, entry.getKey()));
				}
			}
		}
	}

	@Override
	public RelativePath resolve(ResourcePath source, ResourcePath relativePath) {
		return new RelativePath(source, relativePath, getMergedPath(source, relativePath));
	}

	@Override
	public RelativePath resolve(ResourcePath source, String relativePath) {
		return resolve(source, new UnresolvedPath(relativePath));
	}

	private String getMergedPath(ResourcePath source, ResourcePath relativePath) {
		String relativeLookup = getLookup(relativePath.getOriginalPath());
		String sourceLookup = getLookup(source.getOriginalPath());
		// not the same lookup
		// => not the same system to load
		// => not relative
		if (relativeLookup != null && !isSameLookupType(relativeLookup, sourceLookup)) {
			return relativePath.getOriginalPath();
		}
		// if path points to a directory
		// => append the relative path
		// if path points to a file
		// => replace file by the relative path
		Path merged = Paths.get(withoutLookup(source instanceof ResolvedPath ? ((ResolvedPath) source).getResolvedPath() : source.getOriginalPath()));
		if (isDirectory(source.getOriginalPath())) {
			merged = merged.resolve(withoutLookup(relativePath.getOriginalPath()));
		} else {
			merged = merged.resolveSibling(withoutLookup(relativePath.getOriginalPath()));
		}
		if (relativeLookup != null) {
			return relativeLookup + ResourceUtils.toResourcePath(merged);
		}
		if (sourceLookup != null) {
			return sourceLookup + ResourceUtils.toResourcePath(merged);
		}
		return ResourceUtils.toResourcePath(merged);
	}

	private boolean isSameLookupType(String relativeLookup, String sourceLookup) {
		LookupMetadata relativeMeta = getLookupMeta(relativeLookup);
		if (relativeMeta == null) {
			return false;
		}
		return relativeMeta.isSameType(getLookupMeta(sourceLookup));
	}

	private LookupMetadata getLookupMeta(String lookup) {
		for (LookupMetadata meta : lookupsMeta) {
			if (meta.getLookup().equals(lookup)) {
				return meta;
			}
		}
		return null;
	}

	private static boolean isDirectory(String path) {
		return path.endsWith("/");
	}

	private String withoutLookup(String path) {
		for (LookupMetadata meta : lookupsMeta) {
			if (!meta.getLookup().isEmpty() && path.startsWith(meta.getLookup())) {
				return path.substring(meta.getLookup().length());
			}
		}
		return path;
	}

	private String getLookup(String path) {
		for (LookupMetadata meta : lookupsMeta) {
			if (!meta.getLookup().isEmpty() && path.startsWith(meta.getLookup())) {
				return meta.getLookup();
			}
		}
		return null;
	}

	private static class LookupMetadata {
		private final String lookup;
		private final String type;

		public LookupMetadata(String lookup, String type) {
			super();
			this.lookup = lookup;
			this.type = type;
		}

		public boolean isSameType(LookupMetadata lookupMeta) {
			if (lookupMeta == null) {
				return false;
			}
			return type.equals(lookupMeta.getType());
		}

		public String getLookup() {
			return lookup;
		}

		public String getType() {
			return type;
		}
	}
}
