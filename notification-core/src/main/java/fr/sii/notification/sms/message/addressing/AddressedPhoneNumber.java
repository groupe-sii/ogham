package fr.sii.notification.sms.message.addressing;

import fr.sii.notification.sms.message.PhoneNumber;


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
}
