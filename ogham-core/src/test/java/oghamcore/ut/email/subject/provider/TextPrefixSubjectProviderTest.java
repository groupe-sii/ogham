package oghamcore.ut.email.subject.provider;

import fr.sii.ogham.core.subject.provider.TextPrefixSubjectProvider;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@LogTestInformation
public class TextPrefixSubjectProviderTest {
	private TextPrefixSubjectProvider subjectProvider;
	
	@BeforeEach
	public void setUp() {
		subjectProvider = new TextPrefixSubjectProvider();
	}
	
	@Test
	public void withPrefix() {
		Email message = new Email().content("Subject: this is the subject\nContent of the email");
		String subject = subjectProvider.provide(message);
		Assertions.assertEquals("this is the subject", subject, "subject should be 'this is the subject'");
		Assertions.assertEquals("Content of the email", message.getContent().toString(), "Content should be updated");
	}
	
	@Test
	public void trim() {
		Email message = new Email().content("Subject:    this is the subject    \nContent of the email");
		String subject = subjectProvider.provide(message);
		Assertions.assertEquals("this is the subject", subject, "subject should be 'this is the subject'");
		Assertions.assertEquals("Content of the email", message.getContent().toString(), "Content should be updated");
	}
	
	@Test
	public void emptySubject() {
		Email message = new Email().content("Subject:\nContent of the email");
		String subject = subjectProvider.provide(message);
		Assertions.assertTrue(subject.isEmpty(), "subject should be empty");
		Assertions.assertEquals("Content of the email", message.getContent().toString(), "Content should be updated");
	}
	
	@Test
	public void malformedPrefix() {
		Email message = new Email().content("subject: this is the subject\nContent of the email");
		String subject = subjectProvider.provide(message);
		Assertions.assertNull(subject, "subject should be null");
		Assertions.assertEquals("subject: this is the subject\nContent of the email", message.getContent().toString(), "Content should not be updated");
	}
	
	@Test
	public void noPrefix() {
		Email message = new Email().content("this is the subject\nContent of the email");
		String subject = subjectProvider.provide(message);
		Assertions.assertNull(subject, "subject should be null");
		Assertions.assertEquals("this is the subject\nContent of the email", message.getContent().toString(), "Content should not be updated");
	}
}
