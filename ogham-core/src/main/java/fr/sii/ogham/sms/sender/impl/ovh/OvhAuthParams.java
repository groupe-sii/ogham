package fr.sii.ogham.sms.sender.impl.ovh;

public class OvhAuthParams {
	private final String account;
	
	private final String login;
	
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