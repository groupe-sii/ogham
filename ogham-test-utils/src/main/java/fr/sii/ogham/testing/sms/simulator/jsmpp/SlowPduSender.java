package fr.sii.ogham.testing.sms.simulator.jsmpp;

import java.io.IOException;
import java.io.OutputStream;

import ogham.testing.org.apache.commons.lang3.NotImplementedException;
import ogham.testing.org.jsmpp.InvalidNumberOfDestinationsException;
import ogham.testing.org.jsmpp.PDUSender;
import ogham.testing.org.jsmpp.PDUStringException;
import ogham.testing.org.jsmpp.bean.BindType;
import ogham.testing.org.jsmpp.bean.DataCoding;
import ogham.testing.org.jsmpp.bean.DestinationAddress;
import ogham.testing.org.jsmpp.bean.ESMClass;
import ogham.testing.org.jsmpp.bean.InterfaceVersion;
import ogham.testing.org.jsmpp.bean.MessageState;
import ogham.testing.org.jsmpp.bean.NumberingPlanIndicator;
import ogham.testing.org.jsmpp.bean.OptionalParameter;
import ogham.testing.org.jsmpp.bean.RegisteredDelivery;
import ogham.testing.org.jsmpp.bean.ReplaceIfPresentFlag;
import ogham.testing.org.jsmpp.bean.TypeOfNumber;
import ogham.testing.org.jsmpp.bean.UnsuccessDelivery;

import fr.sii.ogham.testing.sms.simulator.config.Awaiter;
import fr.sii.ogham.testing.sms.simulator.config.ServerDelays;

/**
 * Decorate a real {@link PDUSender}. If delay is configured, it waits for that
 * delay before sending the real PDU.
 * 
 * @author Aurélien Baudet
 *
 */
public class SlowPduSender implements PDUSender {
	private final PDUSender delegate;
	private final ServerDelays delays;

	public SlowPduSender(PDUSender delegate, ServerDelays delays) {
		super();
		this.delegate = delegate;
		this.delays = delays;
	}

	@Override
	public byte[] sendHeader(OutputStream os, int commandId, int commandStatus, int sequenceNumber) throws IOException {
		await(delays.getSendHeaderWaiting());
		return delegate.sendHeader(os, commandId, commandStatus, sequenceNumber);
	}

	@Override
	public byte[] sendBind(OutputStream os, BindType bindType, int sequenceNumber, String systemId, String password, String systemType, InterfaceVersion interfaceVersion, TypeOfNumber addrTon,
			NumberingPlanIndicator addrNpi, String addressRange) throws PDUStringException, IOException {
		await(delays.getSendBindWaiting());
		return delegate.sendBind(os, bindType, sequenceNumber, systemId, password, systemType, interfaceVersion, addrTon, addrNpi, addressRange);
	}

	@Override
	public byte[] sendBindResp(OutputStream os, int commandId, int sequenceNumber, String systemId, InterfaceVersion interfaceVersion) throws PDUStringException, IOException {
		await(delays.getSendBindRespWaiting());
		return delegate.sendBindResp(os, commandId, sequenceNumber, systemId, interfaceVersion);
	}

	@Override
	public byte[] sendOutbind(OutputStream os, int sequenceNumber, String systemId, String password) throws PDUStringException, IOException {
		await(delays.getSendOutbindWaiting());
		return delegate.sendOutbind(os, sequenceNumber, systemId, password);
	}

	@Override
	public byte[] sendUnbind(OutputStream os, int sequenceNumber) throws IOException {
		await(delays.getSendUnbindWaiting());
		return delegate.sendUnbind(os, sequenceNumber);
	}

	@Override
	public byte[] sendGenericNack(OutputStream os, int commandStatus, int sequenceNumber) throws IOException {
		await(delays.getSendGenericNackWaiting());
		return delegate.sendGenericNack(os, commandStatus, sequenceNumber);
	}

	@Override
	public byte[] sendUnbindResp(OutputStream os, int commandStatus, int sequenceNumber) throws IOException {
		await(delays.getSendUnbindRespWaiting());
		return delegate.sendUnbindResp(os, commandStatus, sequenceNumber);
	}

	@Override
	public byte[] sendEnquireLink(OutputStream os, int sequenceNumber) throws IOException {
		await(delays.getSendEnquireLinkWaiting());
		return delegate.sendEnquireLink(os, sequenceNumber);
	}

	@Override
	public byte[] sendEnquireLinkResp(OutputStream os, int sequenceNumber) throws IOException {
		await(delays.getSendEnquireLinkRespWaiting());
		return delegate.sendEnquireLinkResp(os, sequenceNumber);
	}

