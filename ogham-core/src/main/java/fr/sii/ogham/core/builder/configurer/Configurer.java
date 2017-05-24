package fr.sii.ogham.core.builder.configurer;

/**
 * Apply specific configuration to the provided builder instance.
 * 
 * <p>
 * This is useful to separate some parts of builder configuration.
 * 
 * @author Aurélien Baudet
 *
 * @param <B>
 *            the type of the builder to configure
 */
public interface Configurer<B> {
	/**
	 * Apply configuration on the provided builder
	 * 
	 * @param builder
	 *            the builder to configure
	 */
	void configure(B builder);
}
