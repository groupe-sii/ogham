package fr.sii.ogham.sms.message.addressing;

import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;
import fr.sii.ogham.sms.message.PhoneNumber;

/**
 * A phone number plus its addressing information (as described in GSM 03.40).
 * <ul>
 * <li>The type of number (TON)</li>
 * <li>The numbering plan identification (NPI)</li>
 * </ul>
 * 
 * @author cdejonghe
 * 
 */
public class AddressedPhoneNumber extends PhoneNumber {
	/** The type of number (TON); */
	private final TypeOfNumber ton;

	/** <li>The numbering plan identification (NPI). */
	private final NumberingPlanIndicator npi;

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
	public AddressedPhoneNumber(String number, TypeOfNumber ton, NumberingPlanIndicator npi) {
		super(number);
		this.ton = ton;
		this.npi = npi;
	}

	public TypeOfNumber getTon() {
		return ton;
	}

	public NumberingPlanIndicator getNpi() {
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
		return new HashCodeBuilder().append(getNumber(), ton, npi).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("number", "ton", "npi").isEqual();
	}

}
