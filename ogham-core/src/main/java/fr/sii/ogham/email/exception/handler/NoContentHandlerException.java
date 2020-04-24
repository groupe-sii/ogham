package fr.sii.ogham.email.exception.handler;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.sender.MessageSender;

/**
 * When a message is about to be sent by a {@link MessageSender} implementation,
 * the original message is converted to the format needed by the underlying
 * protocol/library. This conversion is done by implementations such as
 * JavaMail, SendGrid or anything else through a content handler (dedicated to
 * each implementation). The purpose of each content handler is to handle a
 * particular {@link Content}.
 * 
 * This exception is thrown when a {@link Content} implementation doesn't have a
 * content handler that is able to handle it.
 * 
 * @author Aurélien Baudet
 *
 */
public class NoContentHandlerException extends ContentHandlerException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public NoContentHandlerException(String message, Content content) {
		super(message, content);
	}

}
