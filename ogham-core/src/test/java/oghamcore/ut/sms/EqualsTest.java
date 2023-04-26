package oghamcore.ut.sms;

import org.junit.jupiter.api.Test;

import fr.sii.ogham.core.message.fluent.SingleContentBuilder;
import fr.sii.ogham.sms.message.PhoneNumber;
import fr.sii.ogham.sms.message.Recipient;
import fr.sii.ogham.sms.message.Sender;
import fr.sii.ogham.sms.message.Sms;
import ogham.testing.nl.jqno.equalsverifier.EqualsVerifier;
import ogham.testing.nl.jqno.equalsverifier.Warning;

public class EqualsTest {
	@Test
	public void sms() {
		EqualsVerifier.forClass(Sms.class)
			.withPrefabValues(SingleContentBuilder.class, new SingleContentBuilder<>(new Sms()), new SingleContentBuilder<>(new Sms()))
			.withIgnoredFields("messageBuilder")
			.usingGetClass()
			.suppress(Warning.NONFINAL_FIELDS)
			.verify();
	}
	
	
	@Test
	public void recipient() {
		EqualsVerifier.forClass(Recipient.class).usingGetClass().suppress(Warning.NONFINAL_FIELDS).verify();
	}
	
	@Test
	public void sender() {
		EqualsVerifier.forClass(Sender.class).usingGetClass().suppress(Warning.NONFINAL_FIELDS).verify();
	}
	
	@Test
	public void phoneNumber() {
		EqualsVerifier.forClass(PhoneNumber.class).usingGetClass().suppress(Warning.NONFINAL_FIELDS).verify();
	}
}
