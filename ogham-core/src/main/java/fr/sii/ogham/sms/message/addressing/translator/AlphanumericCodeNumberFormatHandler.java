package fr.sii.ogham.sms.message.addressing.translator;

import java.util.regex.Pattern;

import fr.sii.ogham.sms.message.PhoneNumber;
import fr.sii.ogham.sms.message.addressing.NumberingPlanIndicator;
import fr.sii.ogham.sms.message.addressing.TypeOfNumber;

/**
 * 
 * If the sender address is alphanumeric (contains both letters and numbers) or
 * non-numeric, TON is set to 5 and NPI to 0.
 * 
 * @author cdejonghe
 * 
 */
public class AlphanumericCodeNumberFormatHandler extends AbstractFixedPhoneNumberHandler {

	private static final Pattern NUMERIC_ONLY_PATTERN = Pattern.compile("(\\+)?[0-9]+");

	public AlphanumericCodeNumberFormatHandler() {
		super(TypeOfNumber.ALPHANUMERIC, NumberingPlanIndicator.UNKNOWN);
	}

	@Override
	public boolean supports(PhoneNumber phoneNumber) {
		return phoneNumber != null && phoneNumber.getNumber() != null && !NUMERIC_ONLY_PATTERN.matcher(phoneNumber.getNumber()).matches();
	}
}