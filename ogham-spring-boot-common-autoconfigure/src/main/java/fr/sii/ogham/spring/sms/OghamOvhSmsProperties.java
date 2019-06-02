package fr.sii.ogham.spring.sms;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import fr.sii.ogham.sms.sender.impl.ovh.SmsCoding;

@ConfigurationProperties("ogham.sms.ovh")
public class OghamOvhSmsProperties {
	/**
	 * URL of the OVH SMS HTTP API
	 */
	private String url = "https://www.ovh.com/cgi-bin/sms/http2sms.cgi";
	/**
	 * The OVH account identifier
	 */
	private String account;
	/**
	 * THe OVH username
	 */
	private String login;
	/**
	 * The OVH password
	 */
	private String password;
	@NestedConfigurationProperty
	private Options options = new Options();

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
		/**
		 * Enable/disable "STOP" indication at the end of the message (useful to
		 * disable for non-commercial SMS).
		 */
		private boolean noStop = true;
		/**
		 * Set the message encoding: NORMAL for 7bit encoding and UTF_8 for 8bit
		 * encoding.
		 * 
		 * If you use UTF-8, your SMS will have a maximum size of 70
		 * characters instead of 160
		 */
		private SmsCoding smsCoding;
		/**
		 * Set a tag to mark sent messages (20 maximum character string)
		 */
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
