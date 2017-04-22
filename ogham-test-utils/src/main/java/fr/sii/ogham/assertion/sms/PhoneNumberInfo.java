package fr.sii.ogham.assertion.sms;

public class PhoneNumberInfo {
	private final String address;
	private final byte npi;
	private final byte ton;
	
	public PhoneNumberInfo(String address, byte npi, byte ton) {
		super();
		this.address = address;
		this.npi = npi;
		this.ton = ton;
	}
	
	public String getAddress() {
		return address;
	}
	public byte getNpi() {
		return npi;
	}
	public byte getTon() {
		return ton;
	}
}