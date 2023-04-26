package oghamcore.ut.core;

import org.junit.jupiter.api.Test;

import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.message.content.MultiTemplateContent;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.resource.ByteResource;
import fr.sii.ogham.core.resource.FileResource;
import fr.sii.ogham.core.resource.LookupResource;
import ogham.testing.nl.jqno.equalsverifier.EqualsVerifier;
import ogham.testing.nl.jqno.equalsverifier.Warning;

public class EqualsTest {
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
