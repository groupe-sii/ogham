package fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.compat;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.List;

import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;


/**
 * Compatibility wrapper that wraps {@link Mail} instance and delegates
 * operations to it.
 * 
 * @author Aurélien Baudet
 * @see CompatUtil
 * @see CompatFactory
 */
public class CorrectPackageNameMailCompat implements MailCompat {
	private final Mail delegate;

	public CorrectPackageNameMailCompat() {
		this(new Mail());
	}

	public CorrectPackageNameMailCompat(Mail delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public EmailCompat getFrom() {
		Email from = delegate.getFrom();
		if (from == null) {
			return null;
		}
		return new CorrectPackageNameEmailCompat(from);
	}

	@Override
	public String getSubject() {
		return delegate.getSubject();
	}

	@Override
	public List<PersonalizationCompat> getPersonalization() {
		List<Personalization> personalization = delegate.getPersonalization();
		if (personalization == null) {
			return null;
		}
		return personalization.stream()
				.map(CorrectPackageNamePersonalizationCompat::new)
				.collect(toList());
	}

	@Override
	public void setSubject(String subject) {
		delegate.setSubject(subject);
	}

	@Override
	public void setFrom(String address, String personal) {
		delegate.setFrom(new Email(address, personal));
	}

	@Override
	public void addContent(String mime, String contentStr) {
		delegate.addContent(new Content(mime, contentStr));
	}

	@Override
	public void addPersonalization(PersonalizationCompat personalization) {
		delegate.addPersonalization(((CorrectPackageNamePersonalizationCompat) personalization).getDelegate());
	}

	@Override
	public void addAttachments(AttachmentsCompat attachment) {
		delegate.addAttachments(((CorrectPackageNameAttachmentsCompat) attachment).getDelegate());
	}

	@Override
	public String build() throws IOException {
		return delegate.build();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <M> M getDelegate() {
		return (M) delegate;
	}
}
