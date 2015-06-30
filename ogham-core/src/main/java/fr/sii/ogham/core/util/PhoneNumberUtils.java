package fr.sii.ogham.core.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import fr.sii.ogham.core.exception.util.PhoneNumberException;

public class PhoneNumberUtils {
	public static String toInternational(String phoneNumber) throws PhoneNumberException {
		try {
			PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
			// TODO: try to find country
			PhoneNumber number = phoneUtil.parse(phoneNumber, null);
			return phoneUtil.format(number, PhoneNumberFormat.E164);
		} catch (NumberParseException e) {
			throw new PhoneNumberException("Failed to transform phone number to international format", e);
		}
	}
	
	private PhoneNumberUtils() {
		super();
	}
}
