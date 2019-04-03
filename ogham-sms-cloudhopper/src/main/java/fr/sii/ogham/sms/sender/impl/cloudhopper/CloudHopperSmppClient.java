package fr.sii.ogham.sms.sender.impl.cloudhopper;

import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.impl.DefaultSmppSessionHandler;
import com.cloudhopper.smpp.pdu.DeliverSm;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class CloudHopperSmppClient {
  private static final Logger logger = LoggerFactory.getLogger(CloudHopperSmppClient.class);

  private ScheduledExecutorService enquireLinkExecutor;
  private ScheduledFuture<?> enquireLinkTask;
  private int enquireLinkPeriod = 60000;
  private int enquireLinkTimeout = 10000;

  private ScheduledExecutorService reconnectionExecutor;
  private ScheduledFuture<?> reconnectionTask;
  private Integer reconnectionDelay = 10000;

  private final SmppSessionConfiguration smppSessionConfiguration;
  private DefaultSmppSessionHandler sessionHandler = new ClientSmppSessionHandler(this);
  private ExecutorService executorService = Executors.newCachedThreadPool();
  private DefaultSmppClient clientBootstrap = new DefaultSmppClient();
  private SmppSession smppSession;
  private ReceiverMessageHandler receiverMessageHandler = null;

  public CloudHopperSmppClient(SmppSessionConfiguration smppSessionConfiguration) {
    this.smppSessionConfiguration = smppSessionConfiguration;

    enquireLinkExecutor = Executors.newScheduledThreadPool(1, new ThreadFactory() {
      @Override
      public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setName("EnquireLink-");
        return thread;
      }
    });

    reconnectionExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
      @Override
      public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setName("Reconnection-");
        return thread;
      }
    });

    scheduleReconnect();
  }

  public synchronized SmppSession getSession() {
    return smppSession;
  }

  protected synchronized void reconnect() {
    try {
      disconnect();
      smppSession = clientBootstrap.bind(smppSessionConfiguration, sessionHandler);
      stopReconnectionkTask();
      runEnquireLinkTask();
      logger.info("SMPP session connected");
    } catch (SmppTimeoutException | SmppChannelException
        | UnrecoverablePduException | InterruptedException error) {
      logger.warn("Unable to connect to SMPP server: ", error);
    }
  }

  public void scheduleReconnect() {
    if (reconnectionTask == null || reconnectionTask.isDone()) {
      reconnectionTask = reconnectionExecutor.scheduleWithFixedDelay(
          new ReconnectionTask(this),
          reconnectionDelay, reconnectionDelay, TimeUnit.MILLISECONDS);
    }
  }

  private void stopReconnectionkTask() {
    if (reconnectionTask != null) {
      reconnectionTask.cancel(false);
    }
  }

  private void disconnect() {
    stopEnquireLinkTask();
    destroySession();
  }

  private void runEnquireLinkTask() {
    enquireLinkTask = enquireLinkExecutor.scheduleWithFixedDelay(
        new EnquireLinkTask(this, enquireLinkTimeout),
        enquireLinkPeriod, enquireLinkPeriod, TimeUnit.MILLISECONDS);
  }

  private void stopEnquireLinkTask() {
    if (enquireLinkTask != null) {
      enquireLinkTask.cancel(true);
    }
  }

  private void destroySession() {
    if (smppSession != null) {
      logger.info("Cleaning up SMPP session... ");
      smppSession.destroy();
      smppSession = null;
    }
  }

  public void processDeliverSm(DeliverSm deliverSm) {
    if (receiverMessageHandler != null) {
      receiverMessageHandler.processDeliverSm(deliverSm);
    }
  }
}
