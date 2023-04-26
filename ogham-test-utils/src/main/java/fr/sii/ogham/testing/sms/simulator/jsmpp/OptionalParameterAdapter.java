package fr.sii.ogham.testing.sms.simulator.jsmpp;

import fr.sii.ogham.testing.sms.simulator.bean.OptionalParameter;
import ogham.testing.com.cloudhopper.commons.util.HexUtil;
import ogham.testing.org.jsmpp.bean.SubmitSm;

import java.math.BigInteger;
import java.util.Arrays;

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
	public Short getTag() {
		byte[] tagValue = getOptionalParameterBytes(0, 2);
		if (tagValue == null) {
			return null;
		}
		return new BigInteger(tagValue).shortValue();
	}

	@Override
	public Integer getLength() {
		byte[] length = getOptionalParameterBytes(2, 4);
		if (length == null) {
			return null;
		}
		return new BigInteger(length).intValue();
	}

	@Override
	public byte[] getValue() {
		return getOptionalParameterBytes(4, null);
	}
	
	@Override
	public String toString() {
		return "["+getTag()+"|"+getLength()+"|"+HexUtil.toHexString(getValue())+"]";
	}

	@SuppressWarnings("squid:S1168")
	private byte[] getOptionalParameterBytes(int from, Integer to) {
		ogham.testing.org.jsmpp.bean.OptionalParameter parameter = original.getOptionalParameter(tag);
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
