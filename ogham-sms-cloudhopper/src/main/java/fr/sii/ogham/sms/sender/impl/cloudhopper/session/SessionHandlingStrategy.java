package fr.sii.ogham.sms.sender.impl.cloudhopper.session;

import com.cloudhopper.smpp.SmppSession;

import fr.sii.ogham.core.clean.Cleanable;
import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.SmppException;

/**
 * There exists several ways to manage SMPP session between the client and the
 * server:
 * 
 * <ul>
 * <li>A new session can be created for each message (useful when message needs
 * to be sent only sometimes)</li>
 * <li>A session can be reused if possible until no message has been sent for a
 * while (useful when several messages have to be sent in a row)</li>
 * <li>Keep the session always alive (useful when application needs to send
 * messages quickly and often)</li>
 * <li>...</li>
 * </ul>
 * 
 * This interface defines the entrypoints that need to be implemented to handle
 * a session.
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public interface SessionHandlingStrategy extends Cleanable {
	/**
	 * The client needs a session to be able to send the message.
	 * 
	 * <p>
	 * Either create a new session or use an existing one.
	 * 
	 * @return the session
	 * @throws SmppException
	 *             when the session can't be created (connection failure)
	 */
	SmppSession getSession() throws SmppException;

	/**
	 * Fired when a message has been sent successfully. This entrypoint may be
	 * useful to do some cleanup or to start background tasks for example.
	 * 
	 * @param sms
	 *            the sent SMS
	 * @throws MessageException
	 *             when something happens during handling of message success
	 */
	void messageSent(Sms sms) throws MessageException;

	/**
	 * Fired when the message couldn't be sent due to an error. This entrypoint
	 * may be useful to do some cleanup or to start background tasks for
	 * example.
	 * 
	 * @param sms
	 *            the SMS that couldn't be sent
	 * @param e
	 *            the raied error while sending
	 * @throws MessageException
	 *             when something happens during handling of message error
	 */
	void messageNotSent(Sms sms, SmppException e) throws MessageException;

	/**
	 * Fired when a message has been processed i.e. either when message has been
	 * sent or when an error has been raised. This entrypoint may be useful to
	 * do some cleanup or to start background tasks for example.
	 * 
	 * <p>
	 * This method is always called after {@link #messageSent(Sms)} and
	 * {@link #messageNotSent(Sms, SmppException)}.
	 * 
	 * <p>
	 * No exception should be thrown because this is used to end the process.
	 * 
	 * @param sms
	 *            the sent SMS
	 */
	void messageProcessed(Sms sms);
}
