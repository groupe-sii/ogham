package fr.sii.ogham.testing.sms.simulator.jsmpp;

import java.math.BigInteger;
import java.util.Arrays;

import org.jsmpp.bean.SubmitSm;

import com.cloudhopper.commons.util.HexUtil;

import fr.sii.ogham.testing.sms.simulator.bean.OptionalParameter;

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
		byte[] tag = getOptionalParameterBytes(0, 2);
		if (tag == null) {
			return null;
		}
		return new BigInteger(tag).shortValue();
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
