package fr.sii.ogham.core.builder.resolution;

import fr.sii.ogham.core.builder.env.EnvironmentBuilder;

/**
 * Configure path prefix/suffix for a resource resolver.
 * 
 * You can set the path prefix/suffix for resource resolution. The aim is to
 * define only the name of the resource (or a subset) and the system will find
 * it for you. It avoids to explicitly write the whole path and let you change
 * the resource resolution easily.
 * 
 * For example:
 * <ul>
 * <li>You have one template located into
 * <code>/foo/template/createAccount.html</code></li>
 * <li>You have one template located into
 * <code>/foo/template/resetPassword.html</code></li>
 * </ul>
 * 
 * So you can set the prefix path to <code>/foo/template/</code> and the suffix
 * to <code>.html</code>. You can now reference the templates using the file
 * name:
 * <ul>
 * <li><code>createAccount</code></li>
 * <li><code>resetPassword</code></li>
 * </ul>
 * 
 * 
 * @author Aurélien Baudet
 *
 * @param <MYSELF>
 *            The type of this instance. This is needed to have the right return
 *            type for fluent chaining with inheritance
 */
public interface PrefixSuffixBuilder<MYSELF> {
	/**
	 * You can set the path prefix for resource resolution. The aim is to define
	 * only the name of the resource (or a subset) and the system will find it
	 * for you. It avoids to explicitly write the whole path and let you change
	 * the resource resolution easily.
	 * 
	 * For example:
	 * <ul>
	 * <li>You have one template located into
	 * <code>/foo/template/createAccount.html</code></li>
	 * <li>You have one template located into
	 * <code>/foo/template/resetPassword.html</code></li>
	 * </ul>
	 * 
	 * So you can set the prefix path to <code>/foo/template/</code>. You can
	 * now reference the templates using the file name:
	 * <ul>
	 * <li><code>createAccount.html</code></li>
	 * <li><code>resetPassword.html</code></li>
	 * </ul>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .pathPrefix("${custom.property.high-priority}", "${custom.property.low-priority}");
	 * </pre>
	 * 
	 * The properties are not immediately evaluated. The evaluation will be done
	 * when the build() method of the specialized resource resolution builder is
	 * called.
	 * 
	 * If you provide several property keys, evaluation will be done on the
	 * first key and if the property exists (see {@link EnvironmentBuilder}),
	 * its value is used. If the first property doesn't exist in properties,
	 * then it tries with the second one and so on.
	 * 
	 * @param prefixes
	 *            one path prefix, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	MYSELF pathPrefix(String... prefixes);

	/**
	 * You can set the path suffix for resource resolution. The aim is to define
	 * only the name of the resource (or a subset) and the system will find it
	 * for you. It avoids to explicitly write the whole path and let you change
	 * the resource resolution easily.
	 * 
	 * For example:
	 * <ul>
	 * <li>You have one template located into
	 * <code>createAccount.html</code></li>
	 * <li>You have one template located into
	 * <code>resetPassword.html</code></li>
	 * </ul>
	 * 
	 * So you can set the suffix path to <code>.html</code>. You can now
	 * reference the templates using the file name:
	 * <ul>
	 * <li><code>createAccount</code></li>
	 * <li><code>resetPassword</code></li>
	 * </ul>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .pathSuffix("${custom.property.high-priority}", "${custom.property.low-priority}");
	 * </pre>
	 * 
	 * The properties are not immediately evaluated. The evaluation will be done
	 * when the build() method of the specialized resource resolution builder is
	 * called.
	 * 
	 * If you provide several property keys, evaluation will be done on the
	 * first key and if the property exists (see {@link EnvironmentBuilder}),
	 * its value is used. If the first property doesn't exist in properties,
	 * then it tries with the second one and so on.
	 * 
	 * @param suffixes
	 *            one path suffix, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	MYSELF pathSuffix(String... suffixes);
}
