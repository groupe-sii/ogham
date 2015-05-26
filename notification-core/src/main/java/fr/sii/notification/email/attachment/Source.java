package fr.sii.notification.email.attachment;

/**
 * The source of an attachment abstracts the way to access to the real
 * attachment content. It provides also the name to display in the mail.
 * 
 * @author Aurélien Baudet
 *
 */
public interface Source {
	/**
	 * Get the name for the attachment.
	 * 
	 * @return the name of the attachment
	 */
	public String getName();
}
