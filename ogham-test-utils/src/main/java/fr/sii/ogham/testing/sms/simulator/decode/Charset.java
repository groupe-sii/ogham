package fr.sii.ogham.testing.sms.simulator.decode;

/**
 * Interface used to decode a SMS content. This is useful to allow developers to
 * use a custom decoding instead of only the ones provided by Cloudhopper.
 * 
 * @author Aurélien Baudet
 */
public interface Charset {
	/**
	 * Decode content of a SMS as bytes into a string.
	 * 
	 * @param bytes
	 *            the bytes to decode
	 * @return the decoded string
	 */
	String decode(byte[] bytes);
}
