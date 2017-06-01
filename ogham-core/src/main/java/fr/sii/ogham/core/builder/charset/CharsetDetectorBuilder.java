package fr.sii.ogham.core.builder.charset;

import java.nio.charset.Charset;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.Parent;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
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
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .defaultCharset("UTF-8");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .defaultCharset("${custom.property.high-priority}", "${custom.property.low-priority}");
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
	 * @param charsets
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	CharsetDetectorBuilder<P> defaultCharset(String... charsets);
}
