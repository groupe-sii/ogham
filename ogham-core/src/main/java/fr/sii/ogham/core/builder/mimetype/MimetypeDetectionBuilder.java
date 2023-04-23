package fr.sii.ogham.core.builder.mimetype;

import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

import jakarta.activation.MimetypesFileTypeMap;

import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.fluent.Parent;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;

/**
 * Builder that configures mimetype detection.
 * 
 * There exists several implementations to provide the mimetype:
 * <ul>
 * <li>Using Java {@link MimetypesFileTypeMap}</li>
 * <li>Using Java 7 {@link Files#probeContentType(java.nio.file.Path)}</li>
 * <li>Using <a href="http://tika.apache.org/">Apache Tika</a></li>
 * <li>Using <a href="https://github.com/arimus/jmimemagic">JMimeMagic</a></li>
 * </ul>
 * 
 * <p>
 * Both implementations provided by Java are based on file extensions. This
 * can't be used in most cases as we often handle {@link InputStream}s.
 * </p>
 * 
 * <p>
 * In previous version of Ogham, JMimeMagic was used and was working quite well.
 * Unfortunately, the library is no more maintained.
 * </p>
 * 
 * <p>
 * This builder allows to use several providers. It will chain them until one
 * can find a valid mimetype. If none is found, you can explicitly provide the
 * default one.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
public interface MimetypeDetectionBuilder<P> extends Parent<P>, Builder<MimeTypeProvider> {
	/**
	 * Enable mimetype detection using <a href="http://tika.apache.org/">Apache
	 * Tika</a>.
	 * 
	 * <p>
	 * Tika is able to detect mimetype either based on file extension or based
	 * on stream content. Tika is enough smart to try several algorithms
	 * (extension, magic headers...).
	 * </p>
	 * 
	 * <p>
	 * Even if several algorithms are used, Tika may not detect mimetype and in
	 * this case {@code application/octet-stream} mimetype is returned by Tika.
	 * The builder let you make detection fail if
	 * {@code application/octet-stream} is returned (see
	 * {@link TikaBuilder#failIfOctetStream(Boolean)}). This is useful in order
	 * to try detection with another implementation.
	 * </p>
	 * 
	 * <p>
	 * If no Tika instance is explicitly provided, default Tika instance is used
	 * (see {@link Tika} and {@link TikaConfig#getDefaultConfig})
	 * </p>
	 * 
	 * @return the builder to configure Tika detector
	 */
	TikaBuilder<MimetypeDetectionBuilder<P>> tika();

	/**
	 * Configures mimetype replacement.
	 * 
	 * This may be needed in some situations where the detected mimetype is
	 * accurate and valid but it may be understood by external systems that are
	 * less accurate.
	 * 
	 * A concrete example is detection of XHTML mimetype. Standard detection
	 * will detect that mimetype is "application/xhtml" or even
	 * "application/xhtml+xml". This is the expected result from the point of
	 * view of mimetype detection. However, in the context of an email client,
	 * "application/xhtml+xml" may be unknown. The resulting email will then be
	 * unreadable. This is where replacement is useful. You can then degrade the
	 * standard behavior and replace "application/xhtml" and
	 * "application/xhtml+xml" by "text/html".
	 * 
	 * @return the builder to configure replacements of auto-detected mimetypes
	 */
	ReplaceMimetypeBuilder<MimetypeDetectionBuilder<P>> replace();

	/**
	 * If no previously registered mimetype detector could determine mimetype,
	 * use a default value.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #defaultMimetype()}.
	 * 
	 * <pre>
	 * .defaultMimetype("text/html")
	 * .defaultMimetype()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("text/plain")
	 * </pre>
	 * 
	 * <pre>
	 * .defaultMimetype("text/html")
	 * .defaultMimetype()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("text/plain")
	 * </pre>
	 * 
	 * In both cases, {@code defaultMimetype("text/html")} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param mimetype
	 *            the default mimetype
	 * @return this instance for fluent chaining
	 */
	MimetypeDetectionBuilder<P> defaultMimetype(String mimetype);

	/**
	 * If no previously registered mimetype detector could determine mimetype,
	 * use a default value.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .defaultMimetype()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("text/plain")
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #defaultMimetype(String)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .defaultMimetype("text/html")
	 * .defaultMimetype()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("text/plain")
	 * </pre>
	 * 
	 * The value {@code "text/html"} is used regardless of the value of the
	 * properties and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	ConfigurationValueBuilder<MimetypeDetectionBuilder<P>, String> defaultMimetype();

	/**
	 * Define which mimetypes are allowed or not.
	 * <p>
	 * The mimetypes may contain {@literal *} to match sub-types (for example
	 * {@code "image/*"}). If the mimetype starts with {@literal !}, the
	 * mimetype is forbidden ({@code "!application/pdf"} for example)
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #allowed()}.
	 * 
	 * <pre>
	 * .allowed("images/*")
	 * .allowed()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("*")
	 * </pre>
	 * 
	 * <pre>
	 * .allowed("images/*")
	 * .allowed()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("*")
	 * </pre>
	 * 
	 * In both cases, {@code allowed("images/*")} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param mimetypes
	 *            the allowed/unallowed mimetypes
	 * @return this instance for fluent chaining
	 */
	MimetypeDetectionBuilder<P> allowed(List<String> mimetypes);

	/**
	 * Define which mimetypes are allowed or not.
	 * <p>
	 * The mimetypes may contain {@literal *} to match sub-types (for example
	 * {@code "image/*"}). If the mimetype starts with {@literal !}, the
	 * mimetype is forbidden ({@code "!application/pdf"} for example)
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #allowed()}.
	 * 
	 * <pre>
	 * .allowed("images/*", "!application/pdf")
	 * .allowed()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("*")
	 * </pre>
	 * 
	 * <pre>
	 * .allowed("images/*", "!application/pdf")
	 * .allowed()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("*")
	 * </pre>
	 * 
	 * In both cases, {@code allowed("images/*", "!application/pdf")} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param mimetypes
	 *            the allowed/unallowed mimetypes
	 * @return this instance for fluent chaining
	 */
	MimetypeDetectionBuilder<P> allowed(String... mimetypes);

	/**
	 * Define which mimetypes are allowed or not.
	 * <p>
	 * The mimetypes may contain {@literal *} to match sub-types (for example
	 * {@code "image/*"}). If the mimetype starts with {@literal !}, the
	 * mimetype is forbidden ({@code "!application/pdf"} for example)
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .allowed()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("*")
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #allowed(List)} takes precedence over
	 * property values and default value.
	 * 
	 * <pre>
	 * .allowed("images/*")
	 * .allowed()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("*")
	 * </pre>
	 * 
	 * The value {@code "images/*"} is used regardless of the value of the
	 * properties and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	ConfigurationValueBuilder<MimetypeDetectionBuilder<P>, String[]> allowed();
}
