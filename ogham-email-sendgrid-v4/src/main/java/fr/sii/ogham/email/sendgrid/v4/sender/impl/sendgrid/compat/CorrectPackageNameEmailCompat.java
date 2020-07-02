package fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.compat;

import com.sendgrid.helpers.mail.objects.Email;

/**
 * Compatibility wrapper that wraps {@link Email} instance and delegates
 * operations to it.
 * 
 * @author Aurélien Baudet
 * @see CompatUtil
 * @see CompatFactory
 */
public class CorrectPackageNameEmailCompat implements EmailCompat {
	private final Email delegate;

	public CorrectPackageNameEmailCompat() {
		this(new Email());
	}

	public CorrectPackageNameEmailCompat(Email delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public String getName() {
		if (delegate == null) {
			return null;
		}
		return delegate.getName();
	}

	@Override
	public String getEmail() {
		if (delegate == null) {
			return null;
		}
		return delegate.getEmail();
	}

}
