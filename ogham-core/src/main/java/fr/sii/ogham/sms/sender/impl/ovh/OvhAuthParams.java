package fr.sii.ogham.sms.sender.impl.ovh;

/**
 * Authentication parameters required by OVH.
 * 
 * @author Aurélien Baudet
 *
 */
public class OvhAuthParams {
	/**
	 * The SMS account (format sms-nic-X)
	 */
	private final String account;

	/**
	 * The SMS user login associated to the account
	 */
	private final String login;

	/**
	 * The user password
	 */
	private final String password;

	public OvhAuthParams(String account, String login, String password) {
		super();
		this.account = account;
		this.login = login;
		this.password = password;
	}

	public String getAccount() {
		return account;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}
}