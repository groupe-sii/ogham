package fr.sii.ogham.sms.builder.cloudhopper;

import com.cloudhopper.smpp.ssl.SslConfiguration;

import fr.sii.ogham.core.builder.BuildContext;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.fluent.AbstractParent;

/**
 * Enable or disable SSL configuration and configure how SSL is handled.
 * 
 * See
 * <a href="https://github.com/fizzed/cloudhopper-smpp/blob/master/SSL.md">How
 * to use SSL with cloudhopper-smpp</a>
 * 
 * @author Aurélien Baudet
 *
 */
public class SslBuilder extends AbstractParent<CloudhopperBuilder> implements Builder<SslConfiguration> {
	private final ConfigurationValueBuilderHelper<SslBuilder, Boolean> enableSslValueBuilder;
	private SslConfiguration sslConfiguration;

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method. The {@link EnvironmentBuilder} is
	 * used to evaluate properties when {@link #build()} method is called.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param buildContext
	 *            for property resolution and evaluation
	 */
	public SslBuilder(CloudhopperBuilder parent, BuildContext buildContext) {
		super(parent);
		enableSslValueBuilder = new ConfigurationValueBuilderHelper<>(this, Boolean.class, buildContext);
	}

	/**
	 * Enable or disable SSL.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #enable()}.
	 * 
	 * <pre>
	 * .enable(true)
	 * .enable()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * <pre>
	 * .enable(true)
	 * .enable()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * In both cases, {@code enable(true)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param enale
	 *            true to enable SSL, false to disable
	 * @return this instance for fluent chaining
	 */
	public SslBuilder enable(Boolean enale) {
		enableSslValueBuilder.setValue(enale);
		return this;
	}

	/**
	 * Enable or disable SSL.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .enable()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #enable(Boolean)} takes precedence over
	 * property values and default value.
	 * 
	 * <pre>
	 * .enable(true)
	 * .enable()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * The value {@code true} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<SslBuilder, Boolean> enable() {
		return enableSslValueBuilder;
	}

	/**
	 * Configure SSL handling.
	 * 
	 * See <a href=
	 * "https://github.com/fizzed/cloudhopper-smpp/blob/master/SSL.md">How to
	 * use SSL with cloudhopper-smpp</a>
	 * 
	 * If this method is called several times, only the last configuration is
	 * used.
	 * 
	 * @param sslConfiguration
	 *            the new SSL configuration
	 * @return this instance for fluent chaining
	 */
	public SslBuilder sslConfiguration(SslConfiguration sslConfiguration) {
		this.sslConfiguration = sslConfiguration;
		return this;
	}

	@Override
	public SslConfiguration build() {
		boolean enabled = enableSslValueBuilder.getValue(false);
		return enabled ? sslConfiguration : null;
	}

}
