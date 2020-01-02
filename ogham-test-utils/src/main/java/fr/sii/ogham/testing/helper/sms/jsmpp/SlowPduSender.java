package fr.sii.ogham.testing.helper.sms.jsmpp;

import java.io.IOException;
import java.io.OutputStream;

import org.jsmpp.InvalidNumberOfDestinationsException;
import org.jsmpp.PDUSender;
import org.jsmpp.PDUStringException;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.DataCoding;
import org.jsmpp.bean.DestinationAddress;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.InterfaceVersion;
import org.jsmpp.bean.MessageState;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.OptionalParameter;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.ReplaceIfPresentFlag;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.bean.UnsuccessDelivery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.testing.helper.sms.rule.config.ServerDelays;

/**
 * Decorate a real {@link PDUSender}. If delay is configured, it waits for that
 * delay before sending the real PDU.
 * 
 * @author Aurélien Baudet
 *
 */
public class SlowPduSender implements PDUSender {
	private static final Logger LOG = LoggerFactory.getLogger(SlowPduSender.class);

	private final PDUSender delegate;
	private final ServerDelays delays;

	public SlowPduSender(PDUSender delegate, ServerDelays delays) {
		super();
		this.delegate = delegate;
		this.delays = delays;
	}

	@Override
	public byte[] sendHeader(OutputStream os, int commandId, int commandStatus, int sequenceNumber) throws IOException {
		waitFor(delays.getSendHeaderDelay());
		return delegate.sendHeader(os, commandId, commandStatus, sequenceNumber);
	}

	@Override
	public byte[] sendBind(OutputStream os, BindType bindType, int sequenceNumber, String systemId, String password, String systemType, InterfaceVersion interfaceVersion, TypeOfNumber addrTon,
			NumberingPlanIndicator addrNpi, String addressRange) throws PDUStringException, IOException {
		waitFor(delays.getSendBindDelay());
		return delegate.sendBind(os, bindType, sequenceNumber, systemId, password, systemType, interfaceVersion, addrTon, addrNpi, addressRange);
	}

	@Override
	public byte[] sendBindResp(OutputStream os, int commandId, int sequenceNumber, String systemId, InterfaceVersion interfaceVersion) throws PDUStringException, IOException {
		waitFor(delays.getSendBindRespDelay());
		return delegate.sendBindResp(os, commandId, sequenceNumber, systemId, interfaceVersion);
	}

	@Override
	public byte[] sendOutbind(OutputStream os, int sequenceNumber, String systemId, String password) throws PDUStringException, IOException {
		waitFor(delays.getSendOutbindDelay());
		return delegate.sendOutbind(os, sequenceNumber, systemId, password);
	}

	@Override
	public byte[] sendUnbind(OutputStream os, int sequenceNumber) throws IOException {
		waitFor(delays.getSendUnbindDelay());
		return delegate.sendUnbind(os, sequenceNumber);
	}

	@Override
	public byte[] sendGenericNack(OutputStream os, int commandStatus, int sequenceNumber) throws IOException {
		waitFor(delays.getSendGenericNackDelay());
		return delegate.sendGenericNack(os, commandStatus, sequenceNumber);
	}

	@Override
	public byte[] sendUnbindResp(OutputStream os, int commandStatus, int sequenceNumber) throws IOException {
		waitFor(delays.getSendUnbindRespDelay());
		return delegate.sendUnbindResp(os, commandStatus, sequenceNumber);
	}

	@Override
	public byte[] sendEnquireLink(OutputStream os, int sequenceNumber) throws IOException {
		waitFor(delays.getSendEnquireLinkDelay());
		return delegate.sendEnquireLink(os, sequenceNumber);
	}

	@Override
	public byte[] sendEnquireLinkResp(OutputStream os, int sequenceNumber) throws IOException {
		waitFor(delays.getSendEnquireLinkRespDelay());
		return delegate.sendEnquireLinkResp(os, sequenceNumber);
	}

