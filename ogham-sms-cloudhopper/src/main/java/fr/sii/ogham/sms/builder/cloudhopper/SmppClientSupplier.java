package fr.sii.ogham.sms.builder.cloudhopper;

import com.cloudhopper.smpp.SmppClient;

/**
 * A functional interface to provide an instance of a {@link SmppClient}.
 * 
 * @author Aurélien Baudet
 */
public interface SmppClientSupplier {
	/**
	 * Get an instance of a {@link SmppClient}.
	 * 
	 * @return the {@link SmppClient} instance
	 */
	SmppClient get();
}
