package fr.sii.ogham.sms.sender.impl.cloudhopper;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.impl.DefaultSmppSessionHandler;
import com.cloudhopper.smpp.pdu.DeliverSm;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientSmppSessionHandler extends DefaultSmppSessionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClientSmppSessionHandler.class);

  private CloudHopperSmppClient smppClient;

  public ClientSmppSessionHandler(CloudHopperSmppClient smppClient) {
    this.smppClient = smppClient;
  }

  @Override
  public void firePduRequestExpired(PduRequest pduRequest) {
    LOGGER.warn("PDU request expired: " + pduRequest);
  }

  @Override
  public PduResponse firePduRequestReceived(PduRequest request) {
    PduResponse response;
    try {
      if (request instanceof DeliverSm) {
        smppClient.processDeliverSm((DeliverSm) request);
      }
      response = request.createResponse();
    } catch (Exception error) {
      LOGGER.warn("SMS receiving error", error);
      response = request.createResponse();
      response.setResultMessage(error.getMessage());
      response.setCommandStatus(SmppConstants.STATUS_UNKNOWNERR);
    }
    return response;
  }

  @Override
  public void fireChannelUnexpectedlyClosed() {
    LOGGER.warn("SMPP session channel unexpectedly closed");
    smppClient.scheduleReconnect();
  }

}
