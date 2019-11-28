package fr.sii.ogham.assertion.sms;

import fr.sii.ogham.helper.sms.bean.Address;
import fr.sii.ogham.helper.sms.bean.NumberingPlanIndicator;
import fr.sii.ogham.helper.sms.bean.TypeOfNumber;

/**
 * Represents a phone number with {@link TypeOfNumber} and
 * {@link NumberingPlanIndicator} information.
 * 
 * @author Aurélien Baudet
 *
 */
public class PhoneNumberInfo {
	private final String address;
	private final byte npi;
	private final byte ton;

	/**
	 * Initializes with an {@link Address}
	 * 
	 * @param address
	 *            the address (phone number + {@link NumberingPlanIndicator} +
	 *            {@link TypeOfNumber})
	 */
	public PhoneNumberInfo(Address address) {
		this(address.getAddress(), address.getNpi(), address.getTon());
	}

	/**
	 * @param address
	 *            the phone number
	 * @param npi
	 *            the {@link NumberingPlanIndicator}
	 * @param ton
	 *            the {@link TypeOfNumber}
	 */
	public PhoneNumberInfo(String address, byte npi, byte ton) {
		super();
		this.address = address;
		this.npi = npi;
		this.ton = ton;
	}

	/**
	 * @return the address (may be phone number, IP address or anything else)
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @return the {@link NumberingPlanIndicator}
	 */
	public byte getNpi() {
		return npi;
	}

	/**
	 * @return the {@link TypeOfNumber}
	 */
	public byte getTon() {
		return ton;
	}
}