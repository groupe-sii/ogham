package fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.compat;

/**
 * Wrapper interface that delegates operations to the real SendGrid Attchments
 * object. This is needed since {@code sendgrid-java} has issue with generated
 * packages.
 * 
 * 
 * @author Aurélien Baudet
 * @see CompatUtil
 */
public interface AttachmentsCompat {

	/**
	 * Set the attachment's content.
	 * 
	 * @param content
	 *            the content.
	 */
	void setContent(String content);

	/**
	 * Set the content ID.
	 * 
	 * @param contentId
	 *            the content ID.
	 */
	void setContentId(String contentId);

	/**
	 * Set the content-disposition of the attachment.
	 * 
	 * @param disposition
	 *            the disposition.
	 */
	void setDisposition(String disposition);

	/**
	 * Set the filename for this attachment.
	 * 
	 * @param filename
	 *            the filename.
	 */
	void setFilename(String filename);

	/**
	 * Set the mime type of the content.
	 * 
	 * @param type
	 *            the mime type.
	 */
	void setType(String type);

}
