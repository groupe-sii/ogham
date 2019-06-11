package fr.sii.ogham.core.resource.path;

/**
 * Resolve the given path against the base path by using the following rules:
 * 
 * <p>
 * If the <b>relative path</b> is not absolute (doesn't start with /) and the
 * <b>base path</b> ends with / then the <b>relative path</b> is
 * <b>appended</b> to the <b>base path</b>.
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
 * If the <b>relative path</b> has different lookup prefix than <b>base
 * path</b> lookup then relative path is considered like an absolute path.
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
 * @author Aurélien Baudet
 *
 */
public interface RelativePathResolver {
	/**
	 * Resolve relative path against the base path:
	 * 
	 * <p>
	 * If the <b>relative path</b> is not absolute (doesn't start with /) and
	 * the <b>base path</b> ends with / then the <b>relative path</b> is
	 * <b>appended</b> to the <b>base path</b>.
	 * </p>
	 * <p>
	 * If the <b>relative path</b> is not absolute (doesn't start with /) and
	 * the <b>base path</b> doesn't end with / then the <b>relative path</b>
	 * <b>replaces</b> the last part of the <b>base path</b>.
	 * </p>
	 * <p>
	 * If the <b>relative path</b> is absolute (starts with /) then the
	 * <b>relative path</b> is is used.
	 * </p>
	 * <p>
	 * If the <b>relative path</b> has lookup prefix (like "classpath:") then
	 * this lookup prefix is used.
	 * </p>
	 * <p>
	 * If the <b>relative path</b> has no lookup prefix then the lookup prefix
	 * of <b>base path</b> is used (if any).
	 * </p>
	 * <p>
	 * If the <b>relative path</b> has different lookup prefix than <b>base
	 * path</b> lookup then relative path is considered like an absolute path.
	 * </p>
	 * 
	 * @param base
	 *            the base path
	 * @param relativePath
	 *            the relative path to resolve
	 * @return the resolved path
	 */
	RelativePath resolve(ResourcePath base, ResourcePath relativePath);

	/**
	 * Resolve relative path against the base path:
	 * 
	 * <p>
	 * If the <b>relative path</b> is not absolute (doesn't start with /) and
	 * the <b>base path</b> ends with / then the <b>relative path</b> is
	 * <b>appended</b> to the <b>base path</b>.
	 * </p>
	 * <p>
	 * If the <b>relative path</b> is not absolute (doesn't start with /) and
	 * the <b>base path</b> doesn't end with / then the <b>relative path</b>
	 * <b>replaces</b> the last part of the <b>base path</b>.
	 * </p>
	 * <p>
	 * If the <b>relative path</b> is absolute (starts with /) then the
	 * <b>relative path</b> is is used.
	 * </p>
	 * <p>
	 * If the <b>relative path</b> has lookup prefix (like "classpath:") then
	 * this lookup prefix is used.
	 * </p>
	 * <p>
	 * If the <b>relative path</b> has no lookup prefix then the lookup prefix
	 * of <b>base path</b> is used (if any).
	 * </p>
	 * <p>
	 * If the <b>relative path</b> has different lookup prefix than <b>base
	 * path</b> lookup then relative path is considered like an absolute path.
	 * </p>
	 * 
	 * @param base
	 *            the base path
	 * @param relativePath
	 *            the relative path as string to resolve
	 * @return the resolved path
	 */
	RelativePath resolve(ResourcePath base, String relativePath);
}