	@Override
	public byte[] sendSubmitSm(OutputStream os, int sequenceNumber, String serviceType, TypeOfNumber sourceAddrTon, NumberingPlanIndicator sourceAddrNpi, String sourceAddr, TypeOfNumber destAddrTon,
			NumberingPlanIndicator destAddrNpi, String destinationAddr, ESMClass esmClass, byte protocolId, byte priorityFlag, String scheduleDeliveryTime, String validityPeriod,
			RegisteredDelivery registeredDelivery, byte replaceIfPresentFlag, DataCoding dataCoding, byte smDefaultMsgId, byte[] shortMessage, OptionalParameter... optionalParameters)
			throws PDUStringException, IOException {
		waitFor(delays.getSendSubmitSmDelay());
		return delegate.sendSubmitSm(os, sequenceNumber, serviceType, sourceAddrTon, sourceAddrNpi, sourceAddr, destAddrTon, destAddrNpi, destinationAddr, esmClass, protocolId, priorityFlag,
				scheduleDeliveryTime, validityPeriod, registeredDelivery, replaceIfPresentFlag, dataCoding, smDefaultMsgId, shortMessage, optionalParameters);
	}

	@Override
	public byte[] sendSubmitSmResp(OutputStream os, int sequenceNumber, String messageId) throws PDUStringException, IOException {
		waitFor(delays.getSendSubmitSmRespDelay());
		return delegate.sendSubmitSmResp(os, sequenceNumber, messageId);
	}

	@Override
	public byte[] sendQuerySm(OutputStream os, int sequenceNumber, String messageId, TypeOfNumber sourceAddrTon, NumberingPlanIndicator sourceAddrNpi, String sourceAddr)
			throws PDUStringException, IOException {
		waitFor(delays.getSendQuerySmDelay());
		return delegate.sendQuerySm(os, sequenceNumber, messageId, sourceAddrTon, sourceAddrNpi, sourceAddr);
	}

	@Override
	public byte[] sendQuerySmResp(OutputStream os, int sequenceNumber, String messageId, String finalDate, MessageState messageState, byte errorCode) throws PDUStringException, IOException {
		waitFor(delays.getSendQuerySmRespDelay());
		return delegate.sendQuerySmResp(os, sequenceNumber, messageId, finalDate, messageState, errorCode);
	}

	@Override
	public byte[] sendDeliverSm(OutputStream os, int sequenceNumber, String serviceType, TypeOfNumber sourceAddrTon, NumberingPlanIndicator sourceAddrNpi, String sourceAddr, TypeOfNumber destAddrTon,
			NumberingPlanIndicator destAddrNpi, String destinationAddr, ESMClass esmClass, byte protocoId, byte priorityFlag, RegisteredDelivery registeredDelivery, DataCoding dataCoding,
			byte[] shortMessage, OptionalParameter... optionalParameters) throws PDUStringException, IOException {
		waitFor(delays.getSendDeliverSmDelay());
		return delegate.sendDeliverSm(os, sequenceNumber, serviceType, sourceAddrTon, sourceAddrNpi, sourceAddr, destAddrTon, destAddrNpi, destinationAddr, esmClass, protocoId, priorityFlag,
				registeredDelivery, dataCoding, shortMessage, optionalParameters);
	}

	@Override
	public byte[] sendDeliverSmResp(OutputStream os, int commandStatus, int sequenceNumber, String messageId) throws IOException {
		waitFor(delays.getSendDeliverSmRespDelay());
		return delegate.sendDeliverSmResp(os, commandStatus, sequenceNumber, messageId);
	}

	@Override
	public byte[] sendDataSm(OutputStream os, int sequenceNumber, String serviceType, TypeOfNumber sourceAddrTon, NumberingPlanIndicator sourceAddrNpi, String sourceAddr, TypeOfNumber destAddrTon,
			NumberingPlanIndicator destAddrNpi, String destinationAddr, ESMClass esmClass, RegisteredDelivery registeredDelivery, DataCoding dataCoding, OptionalParameter... optionalParameters)
			throws PDUStringException, IOException {
		waitFor(delays.getSendDataSmDelay());
		return delegate.sendDataSm(os, sequenceNumber, serviceType, sourceAddrTon, sourceAddrNpi, sourceAddr, destAddrTon, destAddrNpi, destinationAddr, esmClass, registeredDelivery, dataCoding,
				optionalParameters);
	}

	@Override
	public byte[] sendDataSmResp(OutputStream os, int sequenceNumber, String messageId, OptionalParameter... optionalParameters) throws PDUStringException, IOException {
		waitFor(delays.getSendDataSmRespDelay());
		return delegate.sendDataSmResp(os, sequenceNumber, messageId, optionalParameters);
	}

