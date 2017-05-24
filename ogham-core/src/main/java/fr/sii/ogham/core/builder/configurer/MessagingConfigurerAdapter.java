package fr.sii.ogham.core.builder.configurer;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilderDelegate;
import fr.sii.ogham.core.builder.mimetype.MimetypeDetectionBuilder;
import fr.sii.ogham.core.builder.mimetype.MimetypeDetectionBuilderDelegate;
import fr.sii.ogham.core.builder.resolution.ResourceResolutionBuilder;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.sms.builder.SmsBuilder;

/**
 * Adapter that configures a {@link MessagingBuilder} instance. It provides
 * configuration methods for each main part of the builder configuration:
 * 
 * <ul>
 * <li>Environment configuration (that may be inherited)</li>
 * <li>Resource resolution configuration (that may be inherited)</li>
 * <li>Mimetype detection configuration (that may be inherited)</li>
 * <li>Email configuration</li>
 * <li>Sms configuration</li>
 * </ul>
 * 
 * <p>
 * This class is intended to help developers configure {@link MessagingBuilder}
 * differently than default configuration if needed without too much pain.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public abstract class MessagingConfigurerAdapter implements MessagingConfigurer {

	@Override
	public void configure(MessagingBuilder builder) {
		configure(builder.environment());
		configure(builder.resource());
		configure(builder.mimetype());
		configure(builder.email());
		configure(builder.sms());
	}

	/**
	 * Configures global environment management (configuration properties
	 * resolution). The configured {@link EnvironmentBuilder} may be inherited
	 * by sub-builders by using same instance or using a derived one (see
	 * {@link EnvironmentBuilderDelegate}).
	 * 
	 * @param builder
	 *            the builder to configure
	 */
	public void configure(EnvironmentBuilder<?> builder) {
		// extension point
	}

	/**
	 * Configures global resource resolution. The configured
	 * {@link ResourceResolutionBuilder} may be inherited by sub-builders by
	 * using same instance or using a derived one.
	 * 
	 * @param builder
	 *            the builder to configure
	 */
	public void configure(ResourceResolutionBuilder<?> builder) {
		// extension point
	}

	/**
	 * Configures global mimetype detection. The configured
	 * {@link MimetypeDetectionBuilder} may be inherited by sub-builders by
	 * using same instance or using a derived one (see
	 * {@link MimetypeDetectionBuilderDelegate}).
	 * 
	 * @param mimetype
	 *            the builder to configure
	 */
	public void configure(MimetypeDetectionBuilder<?> mimetype) {
		// extension point
	}

	/**
	 * Configures common email handling:
	 * <ul>
	 * <li>Attachment handling</li>
	 * <li>Image inlining</li>
	 * <li>CSS inlining</li>
	 * <li>Template handling</li>
	 * <li>Autofill handling</li>
	 * <li>Sender implementations handling</li>
	 * </ul>
	 * 
	 * @param builder
	 *            the builder to configure
	 */
	public void configure(EmailBuilder builder) {
		// extension point
	}

	/**
	 * Configures common SMS handling:
	 * <ul>
	 * <li>Template handling</li>
	 * <li>Autofill handling</li>
	 * <li>Sender implementations handling</li>
	 * </ul>
	 * 
	 * @param builder
	 *            the builder to configure
	 */
	public void configure(SmsBuilder builder) {
		// extension point
	}

}
