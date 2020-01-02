package fr.sii.ogham.testing.helper.sms.jsmpp;

import java.math.BigInteger;
import java.util.Arrays;

import org.jsmpp.bean.SubmitSm;

import fr.sii.ogham.testing.helper.sms.bean.OptionalParameter;

/**
 * Adapts a JSMPP optional parameter to the Ogham abstraction
 * 
 * @author Aurélien Baudet
 *
 */
public class OptionalParameterAdapter implements OptionalParameter {
	private final SubmitSm original;
	private final short tag;

	/**
	 * @param original
	 *            the JSMPP {@link SubmitSm} request
	 * @param tag
	 *            the tag to extract
	 */
	public OptionalParameterAdapter(SubmitSm original, short tag) {
		super();
		this.original = original;
		this.tag = tag;
	}

	@Override
	public short getTag() {
		return new BigInteger(getOptionalParameterBytes(0, 2)).shortValue();
	}

	@Override
	public int getLength() {
		return new BigInteger(getOptionalParameterBytes(2, 4)).intValue();
	}

	@Override
	public byte[] getValue() {
		return getOptionalParameterBytes(4, null);
	}

	@SuppressWarnings("squid:S1168")
	private byte[] getOptionalParameterBytes(int from, Integer to) {
		org.jsmpp.bean.OptionalParameter parameter = original.getOptionalParameter(tag);
		if (parameter == null) {
			return null;
		}
		byte[] bytes = parameter.serialize();
		if (bytes == null) {
			return null;
		}
		return Arrays.copyOfRange(bytes, from, to == null ? bytes.length : to);
	}
}