	@Override
	public byte[] sendCancelSm(OutputStream os, int sequenceNumber, String serviceType, String messageId, TypeOfNumber sourceAddrTon, NumberingPlanIndicator sourceAddrNpi, String sourceAddr,
			TypeOfNumber destAddrTon, NumberingPlanIndicator destAddrNpi, String destinationAddr) throws PDUStringException, IOException {
		waitFor(delays.getSendCancelSmDelay());
		return delegate.sendCancelSm(os, sequenceNumber, serviceType, messageId, sourceAddrTon, sourceAddrNpi, sourceAddr, destAddrTon, destAddrNpi, destinationAddr);
	}

	@Override
	public byte[] sendCancelSmResp(OutputStream os, int sequenceNumber) throws IOException {
		waitFor(delays.getSendCancelSmRespDelay());
		return delegate.sendCancelSmResp(os, sequenceNumber);
	}

	@Override
	public byte[] sendReplaceSm(OutputStream os, int sequenceNumber, String messageId, TypeOfNumber sourceAddrTon, NumberingPlanIndicator sourceAddrNpi, String sourceAddr, String scheduleDeliveryTime,
			String validityPeriod, RegisteredDelivery registeredDelivery, byte smDefaultMsgId, byte[] shortMessage) throws PDUStringException, IOException {
		waitFor(delays.getSendReplaceSmDelay());
		return delegate.sendReplaceSm(os, sequenceNumber, messageId, sourceAddrTon, sourceAddrNpi, sourceAddr, scheduleDeliveryTime, validityPeriod, registeredDelivery, smDefaultMsgId, shortMessage);
	}

	@Override
	public byte[] sendReplaceSmResp(OutputStream os, int sequenceNumber) throws IOException {
		waitFor(delays.getSendReplaceSmRespDelay());
		return delegate.sendReplaceSmResp(os, sequenceNumber);
	}

	@Override
	public byte[] sendSubmiMulti(OutputStream os, int sequenceNumber, String serviceType, TypeOfNumber sourceAddrTon, NumberingPlanIndicator sourceAddrNpi, String sourceAddr,
			DestinationAddress[] destinationAddresses, ESMClass esmClass, byte protocolId, byte priorityFlag, String scheduleDeliveryTime, String validityPeriod, RegisteredDelivery registeredDelivery,
			ReplaceIfPresentFlag replaceIfPresentFlag, DataCoding dataCoding, byte smDefaultMsgId, byte[] shortMessage, OptionalParameter... optionalParameters)
			throws PDUStringException, InvalidNumberOfDestinationsException, IOException {
		waitFor(delays.getSendSubmiMultiDelay());
		return delegate.sendSubmiMulti(os, sequenceNumber, serviceType, sourceAddrTon, sourceAddrNpi, sourceAddr, destinationAddresses, esmClass, protocolId, priorityFlag, scheduleDeliveryTime,
				validityPeriod, registeredDelivery, replaceIfPresentFlag, dataCoding, smDefaultMsgId, shortMessage, optionalParameters);
	}

	@Override
	public byte[] sendSubmitMultiResp(OutputStream os, int sequenceNumber, String messageId, UnsuccessDelivery... unsuccessDeliveries) throws PDUStringException, IOException {
		waitFor(delays.getSendSubmitMultiRespDelay());
		return delegate.sendSubmitMultiResp(os, sequenceNumber, messageId, unsuccessDeliveries);
	}

	@Override
	public byte[] sendAlertNotification(OutputStream os, int sequenceNumber, byte sourceAddrTon, byte sourceAddrNpi, String sourceAddr, byte esmeAddrTon, byte esmeAddrNpi, String esmeAddr,
			OptionalParameter... optionalParameters) throws PDUStringException, IOException {
		waitFor(delays.getSendAlertNotificationDelay());
		return delegate.sendAlertNotification(os, sequenceNumber, sourceAddrTon, sourceAddrNpi, sourceAddr, esmeAddrTon, esmeAddrNpi, esmeAddr, optionalParameters);
	}

	private void waitFor(long delay) {
		if (delay == 0) {
			return;
		}
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			LOG.error("Failed to sleep for " + delay, e);
			Thread.currentThread().interrupt();
		}
	}
}
