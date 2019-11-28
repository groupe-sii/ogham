package fr.sii.ogham.helper.sms.bean;

/**
 * Tag-Length-Value optional parameter in SMPP.
 * 
 * @author Aurélien Baudet
 *
 */
public interface OptionalParameter {
	/**
	 * The tag value
	 * 
	 * @return the tag value
	 */
	short getTag();

	/**
	 * The length of the value
	 * 
	 * @return the length
	 */
	int getLength();

	/**
	 * The parameter value
	 * 
	 * @return the value
	 */
	byte[] getValue();
}
