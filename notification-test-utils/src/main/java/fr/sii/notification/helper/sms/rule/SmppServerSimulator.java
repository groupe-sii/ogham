package fr.sii.notification.helper.sms.rule;

import java.util.List;

public interface SmppServerSimulator<M> {
	public void start() throws SmppServerException;
	
	public void stop() throws SmppServerException;
	
	public int getPort();
	
	public List<M> getReceivedMessages();
}
