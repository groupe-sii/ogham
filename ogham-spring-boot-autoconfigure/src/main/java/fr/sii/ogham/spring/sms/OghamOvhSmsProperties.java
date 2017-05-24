package fr.sii.ogham.spring.sms;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import fr.sii.ogham.sms.sender.impl.ovh.SmsCoding;

@ConfigurationProperties("ogham.sms.ovh")
public class OghamOvhSmsProperties {
	private String url = "https://www.ovh.com/cgi-bin/sms/http2sms.cgi";
	private String account;
	private String login;
	private String password;
	@NestedConfigurationProperty
	private Options options;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Options getOptions() {
		return options;
	}

	public void setOptions(Options options) {
		this.options = options;
	}

	public static class Options {
		private boolean noStop = true;
		private SmsCoding smsCoding;
		private String tag;

		public boolean isNoStop() {
			return noStop;
		}

		public void setNoStop(boolean noStop) {
			this.noStop = noStop;
		}

		public SmsCoding getSmsCoding() {
			return smsCoding;
		}

		public void setSmsCoding(SmsCoding smsCoding) {
			this.smsCoding = smsCoding;
		}

		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}
	}
}
