package fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.compat;

import com.sendgrid.helpers.mail.objects.Attachments;

/**
 * Compatibility wrapper that wraps {@link Attachments} instance and delegates
 * operations to it.
 * 
 * @author Aurélien Baudet
 * @see CompatUtil
 * @see CompatFactory
 */
public class CorrectPackageNameAttachmentsCompat implements AttachmentsCompat {
	private final Attachments delegate;

	public CorrectPackageNameAttachmentsCompat() {
		super();
		this.delegate = new Attachments();
	}

	@Override
	public void setContent(String encodeToString) {
		delegate.setContent(encodeToString);
	}

	@Override
	public void setContentId(String cid) {
		delegate.setContentId(cid);
	}

	@Override
	public void setDisposition(String disposition) {
		delegate.setDisposition(disposition);
	}

	@Override
	public void setFilename(String name) {
		delegate.setFilename(name);
	}

	@Override
	public void setType(String mimetype) {
		delegate.setType(mimetype);
	}

	public Attachments getDelegate() {
		return delegate;
	}

}
