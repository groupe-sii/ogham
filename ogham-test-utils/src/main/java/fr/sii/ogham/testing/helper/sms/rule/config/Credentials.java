package fr.sii.ogham.testing.helper.sms.rule.config;

/**
 * Server credentials
 * 
 * @author Aurélien Baudet
 *
 */
public class Credentials {
	private final String systemId;
	private final String password;

	public Credentials(String systemId, String password) {
		super();
		this.systemId = systemId;
		this.password = password;
	}

	public String getSystemId() {
		return systemId;
	}

	public String getPassword() {
		return password;
	}
}
