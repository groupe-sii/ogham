package fr.sii.notification.sms.message;

import fr.sii.notification.core.util.EqualsBuilder;
import fr.sii.notification.core.util.HashCodeBuilder;

public class PhoneNumber {
	private String number;

	public PhoneNumber(String number) {
		super();
		this.number = number;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	@Override
	public String toString() {
		return number;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(number).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("number").isEqual();
	}
	
}
