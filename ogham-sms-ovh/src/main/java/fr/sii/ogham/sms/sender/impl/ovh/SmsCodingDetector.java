package fr.sii.ogham.sms.sender.impl.ovh;

/**
 * Automatically detects the {@link SmsCoding} to use according to the
 * characters used in the message.
 * 
 * @author Aurélien Baudet
 *
 */
public interface SmsCodingDetector {
	/**
	 * Automatically detects the {@link SmsCoding} to use according to the
	 * characters used in the message.
	 * 
	 * @param message
	 *            the text message to analyze
	 * @return the detected {@link SmsCoding}
	 */
	SmsCoding detect(String message);
}
