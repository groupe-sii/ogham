package fr.sii.ogham.core.builder.mimetype;

import java.io.InputStream;
import java.nio.file.Files;

import javax.activation.MimetypesFileTypeMap;

import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.Parent;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
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
	 * {@link TikaBuilder#failIfOctetStream(boolean)}). This is useful in order
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
	 * If no previously registered mimetype detector could determine mimetype,
	 * use a default value.
	 * 
	 * <p>
	 * You can specify a direct value as default mimetype. For example:
	 * 
	 * <pre>
	 * .defaultMimetype("text/plain");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .defaultMimetype("${custom.property.high-priority}", "${custom.property.low-priority}");
	 * </pre>
	 * 
	 * The properties are not immediately evaluated. The evaluation will be done
	 * when the {@link #build()} method is called.
	 * 
	 * If you provide several property keys, evaluation will be done on the
	 * first key and if the property exists (see {@link EnvironmentBuilder}),
	 * its value is used. If the first property doesn't exist in properties,
	 * then it tries with the second one and so on.
	 * 
	 * @param mimetype
	 *            one mimetype value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	MimetypeDetectionBuilder<P> defaultMimetype(String... mimetype);
}
