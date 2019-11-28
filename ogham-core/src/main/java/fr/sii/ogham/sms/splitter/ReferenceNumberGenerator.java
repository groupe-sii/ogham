package fr.sii.ogham.sms.splitter;

import fr.sii.ogham.sms.exception.message.ReferenceNumberGenerationException;

/**
 * In the cellular phone industry, mobile phones and their networks sometimes
 * support concatenated short message service (or concatenated SMS) to overcome
 * the limitation on the number of characters that can be sent in a single SMS
 * text message transmission (which is usually 160). Using this method, long
 * messages are split into smaller messages by the sending device and recombined
 * at the receiving end. Each message is then billed separately. When the
 * feature works properly, it is nearly transparent to the user, appearing as a
 * single long text message.
 * 
 * <p>
 * One way of sending concatenated SMS (CSMS) is to split the message into 153
 * 7-bit character parts (134 octets), and sending each part with a User Data
 * Header (UDH) tacked onto the beginning. A UDH can be used for various
 * purposes and its contents and size varies accordingly, but a UDH for
 * concatenating SMSes look like this:
 * 
 * <ul>
 * <li>Field 1 (1 octet): Length of User Data Header, in this case 05.</li>
 * <li>Field 2 (1 octet): Information Element Identifier, equal to 00
 * (Concatenated short messages, 8-bit reference number)</li>
 * <li>Field 3 (1 octet): Length of the header, excluding the first two fields;
 * equal to 03</li>
 * <li>Field 4 (1 octet): 00-FF, CSMS reference number, must be same for all the
 * SMS parts in the CSMS</li>
 * <li>Field 5 (1 octet): 00-FF, total number of parts. The value shall remain
 * constant for every short message which makes up the concatenated short
 * message. If the value is zero then the receiving entity shall ignore the
 * whole information element</li>
 * <li>Field 6 (1 octet): 00-FF, this part's number in the sequence. The value
 * shall start at 1 and increment for every short message which makes up the
 * concatenated short message. If the value is zero or greater than the value in
 * Field 5 then the receiving entity shall ignore the whole information element.
 * [ETSI Specification: GSM 03.40 Version 5.3.0: July 1996]</li>
 * </ul>
 * 
 * <p>
 * It is possible to use a 16 bit CSMS reference number in order to reduce the
 * probability that two different concatenated messages are sent with identical
 * reference numbers to a receiver. In this case, the User Data Header shall be:
 * 
 * <ul>
 * <li>Field 1 (1 octet): Length of User Data Header (UDL), in this case
 * 06.</li>
 * <li>Field 2 (1 octet): Information Element Identifier, equal to 08
 * (Concatenated short messages, 16-bit reference number)</li>
 * <li>Field 3 (1 octet): Length of the header, excluding the first two fields;
 * equal to 04</li>
 * <li>Field 4 (2 octets): 0000-FFFF, CSMS reference number, must be same for
 * all the SMS parts in the CSMS</li>
 * <li>Field 5 (1 octet): 00-FF, total number of parts. The value shall remain
 * constant for every short message which makes up the concatenated short
 * message. If the value is zero then the receiving entity shall ignore the
 * whole information element</li>
 * <li>Field 6 (1 octet): 00-FF, this part's number in the sequence. The value
 * shall start at 1 and increment for every short message which makes up the
 * concatenated short message. If the value is zero or greater than the value in
 * Field 5 then the receiving entity shall ignore the whole information element.
 * [ETSI Specification: GSM 03.40 Version 5.3.0: July 1996]</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public interface ReferenceNumberGenerator {
	/**
	 * Generate a reference number to identify concatenated messages.
	 * 
	 * @return the bytes that represent the reference number
	 * @throws ReferenceNumberGenerationException
	 *             when generation of reference number has failed
	 */
	byte[] generateReferenceNumber() throws ReferenceNumberGenerationException;
}
