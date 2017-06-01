package fr.sii.ogham.email;

public final class SendGridConstants {
	/**
	 * The configurer has a priority of 30000 in order to be applied after
	 * templating configurers and JavaMail configurer.
	 */
	public static final int DEFAULT_SENDGRID_CONFIGURER_PRIORITY = 30000;

	private SendGridConstants() {
		super();
	}
}
