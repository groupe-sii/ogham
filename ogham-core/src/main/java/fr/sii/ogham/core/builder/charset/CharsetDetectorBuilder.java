package fr.sii.ogham.core.builder.charset;

import java.nio.charset.Charset;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.Parent;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.charset.CharsetDetector;

/**
 * Configures NIO charset detection. A charset detector tries to detect the NIO
 * charset used by the provided input. The charset detection is not
 * deterministic. For a same input string, several charsets may be detected. The
 * detector can provide the best charset or a list of possible charsets with a
 * confidence indication.
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
public interface CharsetDetectorBuilder<P> extends Parent<P>, Builder<CharsetDetector> {
	
	/**
	 * Set the default {@link Charset} that is used by the Java application.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #defaultCharset()}.
	 * 
	 * <pre>
	 * .defaultCharset("UTF-16")
	 * .defaultCharset()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("UTF-8")
	 * </pre>
	 * 
	 * <pre>
	 * .defaultCharset("UTF-16")
	 * .defaultCharset()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("UTF-8")
	 * </pre>
	 * 
	 * In both cases, {@code defaultCharset("UTF-16")} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param charsetName
	 *            the charset
	 * @return this instance for fluent chaining
	 */
	CharsetDetectorBuilder<P> defaultCharset(String charsetName);

	/**
	 * Set the default {@link Charset} that is used by the Java application.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some property keys and/or a default value.
	 * The aim is to let developer be able to externalize its configuration (using system properties, configuration file or anything else).
	 * If the developer doesn't configure any value for the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .defaultCharset()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("UTF-8")
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #defaultCharset(String)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .defaultCharset("UTF-16")
	 * .defaultCharset()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("UTF-8")
	 * </pre>
	 * 
	 * The value {@code "UTF-16"} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	ConfigurationValueBuilder<CharsetDetectorBuilder<P>, String> defaultCharset();
}
