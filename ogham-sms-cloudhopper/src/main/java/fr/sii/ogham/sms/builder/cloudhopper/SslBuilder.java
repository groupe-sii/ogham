package fr.sii.ogham.sms.builder.cloudhopper;

import com.cloudhopper.smpp.ssl.SslConfiguration;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.exception.builder.BuildException;

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
	private SslConfiguration sslConfiguration;
	private boolean enableSsl;

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method.
	 * 
	 * @param parent
	 *            the parent builder
	 */
	public SslBuilder(CloudhopperBuilder parent) {
		super(parent);
	}

	/**
	 * Enable or disable SSL.
	 * 
	 * @param enableSsl
	 *            true to enable SSL, false to disable
	 * @return this instance for fluent chaining
	 */
	public SslBuilder enable(boolean enableSsl) {
		this.enableSsl = enableSsl;
		return this;
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
	public SslConfiguration build() throws BuildException {
		return enableSsl ? null : sslConfiguration;
	}

}
