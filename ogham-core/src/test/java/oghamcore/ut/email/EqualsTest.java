package oghamcore.ut.email;

import org.junit.jupiter.api.Test;

import fr.sii.ogham.core.message.fluent.SingleContentBuilder;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.message.EmailAddress;
import fr.sii.ogham.email.message.Recipient;
import fr.sii.ogham.email.message.fluent.AttachBuilder;
import fr.sii.ogham.email.message.fluent.BodyBuilder;
import fr.sii.ogham.email.message.fluent.EmbedBuilder;
import ogham.testing.nl.jqno.equalsverifier.EqualsVerifier;
import ogham.testing.nl.jqno.equalsverifier.Warning;

public class EqualsTest {
	@Test
	public void email() {
		EqualsVerifier.forClass(Email.class)
			.withPrefabValues(SingleContentBuilder.class, new SingleContentBuilder<>(new Email()), new SingleContentBuilder<>(new Email()))
			.withPrefabValues(BodyBuilder.class, new BodyBuilder(new Email()), new BodyBuilder(new Email()))
			.withPrefabValues(AttachBuilder.class, new AttachBuilder(new Email()), new AttachBuilder(new Email()))
			.withPrefabValues(EmbedBuilder.class, new EmbedBuilder(new Email()), new EmbedBuilder(new Email()))
			.withIgnoredFields("htmlBuilder", "textBuilder", "bodyBuilder", "attachBuilder", "embedBuilder")
			.usingGetClass()
			.suppress(Warning.NONFINAL_FIELDS)
			.verify();
	}
	
	@Test
	public void address() {
		EqualsVerifier.forClass(EmailAddress.class).usingGetClass().suppress(Warning.NONFINAL_FIELDS).verify();
	}
	
	@Test
	public void recipient() {
		EqualsVerifier.forClass(Recipient.class).usingGetClass().suppress(Warning.NONFINAL_FIELDS).verify();
	}
	
	@Test
	public void attachment() {
		EqualsVerifier.forClass(Attachment.class).usingGetClass().suppress(Warning.NONFINAL_FIELDS).verify();
	}
}
