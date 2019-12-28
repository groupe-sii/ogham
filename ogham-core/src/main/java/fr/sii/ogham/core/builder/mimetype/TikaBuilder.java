package fr.sii.ogham.core.builder.mimetype;

import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.Parent;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;

/**
 * Enable mimetype detection using <a href="http://tika.apache.org/">Apache
 * Tika</a>.
 * 
 * <p>
 * Tika is able to detect mimetype either based on file extension or based on
 * stream content. Tika is enough smart to try several algorithms (extension,
 * magic headers...).
 * </p>
 * 
 * <p>
 * Even if several algorithms are used, Tika may not detect mimetype and in this
 * case {@code application/octet-stream} mimetype is returned by Tika. The
 * builder let you make detection fail if {@code application/octet-stream} is
 * returned (see {@link TikaBuilder#failIfOctetStream(Boolean)}). This is useful
 * in order to try detection with another implementation.
 * </p>
 * 
 * <p>
 * If no Tika instance is explicitly provided, default Tika instance is used
 * (see {@link Tika} and {@link TikaConfig#getDefaultConfig})
 * </p>
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
public interface TikaBuilder<P> extends Parent<P>, Builder<MimeTypeProvider> {
	/**
	 * Provide custom {@link Tika} instance.
	 * 
	 * <p>
	 * This can be useful if the default instance doesn't fit your needs. This
	 * lets you complete control on Tika configuration.
	 * </p>
	 * 
	 * @param tika
	 *            the Tika instance
	 * @return this instance for fluent chaining
	 */
	TikaBuilder<P> instance(Tika tika);

	
	/**
	 * If Tika detection returns an {@code application/octet-stream}, it may
	 * means that detection was not enough accurate. In order to try other
	 * registered implementations that are able to detect mimetypes, you can set
	 * this to true.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #failIfOctetStream()}.
	 * 
	 * <pre>
	 * .failIfOctetStream(false)
	 * .failIfOctetStream()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * <pre>
	 * .failIfOctetStream(false)
	 * .failIfOctetStream()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * In both cases, {@code failIfOctetStream(false)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param fail
	 *            true to fail if mimetype returned by Tika is
	 *            application/octet-stream, if false then
	 *            application/octet-stream is used
	 * @return this instance for fluent chaining
	 */
	TikaBuilder<P> failIfOctetStream(Boolean fail);

	/**
	 * If Tika detection returns an {@code application/octet-stream}, it may
	 * means that detection was not enough accurate. In order to try other
	 * registered implementations that are able to detect mimetypes, you can set
	 * this to true.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some property keys and/or a default value.
	 * The aim is to let developer be able to externalize its configuration (using system properties, configuration file or anything else).
	 * If the developer doesn't configure any value for the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .failIfOctetStream()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #failIfOctetStream(Boolean)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .failIfOctetStream(false)
	 * .failIfOctetStream()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * The value {@code false} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	ConfigurationValueBuilder<TikaBuilder<P>, Boolean> failIfOctetStream();
}
