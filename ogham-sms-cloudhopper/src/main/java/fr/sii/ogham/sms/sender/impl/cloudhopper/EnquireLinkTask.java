package fr.sii.ogham.sms.sender.impl.cloudhopper;

import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.pdu.EnquireLink;
import com.cloudhopper.smpp.type.RecoverablePduException;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnquireLinkTask implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(EnquireLinkTask.class);

  private CloudHopperSmppClient smppClient;
  private Integer enquireLinkTimeout;

  public EnquireLinkTask(CloudHopperSmppClient smppClient, Integer enquireLinkTimeout) {
    this.smppClient = smppClient;
    this.enquireLinkTimeout = enquireLinkTimeout;
  }

  @Override
  public void run() {
    SmppSession smppSession = smppClient.getSession();
    if (smppSession != null && smppSession.isBound()) {
      try {
        smppSession.enquireLink(new EnquireLink(), enquireLinkTimeout);
      } catch (SmppTimeoutException | SmppChannelException
          | RecoverablePduException | UnrecoverablePduException error) {
        LOGGER.warn("Enquire link failed, executing reconnect: ", error);
        smppClient.scheduleReconnect();
      } catch (InterruptedException error) {
        LOGGER.info("Enquire link interrupted, probably killed by reconnecting");
      }
    } else {
      LOGGER.warn("Enquire link running while session is not connected");
    }
  }

}
