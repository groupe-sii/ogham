package fr.sii.ogham.sms.builder.cloudhopper;

import com.cloudhopper.smpp.SmppSessionHandler;

/**
 * A functional interface to provide an instance of a {@link SmppSessionHandler}.
 * 
 * @author Aurélien Baudet
 */
public interface SmppSessionHandlerSupplier {
	/**
	 * Get an instance of a {@link SmppSessionHandler}.
	 * 
	 * @return the {@link SmppSessionHandler} instance
	 */
	SmppSessionHandler get();
}
