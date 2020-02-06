package oghamtesting.ut;

import org.junit.Test;

import fr.sii.ogham.testing.assertion.sms.ExpectedAddressedPhoneNumber;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class EqualsTest {
	@Test
	public void expectedAddressedPhoneNumber() {
		EqualsVerifier.forClass(ExpectedAddressedPhoneNumber.class).usingGetClass().suppress(Warning.NONFINAL_FIELDS).verify();
	}
}
