package fr.sii.ogham.testing.sms.simulator.bean;

/**
 * Tag-Length-Value optional parameter in SMPP.
 * 
 * @author Aurélien Baudet
 *
 */
public interface OptionalParameter {
	/**
	 * The tag value.
	 * 
	 * @return the tag value
	 */
	Short getTag();

	/**
	 * The length of the value
	 * 
	 * @return the length
	 */
	Integer getLength();

	/**
	 * The parameter value
	 * 
	 * @return the value
	 */
	byte[] getValue();
}