	@Override
	public byte[] sendSubmitSm(OutputStream os, int sequenceNumber, String serviceType, TypeOfNumber sourceAddrTon, NumberingPlanIndicator sourceAddrNpi, String sourceAddr, TypeOfNumber destAddrTon,
			NumberingPlanIndicator destAddrNpi, String destinationAddr, ESMClass esmClass, byte protocolId, byte priorityFlag, String scheduleDeliveryTime, String validityPeriod,
			RegisteredDelivery registeredDelivery, byte replaceIfPresentFlag, DataCoding dataCoding, byte smDefaultMsgId, byte[] shortMessage, OptionalParameter... optionalParameters)
			throws PDUStringException, IOException {
		await(delays.getSendSubmitSmWaiting());
		return delegate.sendSubmitSm(os, sequenceNumber, serviceType, sourceAddrTon, sourceAddrNpi, sourceAddr, destAddrTon, destAddrNpi, destinationAddr, esmClass, protocolId, priorityFlag,
				scheduleDeliveryTime, validityPeriod, registeredDelivery, replaceIfPresentFlag, dataCoding, smDefaultMsgId, shortMessage, optionalParameters);
	}

	@Override
	public byte[] sendSubmitSmResp(OutputStream os, int sequenceNumber, String messageId, OptionalParameter... optionalParameters) throws PDUStringException, IOException {
		await(delays.getSendSubmitSmRespWaiting());
		return delegate.sendSubmitSmResp(os, sequenceNumber, messageId, optionalParameters);
	}

	@Override
	public byte[] sendQuerySm(OutputStream os, int sequenceNumber, String messageId, TypeOfNumber sourceAddrTon, NumberingPlanIndicator sourceAddrNpi, String sourceAddr)
			throws PDUStringException, IOException {
		await(delays.getSendQuerySmWaiting());
		return delegate.sendQuerySm(os, sequenceNumber, messageId, sourceAddrTon, sourceAddrNpi, sourceAddr);
	}

	@Override
	public byte[] sendQuerySmResp(OutputStream os, int sequenceNumber, String messageId, String finalDate, MessageState messageState, byte errorCode) throws PDUStringException, IOException {
		await(delays.getSendQuerySmRespWaiting());
		return delegate.sendQuerySmResp(os, sequenceNumber, messageId, finalDate, messageState, errorCode);
	}

	@Override
	public byte[] sendDeliverSm(OutputStream os, int sequenceNumber, String serviceType, TypeOfNumber sourceAddrTon, NumberingPlanIndicator sourceAddrNpi, String sourceAddr, TypeOfNumber destAddrTon,
			NumberingPlanIndicator destAddrNpi, String destinationAddr, ESMClass esmClass, byte protocoId, byte priorityFlag, RegisteredDelivery registeredDelivery, DataCoding dataCoding,
			byte[] shortMessage, OptionalParameter... optionalParameters) throws PDUStringException, IOException {
		await(delays.getSendDeliverSmWaiting());
		return delegate.sendDeliverSm(os, sequenceNumber, serviceType, sourceAddrTon, sourceAddrNpi, sourceAddr, destAddrTon, destAddrNpi, destinationAddr, esmClass, protocoId, priorityFlag,
				registeredDelivery, dataCoding, shortMessage, optionalParameters);
	}

	@Override
	public byte[] sendDeliverSmResp(OutputStream os, int commandStatus, int sequenceNumber, String messageId) throws IOException {
		await(delays.getSendDeliverSmRespWaiting());
		return delegate.sendDeliverSmResp(os, commandStatus, sequenceNumber, messageId);
	}

	@Override
	public byte[] sendDataSm(OutputStream os, int sequenceNumber, String serviceType, TypeOfNumber sourceAddrTon, NumberingPlanIndicator sourceAddrNpi, String sourceAddr, TypeOfNumber destAddrTon,
			NumberingPlanIndicator destAddrNpi, String destinationAddr, ESMClass esmClass, RegisteredDelivery registeredDelivery, DataCoding dataCoding, OptionalParameter... optionalParameters)
			throws PDUStringException, IOException {
		await(delays.getSendDataSmWaiting());
		return delegate.sendDataSm(os, sequenceNumber, serviceType, sourceAddrTon, sourceAddrNpi, sourceAddr, destAddrTon, destAddrNpi, destinationAddr, esmClass, registeredDelivery, dataCoding,
				optionalParameters);
	}

	@Override
	public byte[] sendDataSmResp(OutputStream os, int sequenceNumber, String messageId, OptionalParameter... optionalParameters) throws PDUStringException, IOException {
		await(delays.getSendDataSmRespWaiting());
		return delegate.sendDataSmResp(os, sequenceNumber, messageId, optionalParameters);
	}

	@Override
	public byte[] sendCancelSm(OutputStream os, int sequenceNumber, String serviceType, String messageId, TypeOfNumber sourceAddrTon, NumberingPlanIndicator sourceAddrNpi, String sourceAddr,
			TypeOfNumber destAddrTon, NumberingPlanIndicator destAddrNpi, String destinationAddr) throws PDUStringException, IOException {
		await(delays.getSendCancelSmWaiting());
		return delegate.sendCancelSm(os, sequenceNumber, serviceType, messageId, sourceAddrTon, sourceAddrNpi, sourceAddr, destAddrTon, destAddrNpi, destinationAddr);
	}

	@Override
	public byte[] sendCancelSmResp(OutputStream os, int sequenceNumber) throws IOException {
		await(delays.getSendCancelSmRespWaiting());
		return delegate.sendCancelSmResp(os, sequenceNumber);
	}

