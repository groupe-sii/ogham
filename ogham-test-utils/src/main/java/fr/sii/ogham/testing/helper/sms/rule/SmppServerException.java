package fr.sii.ogham.testing.helper.sms.rule;

public class SmppServerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8223968724342714763L;

	public SmppServerException(String message, Throwable cause) {
		super(message, cause);
	}

	public SmppServerException(String message) {
		super(message);
	}

	public SmppServerException(Throwable cause) {
		super(cause);
	}

}
