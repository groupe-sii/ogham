package fr.sii.ogham.sms.splitter;

/**
 * Represents a segment (part) of the original message.
 * 
 * @author Aurélien Baudet
 *
 */
public interface Segment {
	/**
	 * Returns the segment text as a byte array (partial message).
	 * 
	 * @return the byte array for the segment
	 */
	byte[] getBytes();
}
