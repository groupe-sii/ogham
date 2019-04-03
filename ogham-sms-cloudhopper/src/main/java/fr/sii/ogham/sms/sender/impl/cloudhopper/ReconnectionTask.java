package fr.sii.ogham.sms.sender.impl.cloudhopper;

public class ReconnectionTask implements Runnable {

  private final CloudHopperSmppClient smppClient;

  protected ReconnectionTask(CloudHopperSmppClient smppClient) {
    this.smppClient = smppClient;
  }

  @Override
  public void run() {
    smppClient.reconnect();
  }

}
