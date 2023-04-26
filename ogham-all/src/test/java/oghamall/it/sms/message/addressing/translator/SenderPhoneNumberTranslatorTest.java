package oghamall.it.sms.message.addressing.translator;

import fr.sii.ogham.sms.builder.SenderNumberBuilder;
import fr.sii.ogham.sms.exception.message.PhoneNumberTranslatorException;
import fr.sii.ogham.sms.message.PhoneNumber;
import fr.sii.ogham.sms.message.addressing.AddressedPhoneNumber;
import fr.sii.ogham.sms.message.addressing.NumberingPlanIndicator;
import fr.sii.ogham.sms.message.addressing.TypeOfNumber;
import fr.sii.ogham.sms.message.addressing.translator.PhoneNumberTranslator;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

@LogTestInformation
public class SenderPhoneNumberTranslatorTest {
	private PhoneNumberTranslator translator;
	private PhoneNumber givenPhoneNumber;

	@BeforeEach
	public void before() {
		translator = new SenderNumberBuilder()
				.format()
					.alphanumericCode(true)
					.internationalNumber(true)
					.shortCode(true)
					.build();
	}

	@Test
	public void translateNull()
			throws PhoneNumberTranslatorException {
		// given
		givenPhoneNumber = null;

		// when
		assertThrows(NullPointerException.class, () -> {
			translator.translate(null);
		});
	}

	@Test
	public void translateNoNumber()
			throws PhoneNumberTranslatorException {
		// given
		givenPhoneNumber = new PhoneNumber(null);

		// when
		AddressedPhoneNumber result = translator.translate(givenPhoneNumber);

		// then
		Assertions.assertNull(result.getNumber());
		Assertions.assertEquals(NumberingPlanIndicator.ISDN_TELEPHONE, result.getNpi());
		Assertions.assertEquals(TypeOfNumber.UNKNOWN, result.getTon());
	}

	@Test
	public void translatePureAlpha()
			throws PhoneNumberTranslatorException {
		// given
		givenPhoneNumber = new PhoneNumber("number");

		// when
		AddressedPhoneNumber result = translator.translate(givenPhoneNumber);

		// then
		Assertions.assertEquals(givenPhoneNumber.getNumber(), result.getNumber());
		Assertions.assertEquals(NumberingPlanIndicator.UNKNOWN, result.getNpi());
		Assertions.assertEquals(TypeOfNumber.ALPHANUMERIC, result.getTon());
	}

	@Test
	public void translateAlphanumeric()
			throws PhoneNumberTranslatorException {
		// given
		givenPhoneNumber = new PhoneNumber("0123456789b");

		// when
		AddressedPhoneNumber result = translator.translate(givenPhoneNumber);

		// then
		Assertions.assertEquals(givenPhoneNumber.getNumber(), result.getNumber());
		Assertions.assertEquals(NumberingPlanIndicator.UNKNOWN, result.getNpi());
		Assertions.assertEquals(TypeOfNumber.ALPHANUMERIC, result.getTon());
	}

	@Test
	public void translateSeemsInternationalButAlpha()
			throws PhoneNumberTranslatorException {
		// given
		givenPhoneNumber = new PhoneNumber("+number");

		// when
		AddressedPhoneNumber result = translator.translate(givenPhoneNumber);

		// then
		Assertions.assertEquals(givenPhoneNumber.getNumber(), result.getNumber());
		Assertions.assertEquals(NumberingPlanIndicator.UNKNOWN, result.getNpi());
		Assertions.assertEquals(TypeOfNumber.ALPHANUMERIC, result.getTon());
	}

	@Test
	public void translateSeemsShortCodeButAlpha()
			throws PhoneNumberTranslatorException {
		// given
		givenPhoneNumber = new PhoneNumber("TOTO");

		// when
		AddressedPhoneNumber result = translator.translate(givenPhoneNumber);

		// then
		Assertions.assertEquals(givenPhoneNumber.getNumber(), result.getNumber());
		Assertions.assertEquals(NumberingPlanIndicator.UNKNOWN, result.getNpi());
		Assertions.assertEquals(TypeOfNumber.ALPHANUMERIC, result.getTon());
	}

	@Test
	public void translateInternational()
			throws PhoneNumberTranslatorException {
		// given
		givenPhoneNumber = new PhoneNumber("+33618160160");

		// when
		AddressedPhoneNumber result = translator.translate(givenPhoneNumber);

		// then
		Assertions.assertEquals(givenPhoneNumber.getNumber(), result.getNumber());
		Assertions.assertEquals(NumberingPlanIndicator.ISDN_TELEPHONE, result.getNpi());
		Assertions.assertEquals(TypeOfNumber.INTERNATIONAL, result.getTon());
	}

	@Test
	public void translateShortCode()
			throws PhoneNumberTranslatorException {
		// given
		givenPhoneNumber = new PhoneNumber("6184");

		// when
		AddressedPhoneNumber result = translator.translate(givenPhoneNumber);

		// then
		Assertions.assertEquals(givenPhoneNumber.getNumber(), result.getNumber());
		Assertions.assertEquals(NumberingPlanIndicator.UNKNOWN, result.getNpi());
		Assertions.assertEquals(TypeOfNumber.NETWORK_SPECIFIC, result.getTon());
	}

	@Test
	public void translateShortCodeMax()
			throws PhoneNumberTranslatorException {
		// given
		givenPhoneNumber = new PhoneNumber("61845");

		// when
		AddressedPhoneNumber result = translator.translate(givenPhoneNumber);

		// then
		Assertions.assertEquals(givenPhoneNumber.getNumber(), result.getNumber());
		Assertions.assertEquals(NumberingPlanIndicator.UNKNOWN, result.getNpi());
		Assertions.assertEquals(TypeOfNumber.NETWORK_SPECIFIC, result.getTon());
	}

	@Test
	public void translateSeemsInternationalButShortCode()
			throws PhoneNumberTranslatorException {
		// given
		givenPhoneNumber = new PhoneNumber("+618");

		// when
		AddressedPhoneNumber result = translator.translate(givenPhoneNumber);

		// then
		Assertions.assertEquals(givenPhoneNumber.getNumber(), result.getNumber());
		Assertions.assertEquals(NumberingPlanIndicator.UNKNOWN, result.getNpi());
		Assertions.assertEquals(TypeOfNumber.NETWORK_SPECIFIC, result.getTon());
	}

	@Test
	public void translateSeemsInternationalBut()
			throws PhoneNumberTranslatorException {
		// given
		givenPhoneNumber = new PhoneNumber("33618160160");

		// when
		AddressedPhoneNumber result = translator.translate(givenPhoneNumber);

		// then
		Assertions.assertEquals(givenPhoneNumber.getNumber(), result.getNumber());
		Assertions.assertEquals(NumberingPlanIndicator.ISDN_TELEPHONE, result.getNpi());
		Assertions.assertEquals(TypeOfNumber.UNKNOWN, result.getTon());
	}
}
