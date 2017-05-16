package fr.sii.ogham.sms.builder.ovh;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilderDelegate;
import fr.sii.ogham.core.builder.env.SimpleEnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.util.BuilderUtils;
import fr.sii.ogham.sms.builder.SmsBuilder;
import fr.sii.ogham.sms.sender.impl.OvhSmsSender;
import fr.sii.ogham.sms.sender.impl.ovh.OvhAuthParams;
import fr.sii.ogham.sms.sender.impl.ovh.OvhOptions;

public class OvhSmsBuilder extends AbstractParent<SmsBuilder> implements Builder<OvhSmsSender> {
	private EnvironmentBuilder<OvhSmsBuilder> environmentBuilder;
	private List<String> urls;
	private List<String> accounts;
	private List<String> logins;
	private List<String> passwords;
	private OvhOptionsBuilder ovhOptionsBuilder;
	
	public OvhSmsBuilder() {
		this(null);
		environmentBuilder = new SimpleEnvironmentBuilder<>(this);
	}

	public OvhSmsBuilder(SmsBuilder parent) {
		super(parent);
		urls = new ArrayList<>();
		accounts = new ArrayList<>();
		logins = new ArrayList<>();
		passwords = new ArrayList<>();
	}

	public EnvironmentBuilder<OvhSmsBuilder> environment() {
		if(environmentBuilder==null) {
			environmentBuilder = new SimpleEnvironmentBuilder<>(this);
		}
		return environmentBuilder;
	}
	
	public OvhSmsBuilder environment(EnvironmentBuilder<?> builder) {
		environmentBuilder = new EnvironmentBuilderDelegate<>(this, builder);
		return this;
	}

	public OvhSmsBuilder url(String... url) {
		urls.addAll(Arrays.asList(url));
		return this;
	}

	public OvhSmsBuilder account(String... account) {
		accounts.addAll(Arrays.asList(account));
		return this;
	}

	public OvhSmsBuilder login(String... login) {
		logins.addAll(Arrays.asList(login));
		return this;
	}

	public OvhSmsBuilder password(String... password) {
		passwords.addAll(Arrays.asList(password));
		return this;
	}

	public OvhOptionsBuilder options() {
		if(ovhOptionsBuilder==null) {
			ovhOptionsBuilder = new OvhOptionsBuilder(this, environmentBuilder);
		}
		return ovhOptionsBuilder;
	}

	@Override
	public OvhSmsSender build() throws BuildException {
		PropertyResolver propertyResolver = environmentBuilder.build();
		URL url = buildUrl(propertyResolver);
		OvhAuthParams authParams = buildAuth(propertyResolver);
		OvhOptions options = buildOptions(propertyResolver);
		if(url==null || authParams.getAccount()==null || authParams.getLogin()==null || authParams.getPassword()==null) {
			return null;
		}
		return new OvhSmsSender(url, authParams, options);
	}

	private URL buildUrl(PropertyResolver propertyResolver) {
		try {
			String url = BuilderUtils.evaluate(urls, propertyResolver, String.class);
			if(url!=null) {
				return new URL(url);
			}
			return null;
		} catch(MalformedURLException e) {
			throw new BuildException("Failed to create OVH SMS sender due to invalid URL", e);
		}
	}

	private OvhAuthParams buildAuth(PropertyResolver propertyResolver) {
		String account = BuilderUtils.evaluate(accounts, propertyResolver, String.class);
		String login = BuilderUtils.evaluate(logins, propertyResolver, String.class);
		String password = BuilderUtils.evaluate(passwords, propertyResolver, String.class);
		return new OvhAuthParams(account, login, password);
	}

	private OvhOptions buildOptions(PropertyResolver propertyResolver) {
		return ovhOptionsBuilder.build();
	}
}
