package fr.sii.ogham.testing.assertion.sms;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ExpectedAddressedPhoneNumber {
	private final String number;
	
	/** The type of number (TON); */
	private final byte ton;

	/** <li>The numbering plan identification (NPI). */
	private final byte npi;

	/**
	 * Initializes the phone number with the given number, TON and NPI.
	 * 
	 * @param number
	 *            Phone number in text format
	 * @param ton
	 *            Type of number
	 * @param npi
	 *            Numbering plan identification
	 */
	public ExpectedAddressedPhoneNumber(String number, byte ton, byte npi) {
		super();
		this.number = number;
		this.ton = ton;
		this.npi = npi;
	}

	public String getNumber() {
		return number;
	}

	public byte getTon() {
		return ton;
	}

	public byte getNpi() {
		return npi;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (getNumber() != null && !getNumber().isEmpty()) {
			builder.append(getNumber()).append("|TON:").append(ton).append("|NPI:").append(npi);
		}
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getNumber()).append(ton).append(npi).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		ExpectedAddressedPhoneNumber rhs = (ExpectedAddressedPhoneNumber) obj;
		return new EqualsBuilder().append(number, rhs.number).append(ton, rhs.ton).append(npi, rhs.npi).isEquals();
	}
}
