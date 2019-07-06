package fr.sii.ogham.core.builder.mimetype;

import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.Parent;
import fr.sii.ogham.core.builder.charset.CharsetDetectorBuilder;
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
 * returned (see {@link TikaBuilder#failIfOctetStream(boolean)}). This is useful
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
	 * @param fail
	 *            true to fail if mimetype returned by Tika is
	 *            application/octet-stream, if false then
	 *            application/octet-stream is used
	 * @return this instance for fluent chaining
	 */
	TikaBuilder<P> failIfOctetStream(boolean fail);

	/**
	 * Tika detection based on string may lead to inaccurate results
	 * because the encoding used to read the content differs from
	 * the encoding used to convert string to byte array.
	 * 
	 * Example of usage to define a default charset based on priority
	 * on a property and UTF-8 by default:
	 * <pre>
	 * <code>
	 * charset()
	 *   .defaultCharset("${ogham.mimetype.tika.default-charset}", "UTF-8")
	 * </code>
	 * </pre>
	 * 
	 * @return builder to configure charset automatic detection
	 */
	CharsetDetectorBuilder<TikaBuilder<P>> charset();
	
	/**
	 * NOTE: this is mostly for advance usage (when creating a custom module).
	 * 
	 * Inherits environment configuration from another builder. This is useful
	 * for configuring independently different parts of Ogham but keeping a
	 * whole coherence.
	 * 
	 * The same instance is shared meaning that all changes done here will also
	 * impact the other builder.
	 * 
	 * <p>
	 * If a previous builder was defined (by calling {@link #charset()} for
	 * example), the new builder will override it.
	 * 
	 * @param builder
	 *            the builder to inherit
	 * @return this instance for fluent chaining
	 */
	TikaBuilder<P> charset(CharsetDetectorBuilder<?> builder);
}
