package fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.compat;

/**
 * Compatibility factory that creates compatibility wrappers.
 * 
 * @author Aurélien Baudet
 * @see CompatUtil
 */
public class CorrectPackageNameCompatFactory implements CompatFactory {

	@Override
	public MailCompat newMail() {
		return new CorrectPackageNameMailCompat();
	}

	@Override
	public AttachmentsCompat newAttachments() {
		return new CorrectPackageNameAttachmentsCompat();
	}

	@Override
	public PersonalizationCompat newPersonalization() {
		return new CorrectPackageNamePersonalizationCompat();
	}

}
