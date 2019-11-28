package fr.sii.ogham.helper.sms.bean;

/**
 * Just provide field values
 * 
 * @author Aurélien Baudet
 *
 */
public class SimpleAddress implements Address {
	private final String address;
	private final byte ton;
	private final byte npi;

	/**
	 * @param address
	 *            the address (phone number, IP address or anything else)
	 * @param ton
	 *            the {@link TypeOfNumber}
	 * @param npi
	 *            the {@link NumberingPlanIndicator}
	 */
	public SimpleAddress(String address, byte ton, byte npi) {
		super();
		this.address = address;
		this.ton = ton;
		this.npi = npi;
	}

	@Override
	public byte getTon() {
		return ton;
	}

	@Override
	public byte getNpi() {
		return npi;
	}

	@Override
	public String getAddress() {
		return address;
	}

}
