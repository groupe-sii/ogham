package fr.sii.ogham.sms.sender.impl;

import static fr.sii.ogham.core.util.LogUtils.logString;
import static fr.sii.ogham.sms.CloudhopperConstants.DEFAULT_CLOUDHOPPER_IMPLEMENTATION_PRIORITY;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.type.RecoverablePduException;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;

import fr.sii.ogham.core.builder.priority.Priority;
import fr.sii.ogham.core.clean.Cleanable;
import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.exception.clean.CleanException;
import fr.sii.ogham.core.sender.AbstractSpecializedSender;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.sender.impl.cloudhopper.ExtendedSmppSessionConfiguration;
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.SmppException;
import fr.sii.ogham.sms.sender.impl.cloudhopper.preparator.MessagePreparator;
import fr.sii.ogham.sms.sender.impl.cloudhopper.session.SessionHandlingStrategy;

/**
 * Implementation based on
 * <a href="https://github.com/twitter/cloudhopper-smpp">cloudhopper-smpp</a>
 * library.
 * 
 * @author Aurélien Baudet
 */
@Priority(properties="${ogham.sms.implementation-priority.cloudhopper}", defaultValue = DEFAULT_CLOUDHOPPER_IMPLEMENTATION_PRIORITY)
public class CloudhopperSMPPSender extends AbstractSpecializedSender<Sms> implements Cleanable {
	private static final Logger LOG = LoggerFactory.getLogger(CloudhopperSMPPSender.class);
	
	private final ExtendedSmppSessionConfiguration configuration;
	private final SessionHandlingStrategy sessionHandler;
	private final MessagePreparator messagePreparator;
	

	public CloudhopperSMPPSender(ExtendedSmppSessionConfiguration configuration, SessionHandlingStrategy sessionHandler, MessagePreparator messagePreparator) {
		super();
		this.configuration = configuration;
		this.sessionHandler = sessionHandler;
		this.messagePreparator = messagePreparator;
	}

	@Override
	public void send(Sms sms) throws MessageException {
		try {
			LOG.debug("Sending SMS...\n{}", logString(sms));
			SmppSession session = sessionHandler.getSession();
			for (SubmitSm msg : messagePreparator.prepareMessages(sms)) {
				session.submit(msg, configuration.getResponseTimeout());
			}
			LOG.debug("SMS sent\n{}", logString(sms));
			sessionHandler.messageSent(sms);
		} catch (SmppException e) {
			sessionHandler.messageNotSent(sms, e);
		} catch (UnrecoverablePduException | RecoverablePduException | SmppTimeoutException | SmppChannelException e) {
			sessionHandler.messageNotSent(sms, new SmppException("Failed to send SMS", e));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			sessionHandler.messageNotSent(sms, new SmppException("Failed to send SMS (interrupted)", e));
		} finally {
			sessionHandler.messageProcessed(sms);
		}
	}

	@Override
	public void clean() throws CleanException {
		sessionHandler.clean();
	}

	@Override
	public String toString() {
		return "CloudhopperSMPPSender";
	}
}
