package fr.sii.ogham.core.builder.resolution;

import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configurer.Configurer;

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
@SuppressWarnings("squid:S00119")
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
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #pathPrefix()}.
	 * 
	 * <pre>
	 * .pathPrefix("/foo/template/")
	 * .pathPrefix()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("/template/")
	 * </pre>
	 * 
	 * <pre>
	 * .pathPrefix("/foo/template/")
	 * .pathPrefix()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("/template/")
	 * </pre>
	 * 
	 * In both cases, {@code pathPrefix("/foo/template/")} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param prefix
	 *            the path prefix
	 * @return this instance for fluent chaining
	 */
	MYSELF pathPrefix(String prefix);

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
	 * This method is mainly used by {@link Configurer}s to register some property keys and/or a default value.
	 * The aim is to let developer be able to externalize its configuration (using system properties, configuration file or anything else).
	 * If the developer doesn't configure any value for the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .pathPrefix()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("/template/")
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #pathPrefix(String)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .pathPrefix("/foo/template/")
	 * .pathPrefix()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("/template/")
	 * </pre>
	 * 
	 * The value {@code "/foo/template/"} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	ConfigurationValueBuilder<MYSELF, String> pathPrefix();

	
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
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #pathSuffix()}.
	 * 
	 * <pre>
	 * .pathSuffix(".html")
	 * .pathSuffix()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(".txt")
	 * </pre>
	 * 
	 * <pre>
	 * .pathSuffix(".html")
	 * .pathSuffix()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(".txt")
	 * </pre>
	 * 
	 * In both cases, {@code pathSuffix(".html")} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param suffix
	 *            the path suffix
	 * @return this instance for fluent chaining
	 */
	MYSELF pathSuffix(String suffix);

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
	 * This method is mainly used by {@link Configurer}s to register some property keys and/or a default value.
	 * The aim is to let developer be able to externalize its configuration (using system properties, configuration file or anything else).
	 * If the developer doesn't configure any value for the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .pathSuffix()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(".txt")
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #pathSuffix(String)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .pathSuffix(".html")
	 * .pathSuffix()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(".txt")
	 * </pre>
	 * 
	 * The value {@code ".html"} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	ConfigurationValueBuilder<MYSELF, String> pathSuffix();
}
