package fr.sii.ogham.core.builder.env;

import java.util.Properties;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.Parent;

/**
 * Registers custom properties (see {@link Properties}).
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
public interface PropertiesBuilder<P> extends Parent<P>, Builder<Properties> {
	/**
	 * Sets a property (key/value pair). Calling several times this method with
	 * the same key overrides any previously defined value.
	 * 
	 * @param key
	 *            the property key
	 * @param value
	 *            the property value
	 * @return this instance for fluent chaining
	 */
	PropertiesBuilder<P> set(String key, String value);
}
