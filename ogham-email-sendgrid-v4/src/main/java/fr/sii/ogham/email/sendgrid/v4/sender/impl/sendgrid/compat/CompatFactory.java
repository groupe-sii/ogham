package fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.compat;

/**
 * {@code sendgrid-java} package version 4.3.0 declares some classes in package
 * {@code com.sendgrid.helpers.mail.objects} (according to source folder
 * structure). However, the {@code package} declaration in every of these
 * classes points to {@code com.sendgrid}.
 * 
 * This creates {@link ClassNotFoundException}s when trying to run Ogham with
 * {@code sendgrid-java} v4.3.0. As it is a mistake from SendGrid, we could
 * simply take the next version that fixes the issue. But some Spring Boot
 * versions (like 2.1.x) has direct dependency to {@code sendgrid-java} v4.3.0.
 * So we must provide a compatibility fix in order to make it work with older
 * Spring Boot versions.
 * 
 * This interface creates wrapper instances around real SendGrid instances.
 * 
 * @author Aurélien Baudet
 * @see CompatUtil
 */
public interface CompatFactory {

	/**
	 * Create a wrapper instance that creates a new Mail instance (either
	 * {@code com.sendgrid.helpers.mail.Mail} or {@code com.sendgrid.Mail} for
	 * v4.3.0).
	 * 
	 * @return the wrapper instance
	 */
	MailCompat newMail();

	/**
	 * Create a wrapper instance that creates a new Attachments instance (either
	 * {@code com.sendgrid.helpers.mail.objects.Attachments} or
	 * {@code com.sendgrid.Attachments} for v4.3.0).
	 * 
	 * @return the wrapper instance
	 */
	AttachmentsCompat newAttachments();

	/**
	 * Create a wrapper instance that creates a new Personalization instance
	 * (either {@code com.sendgrid.helpers.mail.objects.Personalization} or
	 * {@code com.sendgrid.Personalization} for v4.3.0).
	 * 
	 * @return the wrapper instance
	 */
	PersonalizationCompat newPersonalization();

}
