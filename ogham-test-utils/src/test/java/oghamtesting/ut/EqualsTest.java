package oghamtesting.ut;

import org.junit.jupiter.api.Test;

import fr.sii.ogham.testing.assertion.sms.ExpectedAddressedPhoneNumber;
import ogham.testing.nl.jqno.equalsverifier.EqualsVerifier;
import ogham.testing.nl.jqno.equalsverifier.Warning;

public class EqualsTest {
	@Test
	public void expectedAddressedPhoneNumber() {
		EqualsVerifier.forClass(ExpectedAddressedPhoneNumber.class).usingGetClass().suppress(Warning.NONFINAL_FIELDS).verify();
	}
}
