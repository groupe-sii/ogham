package fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.compat;

import static java.util.stream.Collectors.toList;

import java.util.List;

import com.sendgrid.Email;
import com.sendgrid.Personalization;

/**
 * Compatibility wrapper that wraps {@link Personalization} instance and
 * delegates operations to it.
 * 
 * @author Aurélien Baudet
 * @see CompatUtil
 * @see CompatFactory
 */
public class WrongPackageNamePersonalizationCompat implements PersonalizationCompat {
	private final Personalization delegate;
	
	public WrongPackageNamePersonalizationCompat() {
		this(new Personalization());
	}

	public WrongPackageNamePersonalizationCompat(Personalization delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public void addTo(String address, String personal) {
		delegate.addTo(new Email(address, personal));
	}

	@Override
	public void addCc(String address, String personal) {
		delegate.addCc(new Email(address, personal));
	}

	@Override
	public void addBcc(String address, String personal) {
		delegate.addBcc(new Email(address, personal));
	}

	@Override
	public List<EmailCompat> getTos() {
		return delegate.getTos().stream()
				.map(WrongPackageNameEmailCompat::new)
				.collect(toList());
	}

	public Personalization getDelegate() {
		return delegate;
	}
}