	@Override
	public byte[] sendReplaceSm(OutputStream os, int sequenceNumber, String messageId, TypeOfNumber sourceAddrTon, NumberingPlanIndicator sourceAddrNpi, String sourceAddr, String scheduleDeliveryTime,
			String validityPeriod, RegisteredDelivery registeredDelivery, byte smDefaultMsgId, byte[] shortMessage) throws PDUStringException, IOException {
		await(delays.getSendReplaceSmWaiting());
		return delegate.sendReplaceSm(os, sequenceNumber, messageId, sourceAddrTon, sourceAddrNpi, sourceAddr, scheduleDeliveryTime, validityPeriod, registeredDelivery, smDefaultMsgId, shortMessage);
	}

	@Override
	public byte[] sendReplaceSmResp(OutputStream os, int sequenceNumber) throws IOException {
		await(delays.getSendReplaceSmRespWaiting());
		return delegate.sendReplaceSmResp(os, sequenceNumber);
	}

	@Override
	public byte[] sendSubmitMulti(OutputStream os, int sequenceNumber, String serviceType, TypeOfNumber sourceAddrTon, NumberingPlanIndicator sourceAddrNpi, String sourceAddr,
			DestinationAddress[] destinationAddresses, ESMClass esmClass, byte protocolId, byte priorityFlag, String scheduleDeliveryTime, String validityPeriod, RegisteredDelivery registeredDelivery,
			ReplaceIfPresentFlag replaceIfPresentFlag, DataCoding dataCoding, byte smDefaultMsgId, byte[] shortMessage, OptionalParameter... optionalParameters)
			throws PDUStringException, InvalidNumberOfDestinationsException, IOException {
		await(delays.getSendSubmitMultiWaiting());
		return delegate.sendSubmitMulti(os, sequenceNumber, serviceType, sourceAddrTon, sourceAddrNpi, sourceAddr, destinationAddresses, esmClass, protocolId, priorityFlag, scheduleDeliveryTime,
				validityPeriod, registeredDelivery, replaceIfPresentFlag, dataCoding, smDefaultMsgId, shortMessage, optionalParameters);
	}

	@Override
	public byte[] sendSubmitMultiResp(OutputStream os, int sequenceNumber, String messageId, UnsuccessDelivery... unsuccessDeliveries) throws PDUStringException, IOException {
		await(delays.getSendSubmitMultiRespWaiting());
		return delegate.sendSubmitMultiResp(os, sequenceNumber, messageId, unsuccessDeliveries);
	}

	@Override
	public byte[] sendAlertNotification(OutputStream os, int sequenceNumber, TypeOfNumber sourceAddrTon, NumberingPlanIndicator sourceAddrNpi, String sourceAddr, TypeOfNumber esmeAddrTon,
			NumberingPlanIndicator esmeAddrNpi, String esmeAddr, OptionalParameter... optionalParameters) throws PDUStringException, IOException {
		await(delays.getSendAlertNotificationWaiting());
		return delegate.sendAlertNotification(os, sequenceNumber, sourceAddrTon, sourceAddrNpi, sourceAddr, esmeAddrTon, esmeAddrNpi, esmeAddr, optionalParameters);
	}


	@Override
	public byte[] sendBroadcastSm(OutputStream os, int sequenceNumber, String serviceType, TypeOfNumber sourceAddrTon, NumberingPlanIndicator sourceAddrNpi, String sourceAddr, String messageId,
			byte priorityFlag, String scheduleDeliveryTime, String validityPeriod, ReplaceIfPresentFlag replaceIfPresentFlag, DataCoding dataCoding, byte smDefaultMsgId,
			OptionalParameter... optionalParameters) throws PDUStringException, IOException {
		throw new NotImplementedException();
	}

	@Override
	public byte[] sendBroadcastSmResp(OutputStream os, int sequenceNumber, String messageId, OptionalParameter... optionalParameters) throws PDUStringException, IOException {
		throw new NotImplementedException();
	}

	@Override
	public byte[] sendCancelBroadcastSm(OutputStream os, int sequenceNumber, String serviceType, String messageId, TypeOfNumber sourceAddrTon, NumberingPlanIndicator sourceAddrNpi, String sourceAddr,
			OptionalParameter... optionalParameters) throws PDUStringException, IOException {
		throw new NotImplementedException();
	}

	@Override
	public byte[] sendCancelBroadcastSmResp(OutputStream os, int sequenceNumber) throws IOException {
		throw new NotImplementedException();
	}

	@Override
	public byte[] sendQueryBroadcastSm(OutputStream os, int sequenceNumber, String messageId, TypeOfNumber sourceAddrTon, NumberingPlanIndicator sourceAddrNpi, String sourceAddr,
			OptionalParameter... optionalParameters) throws PDUStringException, IOException {
		throw new NotImplementedException();
	}

	@Override
	public byte[] sendQueryBroadcastSmResp(OutputStream os, int sequenceNumber, String messageId, OptionalParameter... optionalParameters) throws PDUStringException, IOException {
		throw new NotImplementedException();
	}
	
	private static void await(Awaiter waiting) {
		if (waiting == null) {
			return;
		}
		waiting.await();
	}

}
