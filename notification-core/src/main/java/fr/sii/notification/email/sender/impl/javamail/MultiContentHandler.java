package fr.sii.notification.email.sender.impl.javamail;

import javax.mail.Multipart;
import javax.mail.internet.MimePart;

import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.message.content.MultiContent;
import fr.sii.notification.email.exception.javamail.ContentHandlerException;

/**
 * Handle multiple contents. It adds several parts to the mail. It creates a
 * part for each sub content. It delegates the management of each sub content to
 * another content handler.
 * 
 * @author Aurélien Baudet
 *
 */
public class MultiContentHandler implements JavaMailContentHandler {
	/**
	 * The content handler used for each sub content
	 */
	private JavaMailContentHandler delegate;

	public MultiContentHandler(JavaMailContentHandler delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public void setContent(MimePart message, Multipart multipart, Content content) throws ContentHandlerException {
		MultiContent multiContent = (MultiContent) content;
		for (Content c : multiContent.getContents()) {
			delegate.setContent(message, multipart, c);
		}
	}

}
