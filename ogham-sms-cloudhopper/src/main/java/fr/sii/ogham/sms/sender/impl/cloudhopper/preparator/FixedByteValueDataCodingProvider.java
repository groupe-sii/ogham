package fr.sii.ogham.sms.sender.impl.cloudhopper.preparator;

import com.cloudhopper.commons.gsm.DataCoding;

import fr.sii.ogham.sms.encoder.Encoded;
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.DataCodingException;

/**
 * Use a fixed value for every messages.
 * 
 * @author Aurélien Baudet
 *
 */
public class FixedByteValueDataCodingProvider implements DataCodingProvider {
	private final byte dcs;

	/**
	 * Initializes with the Data Coding Scheme value to use for all messages.
	 * 
	 * @param dcs
	 *            teh Data Coding Scheme value
	 */
	public FixedByteValueDataCodingProvider(byte dcs) {
		super();
		this.dcs = dcs;
	}

	@Override
	public DataCoding provide(Encoded encoded) throws DataCodingException {
		return DataCoding.parse(dcs);
	}

}
