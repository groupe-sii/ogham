package fr.sii.ogham.sms.builder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.sms.SmsConstants.OvhConstants;
import fr.sii.ogham.sms.sender.impl.OvhSmsSender;
import fr.sii.ogham.sms.sender.impl.ovh.OvhAuthParams;
import fr.sii.ogham.sms.sender.impl.ovh.OvhOptions;
import fr.sii.ogham.sms.sender.impl.ovh.SmsCoding;

public class OvhSmsBuilder implements Builder<OvhSmsSender> {

	private Properties properties;
	
	private OvhAuthParams authParams;
	
	private OvhOptions options;
	
	private URL ovhUrl;

	@Override
	public OvhSmsSender build() throws BuildException {
		try {
			if(ovhUrl==null) {
				ovhUrl = new URL(OvhConstants.HTTP_API_URL);
			}
			if(authParams==null) {
				authParams = new OvhAuthParams(properties.getProperty(OvhConstants.ACCOUNT_PROPERTY), 
												properties.getProperty(OvhConstants.LOGIN_PROPERTY), 
												properties.getProperty(OvhConstants.PASSWORD_PROPERTY));
			}
			if(options==null) {
				String noStop = properties.getProperty(OvhConstants.NO_STOP_PROPERTY);
				String smsCoding = properties.getProperty(OvhConstants.SMS_CODING_PROPERTY);
				options = new OvhOptions(noStop==null ? true : Boolean.valueOf(noStop), 
											properties.getProperty(OvhConstants.TAG_PROPERTY), 
											smsCoding==null ? null : SmsCoding.valueOf(smsCoding));
			}
			return new OvhSmsSender(ovhUrl, authParams, options);
		} catch(MalformedURLException e) {
			throw new BuildException("Invalid URL for OVH API", e);
		}
	}
	
	public OvhSmsBuilder useDefaults(Properties properties) {
		withProperties(properties);
		return this;
	}

	public OvhSmsBuilder withProperties(Properties properties) {
		this.properties = properties;
		return this;
	}

	public OvhSmsBuilder withUrl(URL ovhUrl) {
		this.ovhUrl = ovhUrl;
		return this;
	}

	public OvhSmsBuilder withAuthParams(OvhAuthParams authParams) {
		this.authParams = authParams;
		return this;
	}

	public OvhSmsBuilder withOptions(OvhOptions options) {
		this.options = options;
		return this;
	}

	public OvhSmsBuilder withOvhUrl(URL ovhUrl) {
		this.ovhUrl = ovhUrl;
		return this;
	}

}
