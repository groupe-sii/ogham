package fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.compat;

/**
 * Compatibility factory that creates compatibility wrappers.
 * 
 * @author Aurélien Baudet
 * @see CompatUtil
 */
public class WrongPackageNameCompatFactory implements CompatFactory {

	@Override
	public MailCompat newMail() {
		return new WrongPackageNameMailCompat();
	}

	@Override
	public AttachmentsCompat newAttachments() {
		return new WrongPackageNameAttachmentsCompat();
	}

	@Override
	public PersonalizationCompat newPersonalization() {
		return new WrongPackageNamePersonalizationCompat();
	}

}
