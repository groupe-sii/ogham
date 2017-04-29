package fr.sii.ogham.core.util;

import java.util.Properties;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.convert.DefaultConverter;
import fr.sii.ogham.core.env.JavaPropertiesResolver;
import fr.sii.ogham.core.env.PropertyResolver;

/**
 * Helper class for {@link Builder} implementations. It separates the builder
 * implementations from the environment.
 * 
 * @author Aurélien Baudet
 * @see Builder
 */
public final class BuilderUtils {
	/**
	 * Provide the default properties. For now, it provides only
	 * {@link System#getProperties()}. But according to the environment or the
	 * future of the module, properties may come from other source.
	 * 
	 * @return the default properties
	 */
	public static Properties getDefaultProperties() {
		return System.getProperties();
	}

	/**
	 * Create the {@link PropertyResolver} that handles {@link Properties}.
	 * 
	 * @param properties
	 *            the properties
	 * @return the property resolver
	 */
	public static JavaPropertiesResolver getDefaultPropertyResolver(Properties properties) {
		return new JavaPropertiesResolver(properties, new DefaultConverter());
	}

	private BuilderUtils() {
		super();
	}
}
