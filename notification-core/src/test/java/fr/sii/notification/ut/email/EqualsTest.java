package fr.sii.notification.ut.email;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;

import fr.sii.notification.core.message.content.MultiContent;
import fr.sii.notification.core.message.content.MultiTemplateContent;
import fr.sii.notification.core.message.content.StringContent;
import fr.sii.notification.core.message.content.TemplateContent;
import fr.sii.notification.email.attachment.Attachment;
import fr.sii.notification.email.attachment.ByteSource;
import fr.sii.notification.email.attachment.FileSource;
import fr.sii.notification.email.attachment.LookupSource;
import fr.sii.notification.email.message.Email;
import fr.sii.notification.email.message.EmailAddress;
import fr.sii.notification.email.message.Recipient;

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
		EqualsVerifier.forClass(LookupSource.class).usingGetClass().suppress(Warning.NONFINAL_FIELDS).verify();
	}
	
	@Test
	public void fileSource() {
		EqualsVerifier.forClass(FileSource.class).usingGetClass().suppress(Warning.NONFINAL_FIELDS).verify();
	}
	
	@Test
	public void byteSource() {
		EqualsVerifier.forClass(ByteSource.class).usingGetClass().suppress(Warning.NONFINAL_FIELDS).verify();
	}
}
