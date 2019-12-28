package fr.sii.ogham.helper.sms.rule;

import java.util.List;

/**
 * Abstraction to wrap any SMPP server
 * 
 * @author Aurélien Baudet
 *
 * @param <M>
 *            the type of received messages
 */
public interface SmppServerSimulator<M> {
	/**
	 * Start the SMPP server.
	 * 
	 * @throws SmppServerException
	 *             when server couldn't be started
	 */
	void start() throws SmppServerException;

	/**
	 * Stop the SMPP server.
	 * 
	 * @throws SmppServerException
	 *             when the server couldn't be stopped
	 */
	void stop() throws SmppServerException;

	/**
	 * The actual port used by the SMPP server.
	 * 
	 * @return the port
	 */
	int getPort();

	/**
	 * Get the list of received messages.
	 * 
	 * @return the received messages
	 */
	List<M> getReceivedMessages();
}
