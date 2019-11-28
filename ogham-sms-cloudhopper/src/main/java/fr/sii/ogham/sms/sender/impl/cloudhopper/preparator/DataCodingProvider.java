package fr.sii.ogham.sms.sender.impl.cloudhopper.preparator;

import com.cloudhopper.commons.charset.Charset;
import com.cloudhopper.commons.gsm.DataCoding;

import fr.sii.ogham.sms.encoder.Encoded;
import fr.sii.ogham.sms.encoder.Encoder;
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.DataCodingException;

/**
 * Data Coding Scheme is a one-octet field in Short Messages (SM) and Cell
 * Broadcast Messages (CB) which carries a basic information how the recipient
 * handset should process the received message. The information includes:
 * <ul>
 * <li>the character set or message coding which determines the encoding of the
 * message user data</li>
 * <li>the message class which determines to which component of the Mobile
 * Station (MS) or User Equipment (UE) should be the message delivered</li>
 * <li>the request to automatically delete the message after reading</li>
 * <li>the state of flags indicating presence of unread voicemail, fax, e-mail
 * or other messages</li>
 * <li>the indication that the message content is compressed</li>
 * <li>the language of the cell broadcast message</li>
 * </ul>
 * The field is described in 3GPP 23.040 and 3GPP 23.038 under the name TP-DCS.
 * 
 * 
 * The message is encoded using an {@link Encoder} which converts a string to a
 * byte array with a particular charset (see {@link Charset}).
 * 
 * {@link DataCodingProvider} indicates which {@link DataCoding} should be used
 * for SMPP message for this particular charset.
 * 
 * @author Aurélien Baudet
 *
 */
public interface DataCodingProvider {
	/**
	 * Indicates the Data Coding Scheme to use for the specified charset.
	 * 
	 * <p>
	 * Data Coding Scheme is a one-octet field in Short Messages (SM) and Cell
	 * Broadcast Messages (CB) which carries a basic information how the
	 * recipient handset should process the received message. The information
	 * includes:
	 * <ul>
	 * <li>the character set or message coding which determines the encoding of
	 * the message user data</li>
	 * <li>the message class which determines to which component of the Mobile
	 * Station (MS) or User Equipment (UE) should be the message delivered</li>
	 * <li>the request to automatically delete the message after reading</li>
	 * <li>the state of flags indicating presence of unread voicemail, fax,
	 * e-mail or other messages</li>
	 * <li>the indication that the message content is compressed</li>
	 * <li>the language of the cell broadcast message</li>
	 * </ul>
	 * The field is described in 3GPP 23.040 and 3GPP 23.038 under the name
	 * TP-DCS.
	 * 
	 * @param encoded
	 *            The encoded message with the associated charset used to encode
	 *            the message
	 * @return The Data Coding Scheme value. It may be a partial value that can
	 *         be merged with another. It may also be null
	 * @throws DataCodingException
	 *             when {@link DataCoding} couldn't be determined
	 */
	DataCoding provide(Encoded encoded) throws DataCodingException;
}
