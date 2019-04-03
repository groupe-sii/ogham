package fr.sii.ogham.sms.sender.impl.cloudhopper;

import com.cloudhopper.smpp.pdu.DeliverSm;

public interface ReceiverMessageHandler {

  public void processDeliverSm(DeliverSm deliverSm);
}
