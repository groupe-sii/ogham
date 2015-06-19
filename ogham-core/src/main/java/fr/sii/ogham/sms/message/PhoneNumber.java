package fr.sii.ogham.sms.message;

import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;

/**
 * Represents a phone number. It wraps a simple string. The aim is to abstracts
 * the concept and to be able to provide other fields latter if needed.
 * 
 * @author Aurélien Baudet
 *
 */
public class PhoneNumber {
	/**
	 * The phone number as string
	 */
	private String number;

	/**
	 * Initialize the phone number with the provided number.
	 * 
	 * @param number
	 *            the phone number
	 */
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
