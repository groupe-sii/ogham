package fr.sii.ogham.sms.builder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.util.BuilderUtils;
import fr.sii.ogham.sms.SmsConstants.OvhConstants;
import fr.sii.ogham.sms.sender.impl.OvhSmsSender;
import fr.sii.ogham.sms.sender.impl.ovh.OvhAuthParams;
import fr.sii.ogham.sms.sender.impl.ovh.OvhOptions;
import fr.sii.ogham.sms.sender.impl.ovh.SmsCoding;

/**
 * Builder that helps to construct the OVH web service implementation.
 * 
 * @author Aurélien Baudet
 *
 */
public class OvhSmsBuilder implements Builder<OvhSmsSender> {
	/**
	 * The properties to use
	 */
	private PropertyResolver propertyResolver;
	
	/**
	 * The required OVH authentication parameters
	 */
	private OvhAuthParams authParams;
	
	/**
	 * The OVH options
	 */
	private OvhOptions options;
	
	/**
	 * The OVH web service URL
	 */
	private URL ovhUrl;

	@Override
	public OvhSmsSender build() throws BuildException {
		try {
			// initialize default url
			if(ovhUrl==null) {
				ovhUrl = new URL(OvhConstants.HTTP_API_URL);
			}
			// initialize default authentication parameters by reading values from properties 
			if(authParams==null) {
				authParams = new OvhAuthParams(propertyResolver.getProperty(OvhConstants.ACCOUNT_PROPERTY), 
												propertyResolver.getProperty(OvhConstants.LOGIN_PROPERTY), 
												propertyResolver.getProperty(OvhConstants.PASSWORD_PROPERTY));
			}
			// initialize default options using values from properties
			if(options==null) {
				String noStop = propertyResolver.getProperty(OvhConstants.NO_STOP_PROPERTY);
				String smsCoding = propertyResolver.getProperty(OvhConstants.SMS_CODING_PROPERTY);
				options = new OvhOptions(noStop==null ? true : Boolean.valueOf(noStop), 
											propertyResolver.getProperty(OvhConstants.TAG_PROPERTY), 
											smsCoding==null ? null : SmsCoding.valueOf(smsCoding));
			}
			// create sender implementation
			return new OvhSmsSender(ovhUrl, authParams, options);
		} catch(MalformedURLException e) {
			throw new BuildException("Invalid URL for OVH API", e);
		}
	}
	
	/**
	 * Tells the builder to use all default behaviors and values:
	 * <ul>
	 * <li>Use the provided properties</li>
	 * <li>Initialize OVH authentication using provided properties</li>
	 * <li>Initialize OVH options using provided properties</li>
	 * </ul>
	 * 
	 * @param properties
	 *            the properties to use
	 * @return this instance for fluent use
	 */
	public OvhSmsBuilder useDefaults(Properties properties) {
		return useDefaults(BuilderUtils.getDefaultPropertyResolver(properties));
	}
	
	/**
	 * Tells the builder to use all default behaviors and values:
	 * <ul>
	 * <li>Use the provided properties</li>
	 * <li>Initialize OVH authentication using provided properties</li>
	 * <li>Initialize OVH options using provided properties</li>
	 * </ul>
	 * 
	 * @param propertyResolver
	 *            the property resolver used to get properties values
	 * @return this instance for fluent use
	 */
	public OvhSmsBuilder useDefaults(PropertyResolver propertyResolver) {
		withProperties(propertyResolver);
		return this;
	}

	/**
	 * Set the properties to use for configuring OVH implementation.
	 * <p>
	 * Automatically called by {@link #useDefaults(Properties)}
	 * </p>
	 * 
	 * @param properties
	 *            the properties to use
	 * @return this instance for fluent use
	 */
	public OvhSmsBuilder withProperties(Properties properties) {
		return withProperties(BuilderUtils.getDefaultPropertyResolver(properties));
	}
	
	/**
	 * Set the properties to use for configuring OVH implementation.
	 * <p>
	 * Automatically called by {@link #useDefaults(Properties)}
	 * </p>
	 * 
	 * @param propertyResolver
	 *            the property resolver used to get properties values
	 * @return this instance for fluent use
	 */
	public OvhSmsBuilder withProperties(PropertyResolver propertyResolver) {
		this.propertyResolver = propertyResolver;
		return this;
	}

	/**
	 * Set the URL of the OVH web service.
	 * 
	 * @param ovhUrl
	 *            the URL of the OVH web service
	 * @return this instance for fluent use
	 */
	public OvhSmsBuilder withUrl(URL ovhUrl) {
		this.ovhUrl = ovhUrl;
		return this;
	}

	/**
	 * Set the authentication parameters (OVH account, login and password).
	 * 
	 * @param authParams
	 *            the authentication parameters
	 * @return this instance for fluent use
	 */
	public OvhSmsBuilder withAuthParams(OvhAuthParams authParams) {
		this.authParams = authParams;
		return this;
	}

	/**
	 * Set the OVH options.
	 * 
	 * @param options
	 *            the OVH options
	 * @return this instance for fluent use
	 */
	public OvhSmsBuilder withOptions(OvhOptions options) {
		this.options = options;
		return this;
	}

}
