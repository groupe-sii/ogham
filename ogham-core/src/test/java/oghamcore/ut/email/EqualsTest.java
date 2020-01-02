package oghamcore.ut.email;

import org.junit.Test;

import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.message.content.MultiTemplateContent;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.resource.ByteResource;
import fr.sii.ogham.core.resource.FileResource;
import fr.sii.ogham.core.resource.LookupResource;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.message.EmailAddress;
import fr.sii.ogham.email.message.Recipient;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class EqualsTest {
	@Test
	public void email() {
		EqualsVerifier.forClass(Email.class).usingGetClass().suppress(Warning.NONFINAL_FIELDS).verify();
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
	
	@Test
	public void stringContent() {
		EqualsVerifier.forClass(StringContent.class).usingGetClass().suppress(Warning.NONFINAL_FIELDS).verify();
	}
	
	@Test
	public void templateContent() {
		EqualsVerifier.forClass(TemplateContent.class).usingGetClass().suppress(Warning.NONFINAL_FIELDS).verify();
	}
	
	@Test
	public void multiContent() {
		EqualsVerifier.forClass(MultiContent.class).usingGetClass().suppress(Warning.NONFINAL_FIELDS).verify();
	}
	
	@Test
	public void multiTemplateContent() {
		EqualsVerifier.forClass(MultiTemplateContent.class).usingGetClass().suppress(Warning.NONFINAL_FIELDS).verify();
	}
	
	@Test
	public void lookupSource() {
		EqualsVerifier.forClass(LookupResource.class).usingGetClass().suppress(Warning.NONFINAL_FIELDS).verify();
	}
	
	@Test
	public void fileSource() {
		EqualsVerifier.forClass(FileResource.class).usingGetClass().suppress(Warning.NONFINAL_FIELDS).verify();
	}
	
	@Test
	public void byteSource() {
		EqualsVerifier.forClass(ByteResource.class).usingGetClass().suppress(Warning.NONFINAL_FIELDS).verify();
	}
}
