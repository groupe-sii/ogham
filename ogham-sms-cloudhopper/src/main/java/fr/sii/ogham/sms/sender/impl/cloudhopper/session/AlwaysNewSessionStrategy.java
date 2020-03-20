package fr.sii.ogham.sms.sender.impl.cloudhopper.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudhopper.smpp.SmppSession;

import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.exception.clean.CleanException;
import fr.sii.ogham.core.retry.RetryExecutor;
import fr.sii.ogham.sms.builder.cloudhopper.SmppClientSupplier;
import fr.sii.ogham.sms.builder.cloudhopper.SmppSessionHandlerSupplier;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.sender.impl.cloudhopper.ExtendedSmppSessionConfiguration;
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.SmppException;

/**
 * Simple management of SMPP session.
 * 
 * For each message:
 * <ol>
 * <li>Open a SMPP session</li>
 * <li>Send the message</li>
 * <li>Close the SMPP session</li>
 * </ol>
 * 
 * <p>
 * Only one message can be sent in the same time.
 * 
 * <p>
 * If an error is raised, the exception is thrown and the SMS is not sent.
 * 
 * @author Aurélien Baudet
 *
 */
public class AlwaysNewSessionStrategy extends BaseSessionHandlingStrategy {
	private static final Logger LOG = LoggerFactory.getLogger(AlwaysNewSessionStrategy.class);

	public AlwaysNewSessionStrategy(ExtendedSmppSessionConfiguration configuration, SmppClientSupplier clientSupplier, SmppSessionHandlerSupplier smppSessionHandlerSupplier, RetryExecutor retry) {
		super(LOG, configuration, clientSupplier, smppSessionHandlerSupplier, retry);
	}

	@Override
	public SmppSession getSession() throws SmppException {
		destroySession();
		destroyClient();
		initClient();
		initSession();
		return currentSession;
	}

	@Override
	public void messageSent(Sms sms) throws MessageException {
		// nothing to do
	}

	@Override
	public void messageNotSent(Sms sms, SmppException e) throws MessageException {
		throw new MessageException("Failed to send SMS", sms, e);
	}

	@Override
	public void messageProcessed(Sms sms) {
		destroySession();
		destroyClient();
	}

	@Override
	public void clean() throws CleanException {
		destroySession();
		destroyClient();
	}
}
